package org.xapps.apps.weatherx.di.modules

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

    @Singleton
    @Provides
    fun provideApplicationSettings(@ApplicationContext context: Context): SettingsService =
        SettingsService(context)

}