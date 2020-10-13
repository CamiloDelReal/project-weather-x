package org.xapps.apps.weatherx.services.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn


@SuppressLint("MissingPermission")
@ExperimentalCoroutinesApi
class GpsTracker(
    private val context: Context
) : LocationListener {

    private var locationManager: LocationManager? = null

    private val updaterEmitter = MutableLiveData<Location>()

    var validateUpdates: Boolean = false

    var location: Location? = null
        private set(value) {
            field = value
            updaterEmitter.value = value
        }

    fun watchUpdater(): Flow<Location> {
        return updaterEmitter.asFlow().flowOn(Dispatchers.Main)
    }

    fun start() {
        locationManager = context.getSystemService(LOCATION_SERVICE) as LocationManager

        val isGPSEnabled = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) ?: false
        val isNetworkEnabled = locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ?: false

        location = try {
            if (isNetworkEnabled) {
                locationManager?.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(),
                    this
                )
                locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            } else if (isGPSEnabled) {
                locationManager?.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(),
                    this
                )
                locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    fun stopTracker() {
        locationManager?.removeUpdates(this@GpsTracker)
    }


    override fun onLocationChanged(location: Location) {
        if (validateUpdates) {
            if(this.location != null) {
                val distanceKm = calculateCoordinatesDistanceKm(
                    this.location!!.latitude,
                    this.location!!.longitude,
                    location.latitude,
                    location.longitude
                )
                if ((distanceKm * 1000) >= MIN_DISTANCE_CHANGE_FOR_UPDATES) {
                    this.location = location
                }
            } else {
                this.location = location
            }
        } else {
            this.location = location
        }
    }

    override fun onProviderDisabled(provider: String) {}

    override fun onProviderEnabled(provider: String) {}

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

    companion object {
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES = 100L     // 100 meters

        private const val MIN_TIME_BW_UPDATES = 1000L * 60L * 1L    // 1 minute
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
