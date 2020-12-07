package org.xapps.apps.weatherx.viewmodels

import android.content.Context
import android.location.Geocoder
import androidx.databinding.Bindable
import androidx.databinding.ObservableArrayList
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import org.xapps.apps.weatherx.BR
import org.xapps.apps.weatherx.R
import org.xapps.apps.weatherx.services.local.PlaceDao
import org.xapps.apps.weatherx.services.models.*
import org.xapps.apps.weatherx.services.utils.NetworkTracker
import org.xapps.apps.weatherx.services.repositories.WeatherRepository
import org.xapps.apps.weatherx.services.settings.SettingsService
import org.xapps.apps.weatherx.services.utils.DateUtils
import org.xapps.apps.weatherx.services.utils.GpsTracker
import org.xapps.apps.weatherx.services.utils.KotlinUtils.timerFlow
import org.xapps.apps.weatherx.views.utils.Message
import org.xapps.apps.weatherx.views.utils.Utilities
import timber.log.Timber
import java.util.*


class HomeViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    private val settings: SettingsService,
    private val networkTracker: NetworkTracker,
    private val session: Session,
    private val gpsTracker: GpsTracker,
    private val geocoder: Geocoder,
    private val placeDao: PlaceDao,
    private val weatherRepository: WeatherRepository
) : ObservableViewModel() {

    private val workingEmitter: MutableLiveData<Boolean> = MutableLiveData()
    private val messageEmitter: MutableLiveData<Message> = MutableLiveData()

    private var jobNetworkTracker: Job? = null
    private var jobGpsTrackerErrors: Job? = null
    private var jobGpsTrackerUpdates: Job? = null
    private var jobWeatherInfo: Job? = null

    fun working(): LiveData<Boolean> = workingEmitter
    fun message(): LiveData<Message> = messageEmitter

    @get:Bindable
    var place: Place? = null
        private set(value) {
            field = value
            notifyPropertyChanged(BR.place)
        }

    @get:Bindable
    var currentWeather: Current? = null
        private set(value) {
            field = value
            notifyPropertyChanged(BR.currentWeather)
        }

    val hourlyWeather = ObservableArrayList<Hourly>()
    val dailyWeather = ObservableArrayList<Daily>()

    init {
        session.currentLanguage = Locale.getDefault().language
        jobNetworkTracker = viewModelScope.launch {
            networkTracker.connectivityWatcher()
                .collect { isConnectionAvailable ->
                    if (isConnectionAvailable) {
                        startWeatherMonitor()
                    }
                }
        }
        networkTracker.start()
    }

    fun startWeatherMonitor() {
        Timber.i("AppLogger - Start weather monitor")
        val lastPlaceId = settings.lastPlaceMonitored()
        if (lastPlaceId == Place.CURRENT_PLACE_ID) {
            Timber.i("AppLogger - Current place was the last monitored place")
            startWeatherMonitorCurrentPlace()
        } else {
            Timber.i("AppLogger - A custom place was the last monitored place")
            viewModelScope.launch {
                placeDao.placeAsync(lastPlaceId)
                    .collect { customPlace ->
                        if(customPlace != null) {
                            startWeatherMonitorCustomPlace(customPlace)
                        } else {
                            Timber.i("AppLogger - The request place couldn't be found in the database")
                            messageEmitter.postValue(Message.error(context.getString(R.string.error_retrieving_place_from_db)))
                        }
                    }
            }
        }
    }

    fun startWeatherMonitorCustomPlace(customPlace: Place) {
        stopMonitors()
        settings.setLastPlaceMonitored(customPlace.id)
        place = customPlace
        session.currentPlace = customPlace

        if (networkTracker.isConnectedToInternet()) {
            Timber.i("AppLogger - Internet gateway detected")
            scheduleWeatherInfo()
        } else {
            Timber.i("AppLogger - Internet connection not found")
            messageEmitter.postValue(Message.error(context.getString(R.string.internet_not_detected)))
        }
    }

    fun startWeatherMonitorCurrentPlace() {
        stopMonitors()
        if (networkTracker.isConnectedToInternet()) {
            Timber.i("AppLogger - Internet gateway detected")
            jobGpsTrackerErrors = viewModelScope.launch {
                gpsTracker.watchError()
                    .collect { error ->
                        Timber.i("AppLogger - GpsTracker has returned error $error")
                        if(currentWeather == null && session.currentPlace == null) {
                            val errorMessage = when (error) {
                                GpsTracker.Error.INVALID_LOCATION -> context.getString(R.string.gps_disabled)
                                GpsTracker.Error.PROVIDER_ERROR -> context.getString(R.string.location_provider_error)
                                else -> context.getString(R.string.location_unknown_error)
                            }
                            messageEmitter.postValue(Message.error(errorMessage))
                        }
                    }
            }
            jobGpsTrackerUpdates = viewModelScope.launch {
                gpsTracker.watchUpdater()
                    .collect { location ->
                        Timber.i("AppLogger - Location recieved from tracker $location")
                        monitorCurrentPlace()
                    }
            }
            gpsTracker.validateUpdates = true
            gpsTracker.start()
            monitorCurrentPlace()
        } else {
            Timber.i("AppLogger - Internet connection not found")
            messageEmitter.postValue(Message.error(context.getString(R.string.internet_not_detected)))
        }
    }

    private fun monitorCurrentPlace() {
        viewModelScope.launch {
            gpsTracker.location?.let { location ->
                try {
                    val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    addresses.isNotEmpty().let {
                        val address = addresses[0]
                        place = Place(
                            id = Place.CURRENT_PLACE_ID,
                            country = address.countryName,
                            city = address.locality,
                            latitude = location.latitude,
                            longitude = location.longitude,
                            code = address.countryCode
                        )
                        session.currentPlace = place
                        placeDao.insertAsync(place!!)

                        scheduleWeatherInfo()
                    }
                } catch(e: Exception) {
                    Timber.i(e, "Exception captured")
                }
            } ?: run {
                Timber.i("AppLogger - GPS tracker doesn't have location yet, getting location from database")
                placeDao.placeAsync(Place.CURRENT_PLACE_ID)
                    .catch {
                        it.printStackTrace()
                    }
                    .collect { currentPlace ->
                        if(currentPlace != null) {
                            place = currentPlace
                            session.currentPlace = place

                            scheduleWeatherInfo()
                        } else {
                            Timber.i("AppLogger - There isn't a place saved in database")
                            messageEmitter.postValue(Message.error(context.getString(R.string.gps_disabled)))
                        }
                    }
            }
        }
    }

    private fun scheduleWeatherInfo() {
        stopJobWeatherScheduler()
        jobWeatherInfo = viewModelScope.launch {
            timerFlow(interval = 1000 * 60 * 10).collect {
                if (networkTracker.isConnectedToInternet()) {
                    viewModelScope.launch {
                        weatherRepository.currentHourlyDaily()
                            .catch { exception ->
                                Timber.e(exception)
                                messageEmitter.postValue(Message.error(exception.localizedMessage))
                            }
                            .collect { weather ->
                                weather?.current?.let {
                                    settings.setLastTemperature(it.temperature)
                                    settings.setLastWasVisible(Utilities.isVisible(it.visibility))
                                    settings.setLastWasDayLight(
                                        DateUtils.isDayLight(
                                            sunrise = it.sunrise,
                                            sunset = it.sunset,
                                            datetime = it.datetime
                                        )
                                    )
                                    if (it.conditions.isNotEmpty()) {
                                        settings.setLastConditionCode(it.conditions[0].id)
                                    }
                                }
                                currentWeather = weather?.current
                                weather?.hourly?.let {
                                    hourlyWeather.clear()
                                    hourlyWeather.addAll(it)
                                }
                                weather?.daily?.let {
                                    dailyWeather.clear()
                                    dailyWeather.addAll(it)
                                }
                                messageEmitter.postValue(Message.ready())
                            }
                    }
                } else {
                    Timber.i("AppLogger - Internet connection not found, skipping weather request")
                }
            }
        }
    }

    fun stopMonitors() {
        gpsTracker.stop()
        stopJobWeatherScheduler()
        stopJobWeatherScheduler()
    }

    private fun stopJobWeatherScheduler() {
        if (jobWeatherInfo?.isActive == true) {
            Timber.i("AppLogger - Stopping weather job")
            jobWeatherInfo?.cancel()
            jobWeatherInfo = null
        }
    }

    private fun stopJobGpsTrackerScheduler() {
        if (jobGpsTrackerErrors?.isActive == true) {
            Timber.i("AppLogger - Stopping gps tracker errors job")
            jobGpsTrackerErrors?.cancel()
            jobGpsTrackerErrors = null
        }
        if (jobGpsTrackerUpdates?.isActive == true) {
            Timber.i("AppLogger - Stopping gps tracker updates job")
            jobGpsTrackerUpdates?.cancel()
            jobGpsTrackerUpdates = null
        }
    }

    fun lastConditionCode(): Int = settings.lastConditionCode()

    fun lastWasDayLight(): Boolean = settings.lastWasDayLight()

    fun lastTemperature(): Double = settings.lastTemperature()

    fun useMetricSystem(): Boolean = settings.useMetricSystem()

    fun lastWasThereVisibility(): Boolean = settings.lastWasThereVisibility()

}