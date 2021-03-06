package org.xapps.apps.weatherx.core.utils

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import java.util.*


@OptIn(ExperimentalCoroutinesApi::class)
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