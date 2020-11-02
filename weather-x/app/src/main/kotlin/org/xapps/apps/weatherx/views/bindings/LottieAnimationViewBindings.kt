package org.xapps.apps.weatherx.views.bindings

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import androidx.databinding.BindingAdapter
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import org.xapps.apps.weatherx.services.models.Condition
import org.xapps.apps.weatherx.services.models.Current
import org.xapps.apps.weatherx.services.models.Daily
import org.xapps.apps.weatherx.services.models.Hourly
import org.xapps.apps.weatherx.services.utils.DateUtils
import org.xapps.apps.weatherx.views.utils.Utilities


object LottieAnimationViewBindings {

    @JvmStatic
    @BindingAdapter("weatherAnimation")
    fun weatherAnimation(view: LottieAnimationView, value: Current?) {
        weatherAnimation(
            view = view,
            condition = value?.let { if (it.conditions.isNotEmpty()) it.conditions[0] else null },
            datetime = value?.datetime ?: 0L,
            sunrise = value?.sunrise ?: 0L,
            sunset = value?.sunset ?: 0L,
            visibility = value?.visibility ?: 2000,
            animate = true
        )
    }

    @JvmStatic
    @BindingAdapter("weatherAnimation")
    fun weatherAnimation(view: LottieAnimationView, value: Hourly?) {
        weatherAnimation(
            view = view,
            condition = value?.let { if (it.conditions.isNotEmpty()) it.conditions[0] else null },
            datetime = value?.datetime ?: 0L,
            sunrise = value?.sunrise ?: 0L,
            sunset = value?.sunset ?: 0L,
            visibility = value?.visibility ?: 2000,
            animate = false
        )
    }

    @JvmStatic
    @BindingAdapter("weatherAnimation")
    fun weatherAnimation(view: LottieAnimationView, value: Daily?) {
        weatherAnimation(
            view = view,
            condition = value?.let { if (it.conditions.isNotEmpty()) it.conditions[0] else null },
            visibility = value?.visibility ?: 2000,
            animate = false
        )
    }

