package org.xapps.apps.weatherx.views.custom

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.widget.ImageViewCompat
import androidx.databinding.BindingAdapter
import kotlinx.android.synthetic.main.content_card_forecast.view.*
import org.xapps.apps.weatherx.R
import org.xapps.apps.weatherx.services.models.Current
import java.util.*
import kotlin.math.min


class ForecastCardView(context: Context, attrs: AttributeSet) :
    FrameLayout(context, attrs), ViewTreeObserver.OnPreDrawListener {
    private var mStickyWidth = STICKY_WIDTH_UNDEFINED

    private var clickableButton = false

    private fun init(context: Context, attrs: AttributeSet) {
        val a: TypedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ForecastCardView)
        val icon = a.getResourceId(R.styleable.ForecastCardView_fc_icon, -1)
        val iconTint = a.getColor(R.styleable.ForecastCardView_fc_iconTint, -1)
        val iconSize = a.getDimension(R.styleable.ForecastCardView_fc_iconSize, 32f)
        val value = a.getString(R.styleable.ForecastCardView_fc_value)
        val description = a.getString(R.styleable.ForecastCardView_fc_description)
        clickableButton = a.getBoolean(R.styleable.ForecastCardView_fc_clickable, false)
        a.recycle()

        inflate(context, R.layout.content_card_forecast, this)

        if(icon != -1) {
            imgIcon.setImageResource(icon)
            ImageViewCompat.setImageTintList(imgIcon, ColorStateList.valueOf(iconTint))
            val iconSizeInDp = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                iconSize,
                resources.displayMetrics
            ).toInt()
            imgIcon.layoutParams.height = iconSizeInDp
            imgIcon.layoutParams.width = iconSizeInDp
        }
        txvValue.text = value
        txvDescription.text = description

        if(clickableButton) {
            val fgValue = TypedValue()
            getContext().theme.resolveAttribute(
                android.R.attr.selectableItemBackground,
                fgValue,
                true
            )
            rootLayout.foreground = AppCompatResources.getDrawable(context, fgValue.resourceId)
        }
    }

    fun setValue(value: String) {
        txvValue.text = value
    }

    fun setDescription(description: String) {
        txvDescription.text = description
    }

    override fun setOnClickListener(newListener: OnClickListener?) {
        if(clickableButton) {
            rootLayout.setOnClickListener(newListener)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredHeight = 10000
        val desiredWidth: Int
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val width: Int
        val height: Int
        height = when (heightMode) {
            MeasureSpec.EXACTLY -> {
                heightSize
            }
            MeasureSpec.AT_MOST -> {
                min(desiredHeight, heightSize)
            }
            else -> {
                desiredHeight
            }
        }
        desiredWidth = when {
            mStickyWidth != STICKY_WIDTH_UNDEFINED -> {
                mStickyWidth
            }
            height > BREAK_HEIGHT -> {
                ARBITRARY_WIDTH_LESSER
            }
            else -> {
                ARBITRARY_WIDTH_GREATER
            }
        }
        width = when (widthMode) {
            MeasureSpec.EXACTLY -> {
                widthSize
            }
            MeasureSpec.AT_MOST -> {
                min(desiredWidth, widthSize)
            }
            else -> {
                desiredWidth
            }
        }
        setMeasuredDimension(width, height)
        val childCount = childCount
        for (i in 0 until childCount) {
            val v: View = getChildAt(i)
            v.measure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val w = right - left
        val h = bottom - top
        super.onLayout(changed, left, top, right, bottom)
        if (h <= BREAK_HEIGHT && w < ARBITRARY_WIDTH_GREATER
            && mStickyWidth == STICKY_WIDTH_UNDEFINED
        ) {
            mStickyWidth = ARBITRARY_WIDTH_GREATER
            viewTreeObserver.addOnPreDrawListener(this)
        } else {
            mStickyWidth = STICKY_WIDTH_UNDEFINED
        }
    }

    override fun onPreDraw(): Boolean {
        viewTreeObserver.removeOnPreDrawListener(this)
        if (mStickyWidth == STICKY_WIDTH_UNDEFINED) {
            return true
        }
        requestLayout()
        return false
    }

    companion object {
        private const val STICKY_WIDTH_UNDEFINED = -1
        private const val BREAK_HEIGHT = 1950
        private const val ARBITRARY_WIDTH_LESSER = 200
        private const val ARBITRARY_WIDTH_GREATER = 800


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
                    String.format(Locale.US, "%.1f%s", popd, "%")
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
                if(info.useMetric) {
                    String.format(Locale.US, "%.1f km/h", info.windSpeed * 3.6)
                } else {
                    String.format(Locale.US, "%.1f mph", info.windSpeed)
                }
            } ?: run {
                view.context.getString(R.string.not_available)
            }
            val descriptionStr = value?.let { info ->
                when {
                    info.windDegrees >= 348.75 && info.windDegrees < 11.25 -> view.context.getString(R.string.north)
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
                if(info.useMetric) {
                    String.format(Locale.US, "%.1f °C", info.feelsLike)
                } else {
                    String.format(Locale.US, "%.1f °F", info.feelsLike)
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
                String.format(Locale.US, "%.2f", info.uvi)
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
                    String.format(Locale.US, "%.1f km", info.visibility.toFloat() / 1000f)
                }
            } ?: run {
                view.context.getString(R.string.not_available)
            }
            view.setValue(valueStr)
        }

    }

    init {
        init(context, attrs)
    }
}