package org.xapps.apps.weatherx.viewmodels

import android.location.Geocoder
import androidx.databinding.Bindable
import androidx.databinding.ObservableArrayList
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.xapps.apps.weatherx.BR
import org.xapps.apps.weatherx.services.local.PlaceDao
import org.xapps.apps.weatherx.services.models.*
import org.xapps.apps.weatherx.services.repositories.WeatherRepository
import org.xapps.apps.weatherx.services.settings.SettingsService
import org.xapps.apps.weatherx.services.utils.DateUtils
import org.xapps.apps.weatherx.services.utils.GpsTracker
import org.xapps.apps.weatherx.services.utils.KotlinUtils.timerFlow
import timber.log.Timber
import java.util.*


class HomeViewModel @ViewModelInject constructor(
    val settings: SettingsService,
    private val session: Session,
    private val gpsTracker: GpsTracker,
    private val geocoder: Geocoder,
    private val placeDao: PlaceDao,
    private val weatherRepository: WeatherRepository
) : ObservableViewModel() {

    private val workingEmitter: MutableLiveData<Boolean> = MutableLiveData()
    private val errorEmitter: MutableLiveData<String> = MutableLiveData()
    private val conditionGroupEmitter: MutableLiveData<Condition.Group> = MutableLiveData()
    private val readyEmitter: MutableLiveData<Boolean> = MutableLiveData()

    private var jobGpsTracker: Job? = null
    private var jobWeatherInfo: Job? = null

    fun watchWorking(): LiveData<Boolean> = workingEmitter
    fun watchError(): LiveData<String> = errorEmitter
    fun watchConditionGroup(): LiveData<Condition.Group> = conditionGroupEmitter
    fun watchReady(): LiveData<Boolean> = readyEmitter

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
    }

    fun prepareMonitoring() {
        jobGpsTracker = viewModelScope.launch {
            gpsTracker.watchUpdater()
                .collect { location ->
                    monitorSessionPlace(ignoreCustomPlaces = true)
                }
        }
        gpsTracker.validateUpdates = true
        gpsTracker.start()
        monitorSessionPlace(ignoreCurrentPlace = true)
    }

    private fun monitorSessionPlace(
        ignoreCurrentPlace: Boolean = false,
        ignoreCustomPlaces: Boolean = false
    ) {
        viewModelScope.launch {
            val lastPlaceId = settings.lastPlaceMonitored()
            if (lastPlaceId == Place.CURRENT_PLACE_ID) {
                if(!ignoreCurrentPlace) {
                    gpsTracker.location?.let { location ->
                        val addresses =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)
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

                            scheduleWeatherInfo()
                        }
                    }
                }
            } else if (!ignoreCustomPlaces) {
                placeDao.placeAsync(lastPlaceId)
                    .collect { place ->
                        this@HomeViewModel.place = place
                        session.currentPlace = place
                    }

                scheduleWeatherInfo()
            }
        }
    }

    fun monitorPlace(place: Place) {
        settings.setLastPlaceMonitored(place.id)
        session.currentPlace = place
        scheduleWeatherInfo()
    }

    fun scheduleWeatherInfo() {
        stopJobWeatherScheduler()
        jobWeatherInfo = viewModelScope.launch {
            timerFlow(interval = 1000 * 60 * 10).collect {
                viewModelScope.launch {
                    weatherRepository.currentHourlyDaily()
                        .catch { exception ->
                            Timber.e(exception, "Exception captured")
                            readyEmitter.postValue(false)
                        }
                        .collect { weather ->
                            conditionGroupEmitter.postValue(Condition.Group.fromName(
                                weather?.current?.let {
                                    settings.setLastTemperature(it.temperature)
                                    settings.setLastWasDayLight(DateUtils.isDayLight(sunrise = it.sunrise, sunset = it.sunset, datetime = it.datetime))
                                    if(it.conditions.isNotEmpty()) {
                                        settings.setLastConditionCode(it.conditions[0].id)
                                        it.conditions[0].main
                                    }
                                    else
                                        null
                                }
                            ))
                            currentWeather = weather?.current
                            weather?.hourly?.let {
                                hourlyWeather.clear()
                                hourlyWeather.addAll(it)
                            }
                            weather?.daily?.let {
                                dailyWeather.clear()
                                dailyWeather.addAll(it)
                            }
                            readyEmitter.postValue(true)
                        }
                }
            }
        }
    }

    fun stopJobWeatherScheduler() {
        if (jobWeatherInfo?.isActive == true) {
            jobWeatherInfo?.cancel()
            jobWeatherInfo = null
        }
    }

    fun stopJobGpsTrackerScheduler() {
        if(jobGpsTracker?.isActive == true) {
            jobGpsTracker?.cancel()
            jobGpsTracker = null
        }
    }

    fun lastConditionCode(): Int = settings.lastConditionCode()

    fun lastWasDayLight(): Boolean = settings.lastWasDayLight()

    fun lastTemperature(): Double = settings.lastTemperature()

    fun useMetric(): Boolean = settings.useMetric()

}