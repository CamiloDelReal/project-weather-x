package org.xapps.apps.weatherx.services.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class Weather(

    @Json(name = "lat")
    val latitude: Double,

    @Json(name = "lon")
    val longitude: Double,

    @Json(name = "timezone")
    val timezone: String,

    @Json(name = "timezone_offset")
    val timezoneOffset: Int,

    @Json(name = "current")
    val current: Current? = null,

    @Json(name = "minutely")
    var minutely: List<Minutely>? = null,

    @Json(name = "daily")
    var daily: List<Daily>? = null,

    @Json(name = "hourly")
    var hourly: List<Hourly>? = null

)