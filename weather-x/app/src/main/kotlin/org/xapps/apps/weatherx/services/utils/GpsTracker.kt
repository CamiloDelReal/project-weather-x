package org.xapps.apps.weatherx.services.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import timber.log.Timber
import java.util.concurrent.TimeUnit


@SuppressLint("MissingPermission")
class GpsTracker(
    private val context: Context
) {

    private var locationProvider: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null

    private val _errorFlow = MutableSharedFlow<Error>(replay = 1)
    private val _updaterFlow = MutableSharedFlow<Location>(replay = 1)

    val errorFlow: SharedFlow<Error> = _errorFlow
    val updaterFlow: SharedFlow<Location?> = _updaterFlow

    enum class Error {
        NONE,
        INVALID_LOCATION,
        PROVIDER_ERROR
    }

    var validateUpdates: Boolean = false

    var error: Error = Error.NONE
        private set(value) {
            field = value
            _errorFlow.tryEmit(value)
        }

    var location: Location? = null
        private set(value) {
            field = value
            value?.let {
                Timber.i("AppLogger - About to emit $value")
                _updaterFlow.tryEmit(it)
            }
        }

    fun start() {
        Timber.i("AppLogger - GPSTracker has started")
        locationProvider = LocationServices.getFusedLocationProviderClient(context)

        locationProvider?.lastLocation?.apply {
            addOnFailureListener{
                error = Error.PROVIDER_ERROR
            }
            addOnSuccessListener { lastLocation ->
                if(lastLocation != null) {
                    Timber.i("AppLogger - Last location has returned a valid value $lastLocation")
                    processLocation(lastLocation)
                } else {
                    Timber.i("AppLogger - Last location has returned an invalid value")
                    error = Error.INVALID_LOCATION
                }
            }
        }

        locationRequest = LocationRequest.create().apply {
            interval = TimeUnit.MINUTES.toMillis(MIN_TIME_BW_UPDATES)
            fastestInterval = TimeUnit.MINUTES.toMillis(MIN_FASTEST_TIME_BW_UPDATES)
            maxWaitTime = TimeUnit.MINUTES.toMillis(WAIT_FOR_BATCH_UPDATES)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            smallestDisplacement = MIN_DISPLACEMENT_FOR_UPDATES
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

                processLocation(locationResult.lastLocation)
            }
        }

        locationProvider?.requestLocationUpdates(locationRequest!!, locationCallback!!, Looper.myLooper()!!)
    }

    private fun processLocation(lastLocation: Location) {
        if (validateUpdates) {
            Timber.i("AppLogger - Location validation active $location")
            if (location != null) {
                Timber.i("AppLogger - Location is valid")
                val distanceKm = calculateCoordinatesDistanceKm(
                    location!!.latitude,
                    location!!.longitude,
                    lastLocation.latitude,
                    lastLocation.longitude
                )
                Timber.i("Distance from last location ${distanceKm}Km ${distanceKm * 1000} ${MIN_DISPLACEMENT_FOR_UPDATES}")
                if ((distanceKm * 1000) >= MIN_DISPLACEMENT_FOR_UPDATES) {
                    Timber.i("AppLogger - Location is in range to update")
                    location = lastLocation
                }
            } else {
                Timber.i("AppLogger - Current saved location is invalid, proceding to save the location recieved")
                location = lastLocation
            }
        } else {
            Timber.i("AppLogger - Location validation is not active, proceding to save new location")
            location = lastLocation
        }
    }

    fun stop() {
        val removeTask = locationProvider?.removeLocationUpdates(locationCallback!!)
        removeTask?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Timber.i("AppLogger - Location callback removed")
            } else {
                Timber.i("AppLogger - Failed to remove location callback")
            }
        }
    }

    companion object {
        private const val MIN_DISPLACEMENT_FOR_UPDATES = 100.0f     // Meters

        private const val MIN_TIME_BW_UPDATES = 5L                  // Minutes
        private const val MIN_FASTEST_TIME_BW_UPDATES = 3L          // Minutes
        private const val WAIT_FOR_BATCH_UPDATES = 1L               // Minutes
    }

    private fun decimalToRadian(value: Double): Double {
        return value * Math.PI / 180
    }

    private fun calculateCoordinatesDistanceKm(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val R = 6.371e3 // Earth radius in KM
        val rLat1: Double = decimalToRadian(lat1)
        val rLat2: Double = decimalToRadian(lat2)
        val deltaLat: Double = decimalToRadian(lat2 - lat1)
        val deltaLong: Double = decimalToRadian(lon2 - lon1)
        val a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(rLat1) * Math.cos(rLat2) *
                Math.sin(deltaLong / 2) * Math.sin(deltaLong / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return R * c
    }

}
