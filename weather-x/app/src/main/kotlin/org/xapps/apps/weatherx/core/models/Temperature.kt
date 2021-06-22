package org.xapps.apps.weatherx.core.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class Temperature(

    @Json(name = "min")
    val minimum: Double,

    @Json(name = "max")
    val maximum: Double,

    @Json(name = "morn")
    val morning: Double,

    @Json(name = "day")
    val day: Double,

    @Json(name = "eve")
    val evening: Double,

    @Json(name = "night")
    val night: Double

) {

    fun average(): Double {
        return (minimum + maximum + morning + evening + day + night) / 6
    }

}