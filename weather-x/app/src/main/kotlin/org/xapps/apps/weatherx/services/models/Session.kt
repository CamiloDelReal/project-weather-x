package org.xapps.apps.weatherx.services.models


class Session {

    var apiKey: String = "8eb0abbbf60ef5486b0e62726e0cdc5f"

    var currentLanguage: String? = "es"

    //    var currentLocation: Location? = null
    var currentPlace: Place? = Place(
        country = "Serbia",
        city = "Stari",
        latitude = 44.795084,
        longitude = 20.496254,
        code = "12000"
    )

}