package org.xapps.apps.weatherx.services.repositories

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.xapps.apps.weatherx.services.local.PlaceDao
import org.xapps.apps.weatherx.services.models.Place
import org.xapps.apps.weatherx.services.utils.debug
import org.xapps.apps.weatherx.services.utils.info
import timber.log.Timber
import javax.inject.Inject


class PlaceRepository @Inject constructor(
    private val dispatcher: CoroutineDispatcher,
    private val placeDao: PlaceDao
) {

    suspend fun place(id: Long): Place? = withContext(dispatcher) {
        info<PlaceRepository>("Requesting place with id = $id")
        val place = placeDao.placeAsync(id)
        debug<PlaceRepository>("Object $place")
        place
    }

    suspend fun insert(place: Place): Long = withContext(dispatcher) {
        info<PlaceRepository>("Inserting object")
        debug<PlaceRepository>("Object $place")
        val id = placeDao.insertAsync(place)
        debug<PlaceRepository>("Object inserted with id = $id")
        id
    }

}