package org.xapps.apps.weatherx.services.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xapps.apps.weatherx.services.models.*
import org.xapps.apps.weatherx.services.remote.WeatherApi
import timber.log.Timber
import java.util.*
import javax.inject.Inject


class WeatherRepository @Inject constructor(
    private val session: Session,
    private val weatherApi: WeatherApi,
    private val settings: SettingsRepository
) {

    suspend fun current(): Current? = withContext(Dispatchers.IO) {
        val weather = weatherApi.current(
            session.apiKey,
            session.currentPlace!!.latitude,
            session.currentPlace!!.longitude,
            session.currentLanguage!!,
            if (settings.useMetricSystemValue()) WeatherApi.UNIT_METRIC else WeatherApi.UNIT_IMPERIAL
        )
        fixDatetime(weather)
        weather.current?.useMetricSystem = settings.useMetricSystemValue()
        weather.current
    }

    suspend fun minutely(): List<Minutely>? = withContext(Dispatchers.IO){
        val weather = weatherApi.minutely(
            session.apiKey,
            session.currentPlace!!.latitude,
            session.currentPlace!!.longitude,
            session.currentLanguage!!,
            if (settings.useMetricSystemValue()) WeatherApi.UNIT_METRIC else WeatherApi.UNIT_IMPERIAL
        )
        fixDatetime(weather)
        val startMinutelyIndex = findNextMinuteIndex(weather.minutely)
        weather.minutely = weather.minutely?.subList(
            startMinutelyIndex,
            startMinutelyIndex + settings.minutelyVisibleItemsSizeValue()
        )
        weather.minutely
    }

    suspend fun hourly(): List<Hourly>? = withContext(Dispatchers.IO) {
        val weather = weatherApi.hourly(
            session.apiKey,
            session.currentPlace!!.latitude,
            session.currentPlace!!.longitude,
            session.currentLanguage!!,
            if (settings.useMetricSystemValue()) WeatherApi.UNIT_METRIC else WeatherApi.UNIT_IMPERIAL
        )
        fixDatetime(weather)
        val startHourlyIndex = findNextHourIndex(weather.hourly)
        weather.hourly = weather.hourly?.subList(
            startHourlyIndex,
            startHourlyIndex + settings.hourlyVisibleItemsSizeValue()
        )
            ?.onEach {
                it.useMetricSystem = settings.useMetricSystemValue()
            }
        weather.hourly
    }

    suspend fun daily(): List<Daily>? = withContext(Dispatchers.IO) {
        val weather = weatherApi.daily(
            session.apiKey,
            session.currentPlace!!.latitude,
            session.currentPlace!!.longitude,
            session.currentLanguage!!,
            if (settings.useMetricSystemValue()) WeatherApi.UNIT_METRIC else WeatherApi.UNIT_IMPERIAL
        )
        fixDatetime(weather)
        val startDailyIndex = findNextDayIndex(weather.daily)
        weather.daily = weather.daily?.subList(
            startDailyIndex,
            startDailyIndex + settings.dailyVisibleItemsSizeValue()
        )
            ?.onEach { it.useMetricSystem = settings.useMetricSystemValue() }
        weather.daily
    }

    suspend fun currentHourlyDaily(): Weather = withContext(Dispatchers.IO) {
        Timber.tag("WeatherRepository").i("currentHourlyDaily")
        val weather = weatherApi.currentHourlyDaily(
            session.apiKey,
            session.currentPlace!!.latitude,
            session.currentPlace!!.longitude,
            session.currentLanguage!!,
            if (settings.useMetricSystemValue()) WeatherApi.UNIT_METRIC else WeatherApi.UNIT_IMPERIAL
        )
        weather.current?.useMetricSystem = settings.useMetricSystemValue()
        fixDatetime(weather)
        val currentDay = findCurrentDay(weather.daily)
        var index = currentDay?.let { day ->
            weather.current?.minimumTemperature = day.temperature.minimum
            weather.current?.maximumTemperature = day.temperature.maximum
            weather.current?.probabilityOfPrecipitation = day.probabilityOfPrecipitation
            weather.daily!!.indexOf(day)
        } ?: 0
        val calendar: Calendar? = currentDay?.let { day ->
            Calendar.getInstance().apply { timeInMillis = day.datetime }
        }
        val startHourlyIndex = findNextHourIndex(weather.hourly)
        weather.hourly = weather.hourly?.subList(
            startHourlyIndex,
            startHourlyIndex + settings.hourlyVisibleItemsSizeValue()
        )
            ?.apply {
                forEach {
                    it.useMetricSystem = settings.useMetricSystemValue()
                    val hourlyCalendar =
                        Calendar.getInstance().apply { timeInMillis = it.datetime }
                    if (calendar != null && (calendar.get(Calendar.DAY_OF_YEAR) != hourlyCalendar.get(Calendar.DAY_OF_YEAR))) {
                        calendar.add(Calendar.DAY_OF_YEAR, 1)
                        index++
                    }
                    it.sunrise = weather.daily?.let { it[index].sunrise } ?: 0
                    it.sunset = weather.daily?.let { it[index].sunset } ?: 0
                }
            }
        val startDailyIndex = findNextDayIndex(weather.daily)
        weather.daily = weather.daily?.subList(
            startDailyIndex,
            startDailyIndex + settings.dailyVisibleItemsSizeValue()
        )
            ?.onEach { it.useMetricSystem = settings.useMetricSystemValue() }
        Timber.i("AppLogger - Checking current ${weather.current?.useMetricSystem}")
        weather
    }

    suspend fun weather(): Weather? = withContext(Dispatchers.IO) {
        val weather = weatherApi.weather(
            session.apiKey,
            session.currentPlace!!.latitude,
            session.currentPlace!!.longitude,
            session.currentLanguage!!,
            if (settings.useMetricSystemValue()) WeatherApi.UNIT_METRIC else WeatherApi.UNIT_IMPERIAL
        )
        weather.current?.useMetricSystem = settings.useMetricSystemValue()
        fixDatetime(weather)
        val startMinutelyIndex = findNextMinuteIndex(weather.minutely)
        weather.minutely = weather.minutely?.subList(
            startMinutelyIndex,
            startMinutelyIndex + settings.minutelyVisibleItemsSizeValue()
        )
        val currentDay = findCurrentDay(weather.daily)
        var index = currentDay?.let { day ->
            weather.current?.minimumTemperature = day.temperature.minimum
            weather.current?.maximumTemperature = day.temperature.maximum
            weather.daily!!.indexOf(day)
        } ?: 0
        val calendar: Calendar? = currentDay?.let { day ->
            Calendar.getInstance().apply { timeInMillis = day.datetime }
        }
        val startHourlyIndex = findNextHourIndex(weather.hourly)
        weather.hourly = weather.hourly?.subList(
            startHourlyIndex,
            startHourlyIndex + settings.hourlyVisibleItemsSizeValue()
        )
            ?.onEach {
                it.useMetricSystem = settings.useMetricSystemValue()
                val hourlyCalendar =
                    Calendar.getInstance().apply { timeInMillis = it.datetime }
                if (calendar != null && (calendar.get(Calendar.DAY_OF_YEAR) != hourlyCalendar.get(Calendar.DAY_OF_YEAR))) {
                    calendar.add(Calendar.DAY_OF_YEAR, 1)
                    index++
                }
                it.sunrise = weather.daily?.let { it[index].sunrise } ?: 0
                it.sunset = weather.daily?.let { it[index].sunset } ?: 0
            }
        val startDailyIndex = findNextDayIndex(weather.daily)
        weather.daily = weather.daily?.subList(
            startDailyIndex,
            startDailyIndex + settings.dailyVisibleItemsSizeValue()
        )
            ?.apply { forEach { it.useMetricSystem = settings.useMetricSystemValue() } }
        weather
    }

    private fun fixDatetime(weather: Weather?) {
        weather?.let {
            it.current?.apply {
                datetime *= 1000L
                sunrise *= 1000L
                sunset *= 1000L
            }
            it.minutely?.onEach { minute -> minute.datetime *= 1000L }
            it.hourly?.onEach { hour -> hour.datetime *= 1000L }
            it.daily?.onEach { day ->
                day.datetime *= 1000L
                day.sunrise *= 1000L
                day.sunset *= 1000L
            }
        }
    }

    private fun findNextMinuteIndex(minutely: List<Minutely>?): Int {
        val currentTimestamp = Date().time
        return minutely?.firstOrNull { it -> (it.datetime >= currentTimestamp) }?.let {
            minutely.indexOf(it)
        } ?: run {
            0
        }
    }

    private fun findNextHourIndex(hourly: List<Hourly>?): Int {
        val currentTimestamp = Date().time
        return hourly?.firstOrNull { it -> (it.datetime >= currentTimestamp) }?.let {
            hourly.indexOf(it)
        } ?: run {
            0
        }
    }

    private fun findCurrentDay(daily: List<Daily>?): Daily? {
        return daily?.firstOrNull { it ->
            val current = Date()
            val currentCalendar = Calendar.getInstance().apply { time = current }
            val date = Date(it.datetime)
            val dateCalendar = Calendar.getInstance().apply { time = date }
            currentCalendar.get(Calendar.DAY_OF_MONTH) == dateCalendar.get(Calendar.DAY_OF_MONTH)
        }

    }

    private fun findNextDayIndex(daily: List<Daily>?): Int {
        return daily?.let {
            it.firstOrNull { day ->
                val current = Date()
                val currentCalendar = Calendar.getInstance().apply { time = current }
                val date = Date(day.datetime)
                val dateCalendar = Calendar.getInstance().apply { time = date }
                currentCalendar.get(Calendar.DAY_OF_MONTH) != dateCalendar.get(Calendar.DAY_OF_MONTH)
            }?.let { nextDay ->
                it.indexOf(nextDay)
            } ?: run {
                0
            }
        } ?: run {
            0
        }
    }
}
