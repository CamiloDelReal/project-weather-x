package org.xapps.apps.weatherx.services.modules

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import org.xapps.apps.weatherx.services.local.LocalDatabaseService
import org.xapps.apps.weatherx.services.local.PlaceDao
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
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
    fun provideLocationDao(localDatabaseService: LocalDatabaseService): PlaceDao =
        localDatabaseService.locationDao()

}