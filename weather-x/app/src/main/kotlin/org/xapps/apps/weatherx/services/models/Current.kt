package org.xapps.apps.weatherx.services.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class CurrentInfo(
    @Json(name = "dt")
    val datetime: Int,

    @Json(name = "sunrise")
    val sunrise: Long,

    @Json(name = "sunset")
    val sunset: Long,

    @Json(name = "temp")
    val temperature: Double,

    @Json(name = "feels_like")
    val feelsLike: Double,

    @Json(name = "clouds")
    val clouds: Int,

    @Json(name = "dew_point")
    val dewPoint: Double,

    @Json(name = "humidity")
    val humidity: Int,

    @Json(name = "pressure")
    val pressure: Int,

    @Json(name = "uvi")
    val uvi: Double,

    @Json(name = "visibility")
    val visibility: Int,

    @Json(name = "wind_deg")
    val windDegrees: Int,

    @Json(name = "wind_speed")
    val windSpeed: Double,

    @Json(name = "weather")
    val weather: List<Weather>
)