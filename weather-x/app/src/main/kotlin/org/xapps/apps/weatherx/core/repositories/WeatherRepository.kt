package org.xapps.apps.weatherx.core.repositories

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.xapps.apps.weatherx.core.models.*
import org.xapps.apps.weatherx.core.remote.WeatherApi
import org.xapps.apps.weatherx.core.utils.debug
import org.xapps.apps.weatherx.core.utils.info
import timber.log.Timber
import java.util.*
import javax.inject.Inject


class WeatherRepository @Inject constructor(
    private val dispatcher: CoroutineDispatcher,
    private val session: Session,
    private val weatherApi: WeatherApi,
    private val settings: SettingsRepository
) {

    suspend fun current(): Current? = withContext(dispatcher) {
        info<WeatherRepository>("Requesting current weather info")
        val weather = weatherApi.current(
            session.apiKey,
            session.currentPlace!!.latitude,
            session.currentPlace!!.longitude,
            session.currentLanguage!!,
            if (settings.useMetricSystemValue()) WeatherApi.UNIT_METRIC else WeatherApi.UNIT_IMPERIAL
        )
        fixDatetime(weather)
        weather.current?.useMetricSystem = settings.useMetricSystemValue()
        debug<WeatherRepository>("Object ${weather.current}")
        weather.current
    }

    suspend fun minutely(): List<Minutely>? = withContext(dispatcher){
        info<WeatherRepository>("Requesting minutely weather info")
        val weather = weatherApi.minutely(
            session.apiKey,
            session.currentPlace!!.latitude,
            session.currentPlace!!.longitude,
            session.currentLanguage!!,
            if (settings.useMetricSystemValue()) WeatherApi.UNIT_METRIC else WeatherApi.UNIT_IMPERIAL
        )
        fixDatetime(weather)
        val startMinutelyIndex = findNextMinuteIndex(weather.minutely)
        debug<WeatherRepository>("Index for next minute info $startMinutelyIndex")
        weather.minutely = weather.minutely?.subList(
            startMinutelyIndex,
            startMinutelyIndex + settings.minutelyVisibleItemsSizeValue()
        )
        debug<WeatherRepository>("Object ${weather.minutely}")
        weather.minutely
    }

    suspend fun hourly(): List<Hourly>? = withContext(dispatcher) {
        info<WeatherRepository>("Requesting hourly weather info")
        val weather = weatherApi.hourly(
            session.apiKey,
            session.currentPlace!!.latitude,
            session.currentPlace!!.longitude,
            session.currentLanguage!!,
            if (settings.useMetricSystemValue()) WeatherApi.UNIT_METRIC else WeatherApi.UNIT_IMPERIAL
        )
        fixDatetime(weather)
        val startHourlyIndex = findNextHourIndex(weather.hourly)
        debug<WeatherRepository>("Index for next hour info $startHourlyIndex")
        weather.hourly = weather.hourly?.subList(
            startHourlyIndex,
            startHourlyIndex + settings.hourlyVisibleItemsSizeValue()
        )
            ?.onEach {
                it.useMetricSystem = settings.useMetricSystemValue()
            }
        debug<WeatherRepository>("Object ${weather.hourly}")
        weather.hourly
    }

    suspend fun daily(): List<Daily>? = withContext(dispatcher) {
        info<WeatherRepository>("Requesting daily weather info")
        val weather = weatherApi.daily(
            session.apiKey,
            session.currentPlace!!.latitude,
            session.currentPlace!!.longitude,
            session.currentLanguage!!,
            if (settings.useMetricSystemValue()) WeatherApi.UNIT_METRIC else WeatherApi.UNIT_IMPERIAL
        )
        fixDatetime(weather)
        val startDailyIndex = findNextDayIndex(weather.daily)
        debug<WeatherRepository>("Index for next day info $startDailyIndex")
        weather.daily = weather.daily?.subList(
            startDailyIndex,
            startDailyIndex + settings.dailyVisibleItemsSizeValue()
        )
            ?.onEach { it.useMetricSystem = settings.useMetricSystemValue() }
        debug<WeatherRepository>("Object ${weather.daily}")
        weather.daily
    }

    suspend fun currentHourlyDaily(): Weather = withContext(dispatcher) {
        info<WeatherRepository>("Requesting current, hourly and daily weather info")
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
        debug<WeatherRepository>("Index for next hour info $startHourlyIndex")
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
        debug<WeatherRepository>("Index for next day info $startDailyIndex")
        weather.daily = weather.daily?.subList(
            startDailyIndex,
            startDailyIndex + settings.dailyVisibleItemsSizeValue()
        )
            ?.onEach { it.useMetricSystem = settings.useMetricSystemValue() }
        Timber.i("AppLogger - Checking current ${weather.current?.useMetricSystem}")
        debug<WeatherRepository>("Object $weather")
        weather
    }

    suspend fun weather(): Weather = withContext(dispatcher) {
        info<WeatherRepository>("Requesting weather info")
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
        debug<WeatherRepository>("Index for next minute info $startMinutelyIndex")
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
        debug<WeatherRepository>("Index for next hour info $startHourlyIndex")
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
        debug<WeatherRepository>("Index for next day info $startDailyIndex")
        weather.daily = weather.daily?.subList(
            startDailyIndex,
            startDailyIndex + settings.dailyVisibleItemsSizeValue()
        )
            ?.apply { forEach { it.useMetricSystem = settings.useMetricSystemValue() } }
        weather
    }

    private fun fixDatetime(weather: Weather?) {
        info<WeatherRepository>("Requesting fix date time")
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
        info<WeatherRepository>("Requesting find next minute index")
        val currentTimestamp = Date().time
        return minutely?.firstOrNull { it -> (it.datetime >= currentTimestamp) }?.let {
            minutely.indexOf(it)
        } ?: run {
            0
        }
    }

    private fun findNextHourIndex(hourly: List<Hourly>?): Int {
        info<WeatherRepository>("Requesting find next hour index")
        val currentTimestamp = Date().time
        return hourly?.firstOrNull { it -> (it.datetime >= currentTimestamp) }?.let {
            hourly.indexOf(it)
        } ?: run {
            0
        }
    }

    private fun findCurrentDay(daily: List<Daily>?): Daily? {
        info<WeatherRepository>("Requesting find current day")
        return daily?.firstOrNull { it ->
            val current = Date()
            val currentCalendar = Calendar.getInstance().apply { time = current }
            val date = Date(it.datetime)
            val dateCalendar = Calendar.getInstance().apply { time = date }
            currentCalendar.get(Calendar.DAY_OF_MONTH) == dateCalendar.get(Calendar.DAY_OF_MONTH)
        }

    }

    private fun findNextDayIndex(daily: List<Daily>?): Int {
        info<WeatherRepository>("Requesting find next day index")
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
