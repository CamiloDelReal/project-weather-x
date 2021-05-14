package org.xapps.apps.weatherx.core.local

import androidx.room.Database
import androidx.room.RoomDatabase
import org.xapps.apps.weatherx.core.models.Place


@Database(entities = arrayOf(Place::class), version = 1, exportSchema = false)
abstract class LocalDatabaseService: RoomDatabase() {

    abstract fun locationDao(): PlaceDao

}