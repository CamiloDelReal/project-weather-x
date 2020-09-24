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
import kotlinx.android.synthetic.main.content_card_forecast.view.*
import org.xapps.apps.weatherx.R
import kotlin.math.min


class ForecastCard(context: Context, attrs: AttributeSet) :
    FrameLayout(context, attrs), ViewTreeObserver.OnPreDrawListener {
    private var mStickyWidth = STICKY_WIDTH_UNDEFINED

    private var clickableButton = false

    private fun init(context: Context, attrs: AttributeSet) {
        val a: TypedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ForecastCard)
        val icon = a.getResourceId(R.styleable.ForecastCard_fc_icon, -1)
        val iconTint = a.getColor(R.styleable.ForecastCard_fc_iconTint, -1)
        val iconSize = a.getDimension(R.styleable.ForecastCard_fc_iconSize, 32f)
        val value = a.getString(R.styleable.ForecastCard_fc_value)
        val description = a.getString(R.styleable.ForecastCard_fc_description)
        clickableButton = a.getBoolean(R.styleable.ForecastCard_fc_clickable, false)
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
    }

    init {
        init(context, attrs)
    }
}