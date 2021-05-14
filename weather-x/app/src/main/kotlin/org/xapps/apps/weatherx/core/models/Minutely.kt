package org.xapps.apps.weatherx.services.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class Minutely(

    @Json(name = "dt")
    var datetime: Long,

    @Json(name = "precipitation")
    val probabilityOfPrecipitation: Double

)