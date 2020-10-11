package org.xapps.apps.weatherx.services.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class WeatherInfo(

    @Json(name = "lat")
    val latitude: Double,

    @Json(name = "lon")
    val longitude: Double,

    @Json(name = "timezone")
    val timezone: String,

    @Json(name = "timezone_offset")
    val timezoneOffset: Int,

    @Json(name = "current")
    val current: Current,

    @Json(name = "minutely")
    val minutely: List<Minutely>,

    @Json(name = "daily")
    val daily: List<Daily>,

    @Json(name = "hourly")
    val hourly: List<Hourly>

)