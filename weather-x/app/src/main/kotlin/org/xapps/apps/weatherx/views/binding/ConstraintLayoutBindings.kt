package org.xapps.apps.weatherx.views.binding

import android.graphics.drawable.AnimationDrawable
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import org.xapps.apps.weatherx.R
import org.xapps.apps.weatherx.services.models.Condition
import org.xapps.apps.weatherx.services.models.Current
import org.xapps.apps.weatherx.services.utils.DateUtils
import timber.log.Timber


object ConstraintLayoutBindings {

    @JvmStatic
    @BindingAdapter("conditionBackground")
    fun conditionBackground(view: ConstraintLayout, data: Current?) {
        data?.let {
            val isDayLight = DateUtils.isDayLight(sunrise = it.sunrise, sunset = it.sunset, datetime = it.datetime)
            val currentDrawable = if(view.background is AnimationDrawable) (view.background as AnimationDrawable).getFrame(1) else view.background
            val nextDrawableId = conditionBackground(if (it.conditions.isNotEmpty()) it.conditions[0].id else 0, isDayLight, it.temperature, it.useMetric)
            val anim = AnimationDrawable()
            anim.addFrame(currentDrawable, 500)
            anim.addFrame(ContextCompat.getDrawable(view.context, nextDrawableId)!!, 500)
            view.background = anim
            anim.isOneShot = true
            anim.setExitFadeDuration(500)
            anim.start()
        }
    }

    fun conditionBackground(
        code: Int,
        isDayLight: Boolean,
        temperature: Double,
        useMetric: Boolean
    ): Int {
        return if (!isDayLight) {
            R.drawable.ic_gradient_night
        } else {
            when (code) {
                200, 201, 202, 210, 211, 212, 221, 230, 231, 232 -> R.drawable.ic_gradient_day_thunderstorm
                300, 301, 302, 310, 311, 312, 313, 314, 321 -> R.drawable.ic_gradient_day_drizzle
                500, 501, 502, 503, 504, 511, 520, 521, 522, 531 -> R.drawable.ic_gradient_day_rain
                600, 601, 602, 611, 612, 613, 615, 616, 620, 621, 622 -> R.drawable.ic_gradient_day_snow
                701 -> R.drawable.ic_gradient_day_mist
                711 -> R.drawable.ic_gradient_day_smoke
                721 -> R.drawable.ic_gradient_day_haze
                731, 761 -> R.drawable.ic_gradient_day_dust
                741 -> R.drawable.ic_gradient_day_fog
                751 -> R.drawable.ic_gradient_day_sand
                762 -> R.drawable.ic_gradient_day_ash
                771 -> R.drawable.ic_gradient_day_squall
                781 -> R.drawable.ic_gradient_day_tornado
                800 -> {
                    if ((useMetric && temperature >= 35.0f) || (!useMetric && temperature >= 95.0f)) {
                        R.drawable.ic_gradient_day_clear_hot
                    } else {
                        R.drawable.ic_gradient_day_clear
                    }
                }
                801, 802 -> R.drawable.ic_gradient_day_clouds
                803, 804 -> R.drawable.ic_gradient_day_clouds_intense
                else -> R.drawable.ic_gradient_day_clear
            }
        }
    }
}
