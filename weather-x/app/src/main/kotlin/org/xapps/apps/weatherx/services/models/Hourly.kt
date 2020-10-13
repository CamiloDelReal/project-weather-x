package org.xapps.apps.weatherx.services.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class Hourly(

    @Json(name = "dt")
    var datetime: Long,

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

    @Json(name = "visibility")
    val visibility: Int,

    @Json(name = "wind_speed")
    val windSpeed: Double,

    @Json(name = "wind_deg")
    val windDegrees: Int,

    @Json(name = "wind_gust")
    val windGust: Double? = null,

    @Json(name = "pop")
    val probabilityOfPrecipitation: Double,

    @Json(name = "rain")
    val rain: Rain? = null,

    @Json(name = "snow")
    val snow: Snow? = null,

    @Json(name = "weather")
    val conditions: List<Condition>,

    @Transient
    var useMetric: Boolean = true

) {

    // Helper fields for UI
    var sunrise: Long = 0
    var sunset: Long = 0

}