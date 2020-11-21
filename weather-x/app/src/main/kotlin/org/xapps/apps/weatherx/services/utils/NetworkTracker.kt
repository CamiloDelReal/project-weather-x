package org.xapps.apps.weatherx.services.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject


class NetworkTracker @Inject constructor(
    private val context: Context
) {

    private val connectivityEmitter = MutableLiveData<Boolean>()

    fun connectivityWatcher(): Flow<Boolean> {
        return connectivityEmitter.asFlow().flowOn(Dispatchers.Main)
    }

    fun isConnectedToInternet(): Boolean {
        val connectivity =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            val activeNetwork = connectivity.activeNetworkInfo
            activeNetwork?.isConnected ?: false
        } else {
            val activeNetwork = connectivity.activeNetwork
            val capabilities = connectivity.getNetworkCapabilities(activeNetwork)
            return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false
        }
    }

    fun start() {
        try {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            connectivityManager.registerDefaultNetworkCallback(object : NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    Timber.i("AppLogger - Possible gateway to internet detected")
                    connectivityEmitter.postValue(true)
                }

                override fun onLost(network: Network) {
                    Timber.i("AppLogger - Possible gateway to internet has lost")
                    connectivityEmitter.postValue(false)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}