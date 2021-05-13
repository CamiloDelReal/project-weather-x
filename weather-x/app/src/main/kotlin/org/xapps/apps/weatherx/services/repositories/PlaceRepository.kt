package org.xapps.apps.weatherx.services.repositories

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.xapps.apps.weatherx.services.local.PlaceDao
import org.xapps.apps.weatherx.services.models.Place
import javax.inject.Inject


class PlaceRepository @Inject constructor(
    private val dispatcher: CoroutineDispatcher,
    private val placeDao: PlaceDao
) {

    suspend fun place(id: Long): Place? = withContext(dispatcher) {
        placeDao.placeAsync(id)
    }

    suspend fun insert(place: Place): Long = withContext(dispatcher) {
        placeDao.insertAsync(place)
    }

}