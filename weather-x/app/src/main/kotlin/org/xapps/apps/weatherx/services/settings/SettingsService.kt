package org.xapps.apps.weatherx.services.settings

import android.content.Context
import androidx.core.content.edit
import org.xapps.apps.weatherx.services.models.Condition
import javax.inject.Inject


class SettingsService @Inject constructor(private val context: Context) {

    companion object {
        const val PREFERENCE_FILENAME = "application_preferences.xml"

        private const val ATTR_IS_FIRST_TIME = "attrIsFirstTime"
        private const val ATTR_DARK_MODE_ON = "attrDarkModeOn"

        private const val ATTR_LAST_PLACE_MONITORED = "attrLastPlaceMonitored"
        private const val ATTR_MINUTELY_VISIBLE_ITEMS_SIZE = "attrMinutelyVisibleItemsSize"
        private const val ATTR_HOURLY_VISIBLE_ITEMS_SIZE = "attrHourlyVisibleItemsSize"
        private const val ATTR_DAILY_VISIBLE_ITEMS_SIZE = "attrDailyVisibleItemsSize"
        private const val ATTR_USE_METRIC = "attrUseMetric"
        private const val ATTR_LAST_CONDITION_CODE = "attrLastConditionCode"
        private const val ATTR_LAST_TEMPERATURE = "attrLastTemperature"
        private const val ATTR_LAST_WAS_DAY_LIGHT = "attrLastWasDayLight"
        private const val ATTR_LAST_WAS_THERE_VISIBILITY = "attrLastWasThereVisibility"
    }

    private val sharedPreferences = context.getSharedPreferences(PREFERENCE_FILENAME, Context.MODE_PRIVATE)

    fun isFirstTime(): Boolean =
        sharedPreferences.getBoolean(ATTR_IS_FIRST_TIME, true)

    fun setIsFirstTime(isFirstTime: Boolean) =
        sharedPreferences.edit { putBoolean(ATTR_IS_FIRST_TIME, isFirstTime) }

    fun isDarkModeOn(): Boolean =
        sharedPreferences.getBoolean(ATTR_DARK_MODE_ON, false)

    fun setIsDarkModeOn(darkModeOn: Boolean) =
        sharedPreferences.edit { putBoolean(ATTR_DARK_MODE_ON, darkModeOn) }

    fun lastPlaceMonitored(): Long =
        sharedPreferences.getLong(ATTR_LAST_PLACE_MONITORED, 0)

    fun setLastPlaceMonitored(id: Long) =
        sharedPreferences.edit { putLong(ATTR_LAST_PLACE_MONITORED, id) }

    fun minutelyVisibleItemsSize(): Int =
        sharedPreferences.getInt(ATTR_MINUTELY_VISIBLE_ITEMS_SIZE, 60)

    fun setMinutelyVisibleItemsSize(size: Int) =
        sharedPreferences.edit { putInt(ATTR_MINUTELY_VISIBLE_ITEMS_SIZE, size) }

    fun hourlyVisibleItemsSize(): Int =
        sharedPreferences.getInt(ATTR_HOURLY_VISIBLE_ITEMS_SIZE, 24)

    fun setHourlyVisibleItemsSize(size: Int) =
        sharedPreferences.edit { putInt(ATTR_HOURLY_VISIBLE_ITEMS_SIZE, size) }

    fun dailyVisibleItemsSize(): Int =
        sharedPreferences.getInt(ATTR_DAILY_VISIBLE_ITEMS_SIZE, 7)

    fun setDailyVisibleItemsSize(size: Int) =
        sharedPreferences.edit { putInt(ATTR_DAILY_VISIBLE_ITEMS_SIZE, size) }

    fun useMetric(): Boolean =
        sharedPreferences.getBoolean(ATTR_USE_METRIC, true)

    fun setUseMetric(useMetric: Boolean) =
        sharedPreferences.edit { putBoolean(ATTR_USE_METRIC, useMetric) }

    fun lastConditionCode(): Int =
        sharedPreferences.getInt(ATTR_LAST_CONDITION_CODE, Condition.Group.DEFAULT_CODE)

    fun setLastConditionCode(code: Int) =
        sharedPreferences.edit { putInt(ATTR_LAST_CONDITION_CODE, code) }

    fun lastTemperature(): Double =
        sharedPreferences.getFloat(ATTR_LAST_TEMPERATURE, 0.0f).toDouble()

    fun setLastTemperature(temperature: Double) =
        sharedPreferences.edit { putFloat(ATTR_LAST_TEMPERATURE, temperature.toFloat()) }

    fun lastWasDayLight(): Boolean =
        sharedPreferences.getBoolean(ATTR_LAST_WAS_DAY_LIGHT, true)

    fun setLastWasDayLight(dayLight: Boolean) =
        sharedPreferences.edit { putBoolean(ATTR_LAST_WAS_DAY_LIGHT, dayLight) }

    fun lastWasThereVisibility(): Boolean =
        sharedPreferences.getBoolean(ATTR_LAST_WAS_THERE_VISIBILITY, true)

    fun setLastWasVisible(isThereVisibility: Boolean) =
        sharedPreferences.edit { putBoolean(ATTR_LAST_WAS_THERE_VISIBILITY, isThereVisibility) }

}