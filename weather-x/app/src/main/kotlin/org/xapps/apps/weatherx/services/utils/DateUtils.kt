package org.xapps.apps.weatherx.services.utils

import java.text.SimpleDateFormat
import java.util.*


object DateUtils {

    fun dateToString(timestamp: Long, format: String = "yyyy-MM-dd HH:mm:ss"): String {
        val date = Date(timestamp)
        return try {
            val formatter = SimpleDateFormat(format, Locale.getDefault())
            formatter.format(date)
        } catch (e: Exception) {
            ""
        }
    }

}