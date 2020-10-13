package org.xapps.apps.weatherx.services.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class Rain(

    @Json(name = "1h")
    val oneHour: Double

)