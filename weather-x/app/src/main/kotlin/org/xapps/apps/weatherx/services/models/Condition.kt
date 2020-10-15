package org.xapps.apps.weatherx.services.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class Condition(

    @Json(name = "id")
    val id: Int,

    @Json(name = "main")
    val main: String,

    @Json(name = "description")
    val description: String,

    @Json(name = "icon")
    val icon: String

) {

    enum class Group {
        THUNDERSTORM,
        DRIZZLE,
        RAIN,
        SNOW,
        MIST,
        SMOKE,
        HAZE,
        DUST,
        FOG,
        SAND,
        ASH,
        SQUALL,
        TORNADO,
        CLEAR,
        CLOUDS;

        companion object {

            fun fromName(name: String?): Group {
                return name?.let {
                    when (name) {
                        "Thunderstorm" -> THUNDERSTORM
                        "Drizzle" -> DRIZZLE
                        "Rain" -> RAIN
                        "Snow" -> SNOW
                        "Mist" -> MIST
                        "Smoke" -> SMOKE
                        "Haze" -> HAZE
                        "Dust" -> DUST
                        "Fog" -> FOG
                        "Sand" -> SAND
                        "Ash" -> ASH
                        "Squall" -> SQUALL
                        "Tornado" -> TORNADO
                        "Clear" -> CLEAR
                        "Clouds" -> CLOUDS
                        else -> CLEAR
                    }
                } ?: run {
                    CLEAR
                }
            }
        }

    }
}
