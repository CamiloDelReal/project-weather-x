package org.xapps.apps.weatherx.services.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import org.xapps.apps.weatherx.services.models.Location


@Dao
interface LocationDao {

    @Insert
    suspend fun insertAsync(location: Location): Long

    @Insert
    fun insert(location: Location): Long

    @Insert
    suspend fun insertAsync(locations: List<Location>): List<Long>

    @Insert
    fun insert(locations: List<Location>): List<Long>

    @Query("SELECT * FROM locations")
    fun locationsAsync(): Flow<List<Location>>

    @Query("SELECT * FROM locations")
    fun locations(): List<Location>

    @Query("SELECT * FROM locations WHERE id = :id")
    fun locationAsync(id: Long): Flow<Location>

    @Query("SELECT * FROM locations WHERE id = :id")
    fun location(id: Long): Location

    @Update
    suspend fun updateAsync(location: Location): Int

    @Update
    fun update(location: Location): Int

    @Update
    suspend fun updateAsync(locations: List<Location>): Int

    @Update
    fun update(locations: List<Location>): Int

    @Delete
    suspend fun deleteAsync(location: Location): Int

    @Delete
    fun delete(location: Location): Int

    @Delete
    suspend fun deleteAsync(locations: List<Location>): Int

    @Delete
    fun delete(locations: List<Location>): Int

}