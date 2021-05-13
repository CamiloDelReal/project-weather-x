package org.xapps.apps.weatherx.services.modules

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import org.xapps.apps.weatherx.services.models.Session
import org.xapps.apps.weatherx.services.remote.WeatherApi
import org.xapps.apps.weatherx.services.repositories.SettingsRepository
import org.xapps.apps.weatherx.services.repositories.WeatherRepository
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class WeatherModule {

    @Singleton
    @Provides
    fun provideWeatherRepository(session: Session, weatherApi: WeatherApi, settings: SettingsRepository): WeatherRepository =
        WeatherRepository(Dispatchers.IO, session, weatherApi, settings)

}