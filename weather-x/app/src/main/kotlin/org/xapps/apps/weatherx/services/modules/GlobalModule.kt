package org.xapps.apps.weatherx.services.modules

import android.content.Context
import android.location.Geocoder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import org.xapps.apps.weatherx.services.models.Session
import org.xapps.apps.weatherx.services.utils.GpsTracker
import java.util.*
import javax.inject.Singleton


@Module
@InstallIn(ApplicationComponent::class)
class GlobalModule {

    @Singleton
    @Provides
    fun provideSession(): Session =
        Session()

    @Singleton
    @Provides
    fun provideGpsTracker(@ApplicationContext context: Context): GpsTracker =
        GpsTracker(context)

    @Singleton
    @Provides
    fun providesGeocoder(@ApplicationContext context: Context): Geocoder =
        Geocoder(context, Locale.getDefault())

}