package org.xapps.apps.weatherx.services.settings

import android.content.Context
import androidx.core.content.edit
import javax.inject.Inject


class SettingsService @Inject constructor(private val context: Context) {

    companion object {
        const val PREFERENCE_FILENAME = "application_preferences.xml"

        private const val ATTR_IS_FIRST_TIME = "attrIsFirstTime"
        private const val ATTR_DARK_MODE_ON = "attrDarkModeOn"
    }

    private val sharedPreferences = context.getSharedPreferences(PREFERENCE_FILENAME, Context.MODE_PRIVATE)

    fun isFirstTime(): Boolean =
        sharedPreferences.getBoolean(ATTR_IS_FIRST_TIME, true)

    fun setIsFirstTime(isFirstTime: Boolean) =
        sharedPreferences.edit { putBoolean(ATTR_IS_FIRST_TIME, isFirstTime) }

    fun isDarkModeOn(): Boolean =
        sharedPreferences.getBoolean(ATTR_DARK_MODE_ON, true)

    fun setIsDarkModeOn(darkModeOn: Boolean) =
        sharedPreferences.edit { putBoolean(ATTR_DARK_MODE_ON, darkModeOn) }

}