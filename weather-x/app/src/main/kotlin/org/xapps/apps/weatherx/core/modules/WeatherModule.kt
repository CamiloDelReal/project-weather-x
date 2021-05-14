package org.xapps.apps.weatherx.core.modules

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import org.xapps.apps.weatherx.core.models.Session
import org.xapps.apps.weatherx.core.remote.WeatherApi
import org.xapps.apps.weatherx.core.repositories.SettingsRepository
import org.xapps.apps.weatherx.core.repositories.WeatherRepository
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class WeatherModule {

    @Singleton
    @Provides
    fun provideWeatherRepository(session: Session, weatherApi: WeatherApi, settings: SettingsRepository): WeatherRepository =
        WeatherRepository(Dispatchers.IO, session, weatherApi, settings)

}