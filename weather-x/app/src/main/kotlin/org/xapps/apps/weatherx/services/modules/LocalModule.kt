package org.xapps.apps.weatherx.services.modules

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import org.xapps.apps.weatherx.services.local.LocalDatabaseService
import org.xapps.apps.weatherx.services.local.PlaceDao
import org.xapps.apps.weatherx.services.repositories.PlaceRepository
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class LocalModule {

    companion object {
        private const val DB_FILENAME = "application_database.db"
    }

    @Singleton
    @Provides
    fun provideLocalDatabaseService(@ApplicationContext context: Context): LocalDatabaseService =
        Room.databaseBuilder(context, LocalDatabaseService::class.java, DB_FILENAME).build()

    @Singleton
    @Provides
    fun providePlaceDao(localDatabaseService: LocalDatabaseService): PlaceDao =
        localDatabaseService.locationDao()

    @Singleton
    @Provides
    fun providePlaceRepository(placeDao: PlaceDao): PlaceRepository =
        PlaceRepository(Dispatchers.IO, placeDao)
}