    private fun weatherAnimation(
        view: LottieAnimationView,
        condition: Condition?,
        datetime: Long = 0L,
        sunrise: Long = 0L,
        sunset: Long = 0L,
        visibility: Int = 2000,
        animate: Boolean = false
    ) {
        condition?.let {
            val isDayLight = DateUtils.isDayLight(sunrise = sunrise, sunset = sunset, datetime = datetime)
            val animation = weatherAnimation(it.id, isDayLight, Utilities.isVisible(visibility))
            if(animate) {
                if (view.tag == null || view.tag as String != animation) {
                    view.tag = animation
                    view.animate().alpha(0.0f).setDuration(250)
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(alphaAnimation: Animator?) {
                                view.alpha = 0.0f
                                view.setAnimation(animation)
                                view.speed = 1.0f
                                view.repeatCount = LottieDrawable.INFINITE
                                view.repeatMode = LottieDrawable.RESTART
                                view.playAnimation()
                                view.animate().alpha(1.0f).setDuration(250)
                                    .setListener(object : AnimatorListenerAdapter() {
                                        override fun onAnimationEnd(animation: Animator?) {
                                            view.alpha = 1.0f
                                        }
                                    })
                            }
                        })
                }
            } else {
                view.tag = animation
                view.setAnimation(animation)
                view.speed = 1.0f
                view.repeatCount = LottieDrawable.INFINITE
                view.repeatMode = LottieDrawable.RESTART
                view.playAnimation()
            }
        }
    }

    fun weatherAnimation(code: Int, isDayLight: Boolean, isThereVisibility: Boolean): String {
        return when (code) {
            200 -> "200-thunderstorm-with-light-rain.json"
            201 -> "201-thunderstorm-with-rain.json"
            202 -> "202-thunderstorm-with-heavy-rain.json"
            210 -> "210-light-thunderstorm.json"
            211 -> "211-thunderstorm.json"
            212 -> "212-heavy-thunderstorm.json"
            221 -> "221-ragged-thunderstorm.json"
            230 -> if (isDayLight) "230-thunderstorm-with-light-drizzle-day.json" else "230-thunderstorm-with-light-drizzle-night.json"
            231 -> "231-thunderstorm-with-drizzle.json"
            232 -> "232-thunderstorm-with-heavy-drizzle.json"
            300 -> if (isDayLight) "300-light-intensity-drizzle-day.json" else "300-light-intensity-drizzle-night.json"
            301 -> "301-drizzle.json"
            302 -> "302-heavy-intensity-drizzle.json"
            310 -> if (isDayLight) "310-light-intensity-drizzle-rain-day.json" else "310-light-intensity-drizzle-rain-night"
            311 -> "311-drizzle-rain.json"
            312 -> "312-heavy-intensity-drizzle-rain.json"
            313 -> "313-shower-rain-and-drizzle.json"
            314 -> "314-heavy-shower-rain-and-drizzle.json"
            321 -> "321-shower-drizzle.json"
            500 -> if (isDayLight) "500-light-rain-day.json" else "500-light-rain-night.json"
            501 -> if (isDayLight) "501-moderate-rain-day.json" else "501-moderate-rain-night.json"
            502 -> "502-heavy-intensity-rain.json"
            503 -> "503-very-heavy-rain.json"
            504 -> "504-extreme-rain.json"
            511 -> "511-freezing-rain.json"
            520 -> if (isDayLight) "520-light-intensity-shower-rain-day.json" else "520-light-intensity-shower-rain-night.json"
            521 -> "521-shower-rain.json"
            522 -> "522-heavy-intensity-shower-rain.json"
            531 -> "531-ragged-shower-rain.json"
            600 -> if (isDayLight) "600-light-snow-day.json" else "600-light-snow-night.json"
            601 -> "601-snow.json"
            602 -> "602-heavy-snow.json"
            611 -> "611-sleet.json"
            612 -> if (isDayLight) "612-light-shower-sleet-day.json" else "612-light-shower-sleet-night.json"
            613 -> "613-shower-sleet.json"
            615 -> if (isDayLight) "615-light-rain-and-snow-day.json" else "615-light-rain-and-snow-night.json"
            616 -> "616-rain-and-snow.json"
            620 -> if (isDayLight) "620-light-shower-snow-day.json" else "620-light-shower-snow-night.json"
            621 -> "621-shower-snow.json"
            622 -> "622-heavy-shower-snow.json"
            701 -> if(!isThereVisibility) "701-mist-no-visibility.json" else { if (isDayLight) "701-mist-day.json" else "701-mist-night.json" }
            711 -> if(!isThereVisibility) "711-smoke-no-visibility.json" else { if (isDayLight) "711-smoke-day.json" else "711-smoke-night.json" }
            721 -> if(!isThereVisibility) "721-haze-no-visibility.json" else { if (isDayLight) "721-haze-day.json" else "721-haze-night.json" }
            731 -> if(!isThereVisibility) "731-dust-no-visibility.json" else { if (isDayLight) "731-dust-day.json" else "731-dust-night.json" }
            741 -> if(!isThereVisibility) "741-fog-no-visibility.json" else { if (isDayLight) "741-fog-day.json" else "741-fog-night.json" }
            751 -> if(!isThereVisibility) "751-sand-no-visibility.json" else { if (isDayLight) "751-sand-day.json" else "751-sand-night.json" }
            761 -> if(!isThereVisibility) "761-dust-no-visibility.json" else { if (isDayLight) "761-dust-day.json" else "761-dust-night.json" }
            762 -> if(!isThereVisibility) "762-ash-no-visibility.json" else { if (isDayLight) "762-ash-day.json" else "762-ash-night.json" }
            771 -> if(!isThereVisibility) "771-squall-no-visibility.json" else { if (isDayLight) "771-squall-day.json" else "771-squall-night.json" }
            781 -> if(!isThereVisibility) "781-tornado-no-visibility.json" else { if (isDayLight) "781-tornado-day.json" else "781-tornado-night.json" }
            800 -> if (isDayLight) "800-clear-sky-day.json" else "800-clear-sky-night.json"
            801 -> if (isDayLight) "801-few-clouds-day.json" else "801-few-clouds-night.json"
            802 -> if (isDayLight) "802-scattered-clouds-day.json" else "802-scattered-clouds-night.json"
            803 -> "803-broken-clouds.json"
            804 -> "804-overcast-clouds.json"
            else -> if (isDayLight) "800-clear-sky-day.json" else "800-clear-sky-night.json"
        }
    }

}