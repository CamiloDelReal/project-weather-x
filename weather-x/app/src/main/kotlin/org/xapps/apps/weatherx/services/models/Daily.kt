package org.xapps.apps.weatherx.services.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class Daily(

    @Json(name = "dt")
    var datetime: Long,

    @Json(name = "sunrise")
    var sunrise: Long,

    @Json(name = "sunset")
    var sunset: Long,

    @Json(name = "temp")
    val temperature: Temperature,

    @Json(name = "feels_like")
    val feelsLike: FeelsLike,

    @Json(name = "clouds")
    val clouds: Int,

    @Json(name = "dew_point")
    val dewPoint: Double,

    @Json(name = "humidity")
    val humidity: Int,

    @Json(name = "visibility")
    val visibility: Int = -1,

    @Json(name = "pressure")
    val pressure: Int,

    @Json(name = "uvi")
    val uvi: Double,

    @Json(name = "pop")
    val probabilityOfPrecipitation: Double,

    @Json(name = "wind_deg")
    val windDegrees: Int,

    @Json(name = "wind_speed")
    val windSpeed: Double,

    @Json(name = "wind_gust")
    val windGust: Double? = null,

    @Json(name = "rain")
    val rain: Double? = null,

    @Json(name = "snow")
    val snow: Double? = null,

    @Json(name = "weather")
    val conditions: List<Condition>,

    @Transient
    var useMetric: Boolean = true

)