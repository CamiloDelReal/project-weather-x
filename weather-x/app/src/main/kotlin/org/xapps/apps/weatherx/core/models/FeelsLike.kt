package org.xapps.apps.weatherx.services.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class FeelsLike(

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
        return (day + night + morning + evening) / 4
    }

}