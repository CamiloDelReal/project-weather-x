package org.xapps.apps.weatherx.services.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import org.xapps.apps.weatherx.services.settings.SettingsService
import javax.inject.Singleton


@Module
@InstallIn(ApplicationComponent::class)
class SettingsModule {

    companion object {
        const val PREFERENCE_FILENAME = "application_preferences.xml"
    }

    @Singleton
    @Provides
    fun provideSettingsService(@ApplicationContext context: Context): SettingsService =
        SettingsService(context, PREFERENCE_FILENAME)

}