package org.xapps.apps.weatherx.views.bindings

import android.widget.TextView
import androidx.databinding.BindingAdapter
import org.xapps.apps.weatherx.R
import org.xapps.apps.weatherx.services.models.Current
import org.xapps.apps.weatherx.services.models.Daily
import org.xapps.apps.weatherx.services.models.Hourly
import org.xapps.apps.weatherx.services.utils.DateUtils
import java.util.*
import kotlin.math.ceil


object TextViewBindings {

    @JvmStatic
    @BindingAdapter("timestampToSimpleHour")
    fun timestampToSimpleHour(textView: TextView, timestamp: Long) {
        textView.text =
            if (timestamp != 0L) DateUtils.dateToString(timestamp,"HH:mm") else ""
    }

    @JvmStatic
    @BindingAdapter("timestampToDate")
    fun timestampToDate(textView: TextView, timestamp: Long) {
        textView.text =
            if (timestamp != 0L) DateUtils.dateToString(timestamp, "MM/dd/yyyy") else ""
    }

    @JvmStatic
    @BindingAdapter("timestampToDayOfTheWeek")
    fun timestampToDayOfTheWeek(textView: TextView, timestamp: Long) {
        textView.text = if (timestamp != 0L) DateUtils.dateToString(timestamp, "EEEE")
            .capitalize() else ""
    }

    @JvmStatic
    @BindingAdapter("timestampToTodayOrTomorrow")
    fun timestampToTodayOrTomorrow(textView: TextView, timestamp: Long) {
        val current = Date()
        val currentCalendar = Calendar.getInstance().apply { time = current }
        val date = Date(timestamp)
        val dateCalendar = Calendar.getInstance().apply { time = date }
        textView.text =
            if (currentCalendar.get(Calendar.DAY_OF_MONTH) == dateCalendar.get(Calendar.DAY_OF_MONTH)) textView.context.getString(
                R.string.today
            ) else textView.context.getString(
                R.string.tomorrow
            )
    }

    @JvmStatic
    @BindingAdapter("temperature")
    fun temperature(textView: TextView, value: Current?) {
        val valueStr = value?.let { info ->
            if (info.useMetric) {
                String.format(Locale.US, "%.1f°", info.temperature)
            } else {
                String.format(Locale.US, "%.1f°", info.temperature)
            }
        } ?: run {
            textView.context.getString(R.string.empty)
        }
        textView.text = valueStr
    }

    @JvmStatic
    @BindingAdapter("temperatureWithUnit")
    fun temperatureWithUnit(textView: TextView, value: Current?) {
        val valueStr = value?.let { info ->
            if (info.useMetric) {
                String.format(Locale.US, "%.1f°C", info.temperature)
            } else {
                String.format(Locale.US, "%.1f°F", info.temperature)
            }
        } ?: run {
            textView.context.getString(R.string.empty)
        }
        textView.text = valueStr
    }

    @JvmStatic
    @BindingAdapter("minimumTemperature")
    fun minimumTemperature(textView: TextView, value: Current?) {
        val valueStr = value?.let { info ->
            if (info.useMetric) {
                String.format(Locale.US, "%.1f°", info.minimumTemperature)
            } else {
                String.format(Locale.US, "%.1f°", info.minimumTemperature)
            }
        } ?: run {
            textView.context.getString(R.string.empty)
        }
        textView.text = valueStr
    }

    @JvmStatic
    @BindingAdapter("maximumTemperature")
    fun maximumTemperature(textView: TextView, value: Current?) {
        val valueStr = value?.let { info ->
            if (info.useMetric) {
                String.format(Locale.US, "%.1f°", info.maximumTemperature)
            } else {
                String.format(Locale.US, "%.1f°", info.maximumTemperature)
            }
        } ?: run {
            textView.context.getString(R.string.empty)
        }
        textView.text = valueStr
    }

    @JvmStatic
    @BindingAdapter("temperature")
    fun temperature(textView: TextView, value: Daily?) {
        val valueStr = value?.let { info ->
            if (info.useMetric) {
                String.format(Locale.US, "%.1f°", info.temperature.average())
            } else {
                String.format(Locale.US, "%.1f°", info.temperature.average())
            }
        } ?: run {
            textView.context.getString(R.string.empty)
        }
        textView.text = valueStr
    }

