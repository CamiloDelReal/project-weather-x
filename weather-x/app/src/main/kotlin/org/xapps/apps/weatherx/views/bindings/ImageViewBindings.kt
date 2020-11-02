package org.xapps.apps.weatherx.views.bindings

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import org.xapps.apps.weatherx.services.models.Hourly


object ImageViewBindings {

    @JvmStatic
    @BindingAdapter("windDirection")
    fun windDirection(imageView: ImageView, value: Hourly?) {
        imageView.rotation = 0f
        val valueInt = value?.let { info ->
            when {
                info.windDegrees >= 348.75 && info.windDegrees < 11.25 -> 180f
                info.windDegrees >= 11.25 && info.windDegrees < 33.75 -> 202.5f
                info.windDegrees >= 33.75 && info.windDegrees < 56.25 -> 225f
                info.windDegrees >= 56.25 && info.windDegrees < 78.75 -> 247.5f
                info.windDegrees >= 78.75 && info.windDegrees < 101.25 -> 270f
                info.windDegrees >= 101.25 && info.windDegrees < 123.75 -> 292.5f
                info.windDegrees >= 123.75 && info.windDegrees < 146.25 -> 315f
                info.windDegrees >= 146.25 && info.windDegrees < 168.75 -> 337.5f
                info.windDegrees >= 168.75 && info.windDegrees < 191.25 -> 0f
                info.windDegrees >= 191.25 && info.windDegrees < 213.75 -> 22.5f
                info.windDegrees >= 213.75 && info.windDegrees < 236.25 -> 45f
                info.windDegrees >= 236.25 && info.windDegrees < 258.75 -> 67.5f
                info.windDegrees >= 258.75 && info.windDegrees < 281.25 -> 90f
                info.windDegrees >= 281.25 && info.windDegrees < 303.75 -> 112.5f
                info.windDegrees >= 303.75 && info.windDegrees < 326.25 -> 135f
                info.windDegrees >= 326.25 && info.windDegrees < 348.75 -> 157.5f
                else -> 0f
            }
        } ?: run {
            0f
        }
        imageView.rotation = valueInt
    }

}