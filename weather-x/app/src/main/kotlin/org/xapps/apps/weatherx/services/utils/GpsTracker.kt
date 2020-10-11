package org.xapps.apps.weatherx.services.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log


@SuppressLint("MissingPermission")
class GpsTracker(
    private val context: Context
) : LocationListener {

    private var locationManager: LocationManager? = null

    interface Listener {
        fun update()
    }

    var listener: Listener? = null

    var location: Location? = null
        private set


    init {
        locationManager = context.getSystemService(LOCATION_SERVICE) as LocationManager

        val isGPSEnabled = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) ?: false
        val isNetworkEnabled = locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ?: false

        location = try {
            if (isNetworkEnabled) {
                Log.i("AppLogger", "Retrieving location from network")
                locationManager?.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(),
                    this
                )
                locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            } else if (isGPSEnabled) {
                Log.i("AppLogger", "Retrieving location from gps")
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
        Log.i("AppLogger", "onLocationChanged $location")
        this.location = location
        listener?.update()
    }

    override fun onProviderDisabled(provider: String) {
        Log.i("AppLogger", "onProviderDisabled")
    }

    override fun onProviderEnabled(provider: String) {
        Log.i("AppLogger", "onProviderEnabled")
    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
        Log.i("AppLogger", "onStatusChanged")
    }

    companion object {
        // The minimum distance to change Updates in meters
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES = 2L     // 10 meters

        // The minimum time between updates in milliseconds
        private const val MIN_TIME_BW_UPDATES = 1000L * 20L * 1L    // 1 minute
    }

}
