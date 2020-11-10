package org.xapps.apps.weatherx.services.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Build
import timber.log.Timber
import javax.inject.Inject


class NetworkUtils @Inject constructor(
    private val context: Context
) {

    fun isConnectedToInternet(): Boolean {
        val connectivity = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            Timber.i("Using for target api <= 28")
            val activeNetwork = connectivity.activeNetworkInfo
            activeNetwork?.isConnected ?: false
        } else {
            Timber.i("Using for target api >= 29")
            val activeNetwork = connectivity.activeNetwork
            val capabilities = connectivity.getNetworkCapabilities(activeNetwork)
            return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false
        }
    }


//        networks.forEach {
//            if(connectivity.getNetworkInfo(it)?.isConnected == true) {
//
//            }
//        }
//        val info = connectivity.allNetworkInfo
//        for (i in info.indices)
//            if (info[i].state == NetworkInfo.State.CONNECTED) {
//                return true
//            }
//        return false
//
//        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
//        val wifi = connectivityManager!!.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
//        val mob = connectivityManager!!.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
//        if (wifi!!.isConnected) {
//            Toast.makeText(this, "WIFI", Toast.LENGTH_SHORT).show()
//        } else if (mob!!.isConnected) {
//            Toast.makeText(this, "MOBILE", Toast.LENGTH_SHORT).show()
//        } else {
//            Toast.makeText(this, "other", Toast.LENGTH_SHORT).show()
//        }
////
////        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
////        val activeNetwork = cm.activeNetworkInfo
////        if (activeNetwork != null) {
////            // connected to the internet
////            if (activeNetwork.type == ConnectivityManager.TYPE_WIFI) {
////                // connected to wifi
////            } else if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE) {
////                // connected to mobile data
////            }
////        } else {
////            // not connected to the internet
////        }
//    }

    fun wifiNetAvailable(): Boolean {
        var available = false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val wifiMgr = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

            try {
                if (wifiMgr.isWifiEnabled) {
                    val wifiInfo = wifiMgr.connectionInfo
                    available = wifiInfo.networkId != -1
                }
            } catch (e: Exception) {
                available = false
            }

        }

        return available
    }

}