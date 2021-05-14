package org.xapps.apps.weatherx.core.models

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

            const val DEFAULT_NAME = "Clear"
            const val DEFAULT_CODE = 800

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

            fun fromCode(code: Int?): Group {
                return code?.let {
                    when(it) {
                        200, 201, 202, 210, 211, 212, 221, 230, 231, 232 -> THUNDERSTORM
                        300, 301, 302, 310, 311, 312, 313, 314, 321 -> DRIZZLE
                        500, 501, 502, 503, 504, 511, 520, 521, 522, 531 -> RAIN
                        600, 601, 602, 611, 612, 613, 615, 616, 620, 621, 622 -> SNOW
                        701 -> MIST
                        711 -> SMOKE
                        721 -> HAZE
                        731, 761 -> DUST
                        741 -> FOG
                        751 -> SAND
                        762 -> ASH
                        771 -> SQUALL
                        781 -> TORNADO
                        800 -> CLEAR
                        801, 802, 803, 804 -> CLOUDS
                        else -> CLEAR
                    }
                } ?: run {
                    CLEAR
                }
            }
        }

    }
}
