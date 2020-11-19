package org.xapps.apps.weatherx.services.utils

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import java.util.*


object KotlinUtils {

    fun timerFlow(initialDelay: Long = 0, interval: Long) = callbackFlow<Void?> {
        val timer = Timer()
        timer.scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    try { offer(null) } catch (e: Exception) {}
                }
            },
            initialDelay,
            interval)
        awaitClose {
            timer.cancel()
        }
    }

}