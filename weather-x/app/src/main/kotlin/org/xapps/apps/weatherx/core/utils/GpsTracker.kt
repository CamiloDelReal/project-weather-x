package org.xapps.apps.weatherx.core.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import java.util.concurrent.TimeUnit
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


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
            info<GpsTracker>("About to emit $value")
            _errorFlow.tryEmit(value)
        }

    var location: Location? = null
        private set(value) {
            field = value
            value?.let {
                info<GpsTracker>("About to emit $value")
                _updaterFlow.tryEmit(it)
            }
        }

    fun start() {
        info<GpsTracker>("Gps tracker is starting")
        locationProvider = LocationServices.getFusedLocationProviderClient(context)

        locationProvider?.lastLocation?.apply {
            addOnFailureListener{
                error<GpsTracker>(it)
                error = Error.PROVIDER_ERROR
            }
            addOnSuccessListener { lastLocation ->
                info<GpsTracker>("Location provider has received new location $lastLocation")
                if(lastLocation != null) {
                    info<GpsTracker>("Location received is valid")
                    processLocation(lastLocation)
                } else {
                    error<GpsTracker>("Location provider has received null object")
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
                info<GpsTracker>("Location provider callback has received a result $locationResult")
                processLocation(locationResult.lastLocation)
            }
        }

        locationProvider?.requestLocationUpdates(locationRequest!!, locationCallback!!, Looper.myLooper()!!)
        info<GpsTracker>("Gps tracker has started")
    }

    private fun processLocation(lastLocation: Location) {
        info<GpsTracker>("Starting processing of last location received $lastLocation")
        if (validateUpdates) {
            info<GpsTracker>("Update validation is active")
            if (location != null) {
                info<GpsTracker>("Current location is valid, proceeding to validate last location")
                val distanceKm = calculateCoordinatesDistanceKm(
                    location!!.latitude,
                    location!!.longitude,
                    lastLocation.latitude,
                    lastLocation.longitude
                )
                debug<GpsTracker>("Distance from last location ${distanceKm}Km")
                if ((distanceKm * 1000) >= MIN_DISPLACEMENT_FOR_UPDATES) {
                    info<GpsTracker>("Location is in range to update, saving last location")
                    location = lastLocation
                }
            } else {
                info<GpsTracker>("Current saved location is invalid, saving last location")
                location = lastLocation
            }
        } else {
            info<GpsTracker>("Update validation is not active, saving last location")
            location = lastLocation
        }
    }

    fun stop() {
        info<GpsTracker>("Gps tracker is been stopped")
        val removeTask = locationProvider?.removeLocationUpdates(locationCallback!!)
        removeTask?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                info<GpsTracker>("Location provider callback removed")
            } else {
                error<GpsTracker>("Failed to remove location callback")
            }
        }
        info<GpsTracker>("Gps tracker has been stopped")
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
        val a = sin(deltaLat / 2) * sin(deltaLat / 2) +
                cos(rLat1) * cos(rLat2) *
                sin(deltaLong / 2) * sin(deltaLong / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c
    }

}
