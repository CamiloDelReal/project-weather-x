package org.xapps.apps.weatherx.views.custom

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.widget.ImageViewCompat
import androidx.databinding.BindingAdapter
import org.xapps.apps.weatherx.R
import org.xapps.apps.weatherx.databinding.ContentCardForecastBinding
import org.xapps.apps.weatherx.core.models.Current


class ForecastCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyle, defStyleRes) {

    private var bindings: ContentCardForecastBinding

    private var clickableButton = false

    init {
        val a: TypedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ForecastCardView)
        val icon = a.getResourceId(R.styleable.ForecastCardView_fc_icon, -1)
        val iconTint = a.getColor(R.styleable.ForecastCardView_fc_iconTint, -1)
        val iconSize = a.getDimension(R.styleable.ForecastCardView_fc_iconSize, 32f)
        val value = a.getString(R.styleable.ForecastCardView_fc_value)
        val description = a.getString(R.styleable.ForecastCardView_fc_description)
        clickableButton = a.getBoolean(R.styleable.ForecastCardView_fc_clickable, false)
        a.recycle()

        val layoutInflater = LayoutInflater.from(context)
        bindings = ContentCardForecastBinding.inflate(layoutInflater, this, true)

        if(icon != -1) {
            bindings.imgIcon.setImageResource(icon)
            ImageViewCompat.setImageTintList(bindings.imgIcon, ColorStateList.valueOf(iconTint))
            val iconSizeInDp = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                iconSize,
                resources.displayMetrics
            ).toInt()
            bindings.imgIcon.layoutParams.height = iconSizeInDp
            bindings.imgIcon.layoutParams.width = iconSizeInDp
        }
        bindings.txvValue.text = value
        bindings.txvDescription.text = description

        if(clickableButton) {
            val fgValue = TypedValue()
            getContext().theme.resolveAttribute(
                android.R.attr.selectableItemBackground,
                fgValue,
                true
            )
            bindings.rootLayout.foreground = AppCompatResources.getDrawable(context, fgValue.resourceId)
        }
    }

    fun setValue(value: String) {
        bindings.txvValue.text = value
    }

    fun setDescription(description: String) {
        bindings.txvDescription.text = description
    }

    override fun setOnClickListener(newListener: OnClickListener?) {
        if(clickableButton) {
            bindings.rootLayout.setOnClickListener(newListener)
        }
    }

    companion object {

        @JvmStatic
        @BindingAdapter("customValue")
        fun customValue(view: ForecastCardView, value: String) {
            view.setValue(value)
        }

        @JvmStatic
        @BindingAdapter("precipitation")
        fun precipitation(view: ForecastCardView, value: Current?) {
            val valueStr = value?.let { info ->
                val popd = info.probabilityOfPrecipitation * 100
                val popl = popd.toLong()
                if(popd == popl.toDouble())
                    String.format("%d%s", popl, "%")
                else
                    String.format("%.1f%s", popd, "%")
            } ?: run {
                view.context.getString(R.string.not_available)
            }
            view.setValue(valueStr)
        }

        @JvmStatic
        @BindingAdapter("pressure")
        fun pressure(view: ForecastCardView, value: Current?) {
            val valueStr = value?.let { info ->
                String.format("%d hPa", info.pressure)
            } ?: run {
                view.context.getString(R.string.not_available)
            }
            view.setValue(valueStr)
        }

        @JvmStatic
        @BindingAdapter("windSpeed")
        fun windSpeed(view: ForecastCardView, value: Current?) {
            val valueStr = value?.let{info ->
                if(info.useMetricSystem) {
                    String.format("%.1f km/h", info.windSpeed * 3.6)
                } else {
                    String.format("%.1f mph", info.windSpeed)
                }
            } ?: run {
                view.context.getString(R.string.not_available)
            }
            val descriptionStr = value?.let { info ->
                when {
                    info.windDegrees >= 348.75 || info.windDegrees < 11.25 -> view.context.getString(R.string.north)
                    info.windDegrees >= 11.25 && info.windDegrees < 33.75 -> view.context.getString(R.string.north_northeast)
                    info.windDegrees >= 33.75 && info.windDegrees < 56.25 -> view.context.getString(R.string.northeast)
                    info.windDegrees >= 56.25 && info.windDegrees < 78.75 -> view.context.getString(R.string.east_northeast)
                    info.windDegrees >= 78.75 && info.windDegrees < 101.25 -> view.context.getString(R.string.east)
                    info.windDegrees >= 101.25 && info.windDegrees < 123.75 -> view.context.getString(R.string.east_southeast)
                    info.windDegrees >= 123.75 && info.windDegrees < 146.25 -> view.context.getString(R.string.southeast)
                    info.windDegrees >= 146.25 && info.windDegrees < 168.75 -> view.context.getString(R.string.south_southeast)
                    info.windDegrees >= 168.75 && info.windDegrees < 191.25 -> view.context.getString(R.string.south)
                    info.windDegrees >= 191.25 && info.windDegrees < 213.75 -> view.context.getString(R.string.south_southwest)
                    info.windDegrees >= 213.75 && info.windDegrees < 236.25 -> view.context.getString(R.string.southwest)
                    info.windDegrees >= 236.25 && info.windDegrees < 258.75 -> view.context.getString(R.string.west_southwest)
                    info.windDegrees >= 258.75 && info.windDegrees < 281.25 -> view.context.getString(R.string.west)
                    info.windDegrees >= 281.25 && info.windDegrees < 303.75 -> view.context.getString(R.string.west_northwest)
                    info.windDegrees >= 303.75 && info.windDegrees < 326.25 -> view.context.getString(R.string.northwest)
                    info.windDegrees >= 326.25 && info.windDegrees < 348.75 -> view.context.getString(R.string.north_northwest)
                    else -> view.context.getString(R.string.empty)
                }
            } ?: run {
                view.context.getString(R.string.not_available)
            }
            view.setValue(valueStr)
            view.setDescription(descriptionStr)
        }

        @JvmStatic
        @BindingAdapter("realFeel")
        fun realFeel(view: ForecastCardView, value: Current?) {
            val valueStr = value?.let{info ->
                if(info.useMetricSystem) {
                    String.format("%.1f°C", info.feelsLike)
                } else {
                    String.format("%.1f°F", info.feelsLike)
                }
            } ?: run {
                view.context.getString(R.string.not_available)
            }
            view.setValue(valueStr)
        }

        @JvmStatic
        @BindingAdapter("uvindex")
        fun uvindex(view: ForecastCardView, value: Current?) {
            val valueStr = value?.let { info ->
                String.format("%.2f", info.uvi)
            } ?: run {
                view.context.getString(R.string.not_available)
            }
            view.setValue(valueStr)
        }

        @JvmStatic
        @BindingAdapter("humidity")
        fun humidity(view: ForecastCardView, value: Current?) {
            val valueStr = value?.let { info ->
                String.format("%d%s", info.humidity, "%")
            } ?: run {
                view.context.getString(R.string.not_available)
            }
            view.setValue(valueStr)
        }

        @JvmStatic
        @BindingAdapter("visibility")
        fun visibility(view: ForecastCardView, value: Current?) {
            val valueStr = value?.let { info ->
                if(info.visibility < 1000) {
                    String.format("%d m", info.visibility)
                } else {
                    String.format("%.1f km", info.visibility.toFloat() / 1000f)
                }
            } ?: run {
                view.context.getString(R.string.not_available)
            }
            view.setValue(valueStr)
        }

    }

}