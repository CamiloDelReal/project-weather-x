package org.xapps.apps.weatherx.services.models


data class Location (
    var name: String,
    var city: String,
    var latitude: Double,
    var longitude: Double,
    var code: Int,
    var useCelsius: Boolean,
    var temperature: Int,
    var condition: String
)