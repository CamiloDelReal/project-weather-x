package org.xapps.apps.weatherx.services.models

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class Weather(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
)