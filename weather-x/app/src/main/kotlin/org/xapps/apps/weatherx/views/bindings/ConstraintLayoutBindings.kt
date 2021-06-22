package org.xapps.apps.weatherx.views.bindings

import android.graphics.drawable.AnimationDrawable
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import org.xapps.apps.weatherx.R
import org.xapps.apps.weatherx.core.models.Current
import org.xapps.apps.weatherx.core.utils.DateUtils


object ConstraintLayoutBindings {

    @JvmStatic
    @BindingAdapter("conditionBackground")
    fun conditionBackground(view: ConstraintLayout, data: Current?) {
        data?.let {
            val isDayLight = DateUtils.isDayLight(
                sunrise = it.sunrise,
                sunset = it.sunset,
                datetime = it.datetime
            )
            val currentDrawable =
                if (view.background is AnimationDrawable) (view.background as AnimationDrawable).getFrame(
                    1
                ) else view.background
            val nextDrawableId = conditionBackground(
                if (it.conditions.isNotEmpty()) it.conditions[0].id else 0,
                isDayLight,
                it.temperature,
                it.useMetricSystem
            )
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

    fun conditionBottomColor(
        code: Int,
        isDayLight: Boolean,
        temperature: Double,
        useMetric: Boolean
    ): Int {
        return if (!isDayLight) {
            R.color.clearSkyNight2
        } else {
            when (code) {
                200, 201, 202, 210, 211, 212, 221, 230, 231, 232 -> R.color.dayThunderstorm2
                300, 301, 302, 310, 311, 312, 313, 314, 321 -> R.color.dayDrizzle2
                500, 501, 502, 503, 504, 511, 520, 521, 522, 531 -> R.color.dayRain2
                600, 601, 602, 611, 612, 613, 615, 616, 620, 621, 622 -> R.color.daySnow2
                701 -> R.color.dayMist2
                711 -> R.color.daySmoke2
                721 -> R.color.dayHaze2
                731, 761 -> R.color.dayDust2
                741 -> R.color.dayFog2
                751 -> R.color.daySand2
                762 -> R.color.dayAsh2
                771 -> R.color.daySquall2
                781 -> R.color.dayTornado2
                800 -> {
                    if ((useMetric && temperature >= 35.0f) || (!useMetric && temperature >= 95.0f)) {
                        R.color.dayClearHot2
                    } else {
                        R.color.dayClear2
                    }
                }
                801, 802 -> R.color.dayClouds2
                803, 804 -> R.color.dayCloudsIntense2
                else -> R.color.dayClear2
            }
        }
    }
}
