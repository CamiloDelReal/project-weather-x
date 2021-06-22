package org.xapps.apps.weatherx.application

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDex
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.*
import org.xapps.apps.weatherx.BuildConfig
import org.xapps.apps.weatherx.core.repositories.SettingsRepository
import timber.log.Timber
import javax.inject.Inject


@HiltAndroidApp
class ApplicationManager : Application() {

    @Inject
    lateinit var settings: SettingsRepository

    init {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    override fun onCreate() {
        super.onCreate()
        runBlocking {
            AppCompatDelegate.setDefaultNightMode(if (settings.isDarkModeOnValue()) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
        }
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}
