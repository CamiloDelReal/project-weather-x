package org.xapps.apps.weatherx.viewmodels

import android.content.Context
import android.location.Geocoder
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.xapps.apps.weatherx.R
import org.xapps.apps.weatherx.services.local.PlaceDao
import org.xapps.apps.weatherx.services.models.*
import org.xapps.apps.weatherx.services.repositories.WeatherRepository
import org.xapps.apps.weatherx.services.repositories.SettingsRepository
import org.xapps.apps.weatherx.services.utils.DateUtils
import org.xapps.apps.weatherx.services.utils.GpsTracker
import org.xapps.apps.weatherx.services.utils.KotlinUtils.timerFlow
import org.xapps.apps.weatherx.services.utils.NetworkTracker
import org.xapps.apps.weatherx.views.utils.Message
import org.xapps.apps.weatherx.views.utils.Utilities
import timber.log.Timber
import java.util.*
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settings: SettingsRepository,
    private val networkTracker: NetworkTracker,
    private val session: Session,
    private val gpsTracker: GpsTracker,
    private val geocoder: Geocoder,
    private val placeDao: PlaceDao,
    private val weatherRepository: WeatherRepository
) : ObservableViewModel() {

    private val _workingFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _messageFlow: MutableSharedFlow<Message> = MutableSharedFlow(replay = 1)

    val workingFlow: StateFlow<Boolean> = _workingFlow
    val messageFlow: SharedFlow<Message> = _messageFlow

    private var jobNetworkTracker: Job? = null
    private var jobGpsTrackerErrors: Job? = null
    private var jobGpsTrackerUpdates: Job? = null
    private var jobWeatherInfo: Job? = null

    val place: ObservableField<Place?> = ObservableField()

    val currentWeather: ObservableField<Current?> = ObservableField()

    val hourlyWeather = ObservableArrayList<Hourly>()
    val dailyWeather = ObservableArrayList<Daily>()

    private var internetAvailable = networkTracker.isConnectedToInternet()

    init {
        session.currentLanguage = Locale.getDefault().language
        jobNetworkTracker = viewModelScope.launch {
            networkTracker.connectivityWatcher()
                .collect { isConnectionAvailable ->
                    if (isConnectionAvailable && isConnectionAvailable != internetAvailable) {
                        Timber.i("Network tracker has returned connection available")
                        startWeatherMonitor()
                    }
                    internetAvailable = isConnectionAvailable
                }
        }
        networkTracker.start()
    }

    fun startWeatherMonitor() {
        Timber.i("AppLogger - Start weather monitor")
        viewModelScope.launch {
            val lastPlaceId = settings.lastPlaceMonitoredValue()
            if (lastPlaceId == Place.CURRENT_PLACE_ID) {
                Timber.i("AppLogger - Current place was the last monitored place")
                startWeatherMonitorCurrentPlace()
            } else {
                Timber.i("AppLogger - A custom place was the last monitored place")
                viewModelScope.launch {
                    placeDao.placeAsync(lastPlaceId)
                        .collect { customPlace ->
                            if (customPlace != null) {
                                startWeatherMonitorCustomPlace(customPlace)
                            } else {
                                Timber.i("AppLogger - The request place couldn't be found in the database")
                                _messageFlow.tryEmit(Message.error(context.getString(R.string.error_retrieving_place_from_db)))
                            }
                        }
                }
            }
        }
    }

    private fun startWeatherMonitorCustomPlace(customPlace: Place) {
        stopMonitors()
        viewModelScope.launch {
            settings.setLastPlaceMonitored(customPlace.id)
        }
        place.set(customPlace)
        session.currentPlace = customPlace

        if (networkTracker.isConnectedToInternet()) {
            Timber.i("AppLogger - Internet gateway detected")
            scheduleWeatherInfo()
        } else {
            Timber.i("AppLogger - Internet connection not found")
            _messageFlow.tryEmit(Message.error(context.getString(R.string.internet_not_detected)))
        }
    }

    private fun startWeatherMonitorCurrentPlace() {
        stopMonitors()
        if (networkTracker.isConnectedToInternet()) {
            Timber.i("AppLogger - Internet gateway detected")
            jobGpsTrackerErrors = viewModelScope.launch {
                gpsTracker.errorFlow.collect { error ->
                    Timber.i("AppLogger - GpsTracker has returned error $error")
                    if(currentWeather.get() == null && session.currentPlace == null) {
                        val errorMessage = when (error) {
                            GpsTracker.Error.INVALID_LOCATION -> context.getString(R.string.gps_disabled)
                            GpsTracker.Error.PROVIDER_ERROR -> context.getString(R.string.location_provider_error)
                            else -> context.getString(R.string.location_unknown_error)
                        }
                        _messageFlow.tryEmit(Message.error(errorMessage))
                    }
                }
            }
            jobGpsTrackerUpdates = viewModelScope.launch {
                Timber.i("AppLogger - About to start to collect updates from gps tracker")
                gpsTracker.updaterFlow.collect { location ->
                    Timber.i("AppLogger - Location recieved from tracker $location")
                    monitorCurrentPlace()
                }
            }
            gpsTracker.validateUpdates = true
            gpsTracker.start()
            monitorCurrentPlace()
        } else {
            Timber.i("AppLogger - Internet connection not found")
            _messageFlow.tryEmit(Message.error(context.getString(R.string.internet_not_detected)))
        }
    }

    private fun monitorCurrentPlace() {
        viewModelScope.launch(Dispatchers.IO) {
            gpsTracker.location?.let { location ->
                try {
                    val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    addresses.isNotEmpty().let {
                        val address = addresses[0]
                        val currentPlace = Place(
                            id = Place.CURRENT_PLACE_ID,
                            country = address.countryName,
                            city = address.locality,
                            latitude = location.latitude,
                            longitude = location.longitude,
                            code = address.countryCode
                        )
                        place.set(currentPlace)
                        session.currentPlace = currentPlace
                        placeDao.insertAsync(currentPlace)

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
                            place.set(currentPlace)
                            session.currentPlace = currentPlace

                            scheduleWeatherInfo()
                        } else {
                            Timber.i("AppLogger - There isn't a place saved in database")
                            _messageFlow.tryEmit(Message.error(context.getString(R.string.gps_disabled)))
                        }
                    }
            }
        }
    }

    fun resetScheduleWeatherInfo() {
        scheduleWeatherInfo()
    }

    private fun scheduleWeatherInfo() {
        stopJobWeatherScheduler()
        jobWeatherInfo = viewModelScope.launch {
            timerFlow(interval = 1000 * 60 * 10).collect {
                if (networkTracker.isConnectedToInternet()) {
                    val weather = weatherRepository.currentHourlyDaily()
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
                    currentWeather.set(weather?.current)
                    weather?.hourly?.let {
                        hourlyWeather.clear()
                        hourlyWeather.addAll(it)
                    }
                    weather?.daily?.let {
                        dailyWeather.clear()
                        dailyWeather.addAll(it)
                    }
                    _messageFlow.tryEmit(Message.ready())
                } else {
                    Timber.i("AppLogger - Internet connection not found, skipping weather request")
                }
            }
        }
    }

    fun stopMonitors() {
        gpsTracker.stop()
        stopJobWeatherScheduler()
        stopJobGpsTrackerScheduler()
    }

    private fun stopJobWeatherScheduler() {
        if(jobWeatherInfo?.isActive == true) {
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

    fun useMetricSystem(): Flow<Boolean> = settings.useMetricSystem()

    fun isDarkModeOn(): Flow<Boolean> = settings.isDarkModeOn()

    fun lastConditionCode(): Int = runBlocking { settings.lastConditionCodeValue() }

    fun lastWasDayLight(): Boolean = runBlocking { settings.lastWasDayLightValue() }

    fun lastTemperature(): Double = runBlocking { settings.lastTemperatureValue() }

    fun useMetricSystemValue(): Boolean = runBlocking { settings.useMetricSystemValue() }

    fun lastWasThereVisibility(): Boolean = runBlocking { settings.lastWasThereVisibilityValue() }

}