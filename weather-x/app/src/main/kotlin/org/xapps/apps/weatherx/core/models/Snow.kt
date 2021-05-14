package org.xapps.apps.weatherx.core.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class Snow (

    @Json(name = "1h")
    val oneHour: Double

)