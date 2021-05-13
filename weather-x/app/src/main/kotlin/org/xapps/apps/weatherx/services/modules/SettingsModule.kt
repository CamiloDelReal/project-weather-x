package org.xapps.apps.weatherx.services.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import org.xapps.apps.weatherx.services.repositories.SettingsRepository
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class SettingsModule {

    companion object {
        const val PREFERENCE_NAME = "application_preferences"
    }

    @Singleton
    @Provides
    fun provideSettingsService(@ApplicationContext context: Context): SettingsRepository =
        SettingsRepository(context, Dispatchers.IO, PREFERENCE_NAME)

}