package org.xapps.apps.weatherx.core.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class Current(

    @Json(name = "dt")
    var datetime: Long,

    @Json(name = "sunrise")
    var sunrise: Long,

    @Json(name = "sunset")
    var sunset: Long,

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

    @Json(name = "wind_gust")
    val windGust: Double? = null,

    @Json(name = "rain")
    val rain: Rain? = null,

    @Json(name = "snow")
    val snow: Snow? = null,

    @Json(name = "weather")
    val conditions: List<Condition>,

    @Transient
    var useMetricSystem: Boolean = true

) {

    // Helper fields for UI
    var minimumTemperature: Double = 0.0
    var maximumTemperature: Double = 0.0
    var probabilityOfPrecipitation: Double = 0.0

}