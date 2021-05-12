package org.xapps.apps.weatherx.services.repositories

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import org.xapps.apps.weatherx.services.models.Condition
import org.xapps.apps.weatherx.services.models.Place
import timber.log.Timber
import javax.inject.Inject


class SettingsRepository @Inject constructor(
    private val context: Context,
    private val preferenceName: String
) {

    companion object {
        private val ATTR_IS_FIRST_TIME = booleanPreferencesKey("attrIsFirstTime")
        private val ATTR_ON_BOARDING_FINISHED = booleanPreferencesKey("attrOnBoardingFinished")
        private val ATTR_DARK_MODE_ON = booleanPreferencesKey("attrDarkModeOn")

        private val ATTR_LAST_PLACE_MONITORED = longPreferencesKey("attrLastPlaceMonitored")
        private val ATTR_MINUTELY_VISIBLE_ITEMS_SIZE = intPreferencesKey("attrMinutelyVisibleItemsSize")
        private val ATTR_HOURLY_VISIBLE_ITEMS_SIZE = intPreferencesKey("attrHourlyVisibleItemsSize")
        private val ATTR_DAILY_VISIBLE_ITEMS_SIZE = intPreferencesKey("attrDailyVisibleItemsSize")
        private val ATTR_USE_METRIC_SYSTEM = booleanPreferencesKey("attrUseMetricSystem")
        private val ATTR_LAST_CONDITION_CODE = intPreferencesKey("attrLastConditionCode")
        private val ATTR_LAST_TEMPERATURE = doublePreferencesKey("attrLastTemperature")
        private val ATTR_LAST_WAS_DAY_LIGHT = booleanPreferencesKey("attrLastWasDayLight")
        private val ATTR_LAST_WAS_THERE_VISIBILITY = booleanPreferencesKey("attrLastWasThereVisibility")
    }

    private val sharedPreferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE)
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = preferenceName)


    suspend fun isFirstTimeValue(): Boolean =
        context.dataStore.data.flowOn(Dispatchers.IO).first()[ATTR_IS_FIRST_TIME] ?: false

    suspend fun setIsFirstTime(isFirstTime: Boolean) =
        context.dataStore.edit { preferences ->
            preferences[ATTR_IS_FIRST_TIME] = isFirstTime
        }

    suspend fun isOnBoardingFinishedValue(): Boolean =
        context.dataStore.data.flowOn(Dispatchers.IO).first()[ATTR_ON_BOARDING_FINISHED] ?: false

    suspend fun setOnBoardingFinished(onBoardingFinished: Boolean) =
        context.dataStore.edit { preferences ->
            preferences[ATTR_ON_BOARDING_FINISHED] = onBoardingFinished
        }

    suspend fun isDarkModeOnValue(): Boolean =
        context.dataStore.data.flowOn(Dispatchers.IO).first()[ATTR_DARK_MODE_ON] ?: false

    fun isDarkModeOn(): Flow<Boolean> =
        context.dataStore.data
            .catch { ex ->
                Timber.e(ex)
                emit(emptyPreferences())
            }
            .map { preferences ->
                val isActive = preferences[ATTR_DARK_MODE_ON] ?: false
                isActive
            }
            .flowOn(Dispatchers.IO)

    suspend fun setIsDarkModeOn(darkModeOn: Boolean) =
        context.dataStore.edit { preferences ->
            preferences[ATTR_DARK_MODE_ON] = darkModeOn
        }

    suspend fun lastPlaceMonitoredValue(): Long =
        context.dataStore.data.flowOn(Dispatchers.IO).first()[ATTR_LAST_PLACE_MONITORED] ?: Place.CURRENT_PLACE_ID

    suspend fun setLastPlaceMonitored(id: Long) =
        context.dataStore.edit { preferences ->
            preferences[ATTR_LAST_PLACE_MONITORED] = id
        }

    suspend fun minutelyVisibleItemsSizeValue(): Int =
        context.dataStore.data.flowOn(Dispatchers.IO).first()[ATTR_MINUTELY_VISIBLE_ITEMS_SIZE] ?: 60

    suspend fun setMinutelyVisibleItemsSize(size: Int) =
        context.dataStore.edit { preferences ->
            preferences[ATTR_MINUTELY_VISIBLE_ITEMS_SIZE] = size
        }

    suspend fun hourlyVisibleItemsSizeValue(): Int =
        context.dataStore.data.flowOn(Dispatchers.IO).first()[ATTR_HOURLY_VISIBLE_ITEMS_SIZE] ?: 24

    suspend fun setHourlyVisibleItemsSize(size: Int) =
        context.dataStore.edit { preferences ->
            preferences[ATTR_HOURLY_VISIBLE_ITEMS_SIZE] = size
        }

    suspend fun dailyVisibleItemsSizeValue(): Int =
        context.dataStore.data.flowOn(Dispatchers.IO).first()[ATTR_DAILY_VISIBLE_ITEMS_SIZE] ?: 7

    suspend fun setDailyVisibleItemsSize(size: Int) =
        context.dataStore.edit { preferences ->
            preferences[ATTR_DAILY_VISIBLE_ITEMS_SIZE] = size
        }

    suspend fun useMetricSystemValue(): Boolean =
        context.dataStore.data.flowOn(Dispatchers.IO).first()[ATTR_USE_METRIC_SYSTEM] ?: true

    fun useMetricSystem(): Flow<Boolean> =
        context.dataStore.data
            .catch { ex ->
                Timber.e(ex)
                emit(emptyPreferences())
            }
            .map { preferences ->
                preferences[ATTR_USE_METRIC_SYSTEM] ?: true
            }
            .flowOn(Dispatchers.IO)

    suspend fun setUseMetricSystem(useMetricSystem: Boolean) =
        context.dataStore.edit { preferences ->
            preferences[ATTR_USE_METRIC_SYSTEM] = useMetricSystem
        }

    suspend fun lastConditionCodeValue(): Int =
        context.dataStore.data.flowOn(Dispatchers.IO).first()[ATTR_LAST_CONDITION_CODE] ?: Condition.Group.DEFAULT_CODE

    suspend fun setLastConditionCode(code: Int) =
        context.dataStore.edit { preferences ->
            preferences[ATTR_LAST_CONDITION_CODE] = code
        }

    suspend fun lastTemperatureValue(): Double =
        context.dataStore.data.flowOn(Dispatchers.IO).first()[ATTR_LAST_TEMPERATURE] ?: 0.0

    suspend fun setLastTemperature(temperature: Double) =
        context.dataStore.edit { preferences ->
            preferences[ATTR_LAST_TEMPERATURE] = temperature
        }

    suspend fun lastWasDayLightValue(): Boolean =
        context.dataStore.data.flowOn(Dispatchers.IO).first()[ATTR_LAST_WAS_DAY_LIGHT] ?: true

    suspend fun setLastWasDayLight(dayLight: Boolean) =
        context.dataStore.edit { preferences ->
            preferences[ATTR_LAST_WAS_DAY_LIGHT] = dayLight
        }

    suspend fun lastWasThereVisibilityValue(): Boolean =
        context.dataStore.data.flowOn(Dispatchers.IO).first()[ATTR_LAST_WAS_THERE_VISIBILITY] ?: true

    suspend fun setLastWasVisible(isThereVisibility: Boolean) =
        context.dataStore.edit { preferences ->
            preferences[ATTR_LAST_WAS_THERE_VISIBILITY] = isThereVisibility
        }

}