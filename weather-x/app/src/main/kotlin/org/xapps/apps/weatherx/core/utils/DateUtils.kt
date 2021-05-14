package org.xapps.apps.weatherx.core.utils

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

    fun isDayLight(sunrise: Long = 0L, sunset: Long = 0L, datetime: Long = 0L): Boolean {
        val current = if(datetime == 0L) Date().time else datetime
        return ((sunrise == 0L && sunset == 0L) || (sunrise <= current && current <= sunset))
    }

}