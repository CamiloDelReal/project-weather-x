package org.xapps.apps.weatherx.viewmodels

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import org.xapps.apps.weatherx.services.settings.SettingsService


class SplashViewModel @ViewModelInject constructor(
    private val settings: SettingsService
) : ViewModel() {
    init {
        Log.i("AppLogger", "Settings = $settings")
    }
}