    @JvmStatic
    @BindingAdapter("temperature")
    fun temperature(textView: TextView, value: Hourly?) {
        val valueStr = value?.let { info ->
            if (info.useMetric) {
                String.format(Locale.US, "%.1f°", info.temperature)
            } else {
                String.format(Locale.US, "%.1f°", info.temperature)
            }
        } ?: run {
            textView.context.getString(R.string.empty)
        }
        textView.text = valueStr
    }

    @JvmStatic
    @BindingAdapter("condition")
    fun condition(textView: TextView, value: Current?) {
        val valueStr = value?.let { info ->
            info.conditions.joinToString(separator = ",") { it.description.capitalize() }
        } ?: run {
            textView.context.getString(R.string.empty)
        }
        textView.text = valueStr
    }

    @JvmStatic
    @BindingAdapter("condition")
    fun condition(textView: TextView, value: Daily?) {
        val valueStr = value?.let { info ->
            info.conditions.joinToString(separator = ",") { it.description.capitalize() }
        } ?: run {
            textView.context.getString(R.string.empty)
        }
        textView.text = valueStr
    }

    @JvmStatic
    @BindingAdapter("condition")
    fun condition(textView: TextView, value: Hourly?) {
        val valueStr = value?.let { info ->
            info.conditions.joinToString(separator = ",") { it.description.capitalize() }
        } ?: run {
            textView.context.getString(R.string.empty)
        }
        textView.text = valueStr
    }

    @JvmStatic
    @BindingAdapter("conditionSimple")
    fun conditionSimple(textView: TextView, value: Hourly?) {
        val valueStr = value?.let { info ->
            info.conditions.joinToString(separator = ",") { it.main.capitalize() }
        } ?: run {
            textView.context.getString(R.string.empty)
        }
        textView.text = valueStr
    }

    @JvmStatic
    @BindingAdapter("pressure")
    fun pressure(textView: TextView, value: Daily?) {
        val valueStr = value?.let { info ->
            String.format("%d hPa", info.pressure)
        } ?: run {
            textView.context.getString(R.string.not_available)
        }
        textView.text = valueStr
    }

    @JvmStatic
    @BindingAdapter("windSpeed")
    fun windSpeed(textView: TextView, value: Daily?) {
        val valueStr = value?.let { info ->
            if (info.useMetric) {
                String.format(Locale.US, "%.1f km/h", info.windSpeed * 3.6)
            } else {
                String.format(Locale.US, "%.1f mph", info.windSpeed)
            }
        } ?: run {
            textView.context.getString(R.string.not_available)
        }
        textView.text = valueStr
    }

    @JvmStatic
    @BindingAdapter("windSpeed")
    fun windSpeed(textView: TextView, value: Hourly?) {
        val valueStr = value?.let { info ->
            if (info.useMetric) {
                String.format(Locale.US, "%.1f km/h", info.windSpeed * 3.6)
            } else {
                String.format(Locale.US, "%.1f mph", info.windSpeed)
            }
        } ?: run {
            textView.context.getString(R.string.not_available)
        }
        textView.text = valueStr
    }

    @JvmStatic
    @BindingAdapter("windDirection")
    fun windDirection(textView: TextView, value: Daily?) {
        val valueStr = value?.let { info ->
            when {
                info.windDegrees >= 348.75 && info.windDegrees < 11.25 -> textView.context.getString(
                    R.string.north
                )
                info.windDegrees >= 11.25 && info.windDegrees < 33.75 -> textView.context.getString(
                    R.string.north_northeast
                )
                info.windDegrees >= 33.75 && info.windDegrees < 56.25 -> textView.context.getString(
                    R.string.northeast
                )
                info.windDegrees >= 56.25 && info.windDegrees < 78.75 -> textView.context.getString(
                    R.string.east_northeast
                )
                info.windDegrees >= 78.75 && info.windDegrees < 101.25 -> textView.context.getString(
                    R.string.east
                )
                info.windDegrees >= 101.25 && info.windDegrees < 123.75 -> textView.context.getString(
                    R.string.east_southeast
                )
                info.windDegrees >= 123.75 && info.windDegrees < 146.25 -> textView.context.getString(
                    R.string.southeast
                )
                info.windDegrees >= 146.25 && info.windDegrees < 168.75 -> textView.context.getString(
                    R.string.south_southeast
                )
                info.windDegrees >= 168.75 && info.windDegrees < 191.25 -> textView.context.getString(
                    R.string.south
                )
                info.windDegrees >= 191.25 && info.windDegrees < 213.75 -> textView.context.getString(
                    R.string.south_southwest
                )
                info.windDegrees >= 213.75 && info.windDegrees < 236.25 -> textView.context.getString(
                    R.string.southwest
                )
                info.windDegrees >= 236.25 && info.windDegrees < 258.75 -> textView.context.getString(
                    R.string.west_southwest
                )
                info.windDegrees >= 258.75 && info.windDegrees < 281.25 -> textView.context.getString(
                    R.string.west
                )
                info.windDegrees >= 281.25 && info.windDegrees < 303.75 -> textView.context.getString(
                    R.string.west_northwest
                )
                info.windDegrees >= 303.75 && info.windDegrees < 326.25 -> textView.context.getString(
                    R.string.northwest
                )
                info.windDegrees >= 326.25 && info.windDegrees < 348.75 -> textView.context.getString(
                    R.string.north_northwest
                )
                else -> textView.context.getString(R.string.empty)
            }
        } ?: run {
            textView.context.getString(R.string.not_available)
        }
        textView.text = valueStr
    }

