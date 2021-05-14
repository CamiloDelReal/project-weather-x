package org.xapps.apps.weatherx.core.models

import org.xapps.apps.weatherx.BuildConfig


class Session {

    var apiKey: String = BuildConfig.OPEN_WEATHER_MAP_API_KEY

    var currentLanguage: String? = null

    var currentPlace: Place? = null

}