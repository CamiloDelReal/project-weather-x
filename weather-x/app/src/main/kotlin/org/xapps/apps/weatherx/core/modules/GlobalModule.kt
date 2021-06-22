package org.xapps.apps.weatherx.core.modules

import android.content.Context
import android.location.Geocoder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.xapps.apps.weatherx.core.models.Session
import org.xapps.apps.weatherx.core.utils.GpsTracker
import org.xapps.apps.weatherx.core.utils.ConnectivityTracker
import java.util.*
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
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

    @Singleton
    @Provides
    fun provideConnectivityTracker(@ApplicationContext context: Context): ConnectivityTracker =
        ConnectivityTracker(context)

}