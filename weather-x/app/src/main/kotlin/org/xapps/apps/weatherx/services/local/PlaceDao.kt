package org.xapps.apps.weatherx.services.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import org.xapps.apps.weatherx.services.models.Place


@Dao
interface PlaceDao {

    @Insert
    suspend fun insertAsync(place: Place): Long

    @Insert
    fun insert(place: Place): Long

    @Insert
    suspend fun insertAsync(places: List<Place>): List<Long>

    @Insert
    fun insert(places: List<Place>): List<Long>

    @Query("SELECT * FROM places")
    fun placesAsync(): Flow<List<Place>>

    @Query("SELECT * FROM places")
    fun places(): List<Place>

    @Query("SELECT * FROM places WHERE id = :id")
    fun placeAsync(id: Long): Flow<Place>

    @Query("SELECT * FROM places WHERE id = :id")
    fun place(id: Long): Place

    @Update
    suspend fun updateAsync(place: Place): Int

    @Update
    fun update(place: Place): Int

    @Update
    suspend fun updateAsync(places: List<Place>): Int

    @Update
    fun update(places: List<Place>): Int

    @Delete
    suspend fun deleteAsync(place: Place): Int

    @Delete
    fun delete(place: Place): Int

    @Delete
    suspend fun deleteAsync(places: List<Place>): Int

    @Delete
    fun delete(places: List<Place>): Int

}