    @JvmStatic
    @BindingAdapter("realFeel")
    fun realFeel(textView: TextView, value: Daily?) {
        val valueStr = value?.let { info ->
            if (info.useMetric) {
                String.format(Locale.US, "%.1f°", info.feelsLike.average())
            } else {
                String.format(Locale.US, "%.1f°", info.feelsLike.average())
            }
        } ?: run {
            textView.context.getString(R.string.not_available)
        }
        textView.text = valueStr
    }

    @JvmStatic
    @BindingAdapter("uvindex")
    fun uvindex(textView: TextView, value: Daily?) {
        val valueStr = value?.let { info ->
            String.format(Locale.US, "%.2f", info.uvi)
        } ?: run {
            textView.context.getString(R.string.not_available)
        }
        textView.text = valueStr
    }

    @JvmStatic
    @BindingAdapter("humidity")
    fun humidity(textView: TextView, value: Daily?) {
        val valueStr = value?.let { info ->
            String.format("%d%s", info.humidity, "%")
        } ?: run {
            textView.context.getString(R.string.not_available)
        }
        textView.text = valueStr
    }

    @JvmStatic
    @BindingAdapter("precipitation")
    fun precipitation(textView: TextView, value: Daily?) {
        val valueStr = value?.let { info ->
            val popd = info.probabilityOfPrecipitation * 100
            val popl = popd.toLong()
            if(popd == popl.toDouble())
                String.format("%d%s", popl, "%")
            else
                String.format(Locale.US, "%.1f%s", popd, "%")
        } ?: run {
            textView.context.getString(R.string.not_available)
        }
        textView.text = valueStr
    }

    @JvmStatic
    @BindingAdapter("precipitation")
    fun precipitation(textView: TextView, value: Hourly?) {
        val valueStr = value?.let { info ->
            val popd = info.probabilityOfPrecipitation * 100
            val popl = popd.toLong()
            if(popd == popl.toDouble())
                String.format("%d%s", popl, "%")
            else
                String.format(Locale.US, "%.1f%s", popd, "%")
        } ?: run {
            textView.context.getString(R.string.not_available)
        }
        textView.text = valueStr
    }

    @JvmStatic
    @BindingAdapter("visibility")
    fun visibility(textView: TextView, value: Daily?) {
        val valueStr = value?.let { info ->
            if (info.visibility == -1) {
                textView.context.getString(R.string.not_available)
            } else if (info.visibility < 1000) {
                String.format("%d m", info.visibility)
            } else {
                String.format(Locale.US, "%.1f km", info.visibility.toFloat() / 1000f)
            }
        } ?: run {
            textView.context.getString(R.string.not_available)
        }
        textView.text = valueStr
    }

    @JvmStatic
    @BindingAdapter("visibility")
    fun visibility(textView: TextView, value: Hourly?) {
        val valueStr = value?.let { info ->
            if (info.visibility == -1) {
                textView.context.getString(R.string.not_available)
            } else if (info.visibility < 1000) {
                String.format("%d m", info.visibility)
            } else {
                String.format(Locale.US, "%d km", ceil(info.visibility.toDouble() / 1000).toInt())
            }
        } ?: run {
            textView.context.getString(R.string.not_available)
        }
        textView.text = valueStr
    }

}