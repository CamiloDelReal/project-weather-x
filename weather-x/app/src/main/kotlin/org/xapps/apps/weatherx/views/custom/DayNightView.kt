package org.xapps.apps.weatherx.views.custom


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.databinding.BindingAdapter
import org.xapps.apps.weatherx.R
import org.xapps.apps.weatherx.services.models.Current
import java.util.*


class DayNightView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyle) {

    private val a = attrs?.let {
        context.obtainStyledAttributes(
            attrs,
            R.styleable.DayNightView,
            defStyle,
            defStyleRes
        )
    }

    var maxProgress = a.useOrDefault(100) { getInteger(R.styleable.DayNightView_dnv_maxProgress, it) }
        set(progress) {
            field = bound(0, progress, Int.MAX_VALUE)
            drawData?.let { drawData = it.copy(maxProgress = progress) }
            invalidate()
        }

    var progress: Int = a.useOrDefault(0) { getInteger(R.styleable.DayNightView_dnv_progress, it) }
        set(progress) {
            field = bound(0, progress, maxProgress)
            drawData?.let { drawData = it.copy(progress = progress) }
            invalidate()
        }

    var progressWidth: Float = a.useOrDefault(4 * context.resources.displayMetrics.density) {
        getDimension(
            R.styleable.DayNightView_dnv_progressWidth,
            it
        )
    }
        set(value) {
            field = value
            progressPaint.strokeWidth = value
        }

    var progressBackgroundWidth: Float = a.useOrDefault(2F) {
        getDimension(
            R.styleable.DayNightView_dnv_progressBackgroundWidth,
            it
        )
    }
        set(mArcWidth) {
            field = mArcWidth
            progressBackgroundPaint.strokeWidth = mArcWidth
        }

    var progressColor: Int
        get() = progressPaint.color
        set(color) {
            progressPaint.color = color
            invalidate()
        }

    var progressBackgroundColor: Int
        get() = progressBackgroundPaint.color
        set(color) {
            progressBackgroundPaint.color = color
            invalidate()
        }

    private val thumb: Drawable = a?.getDrawable(R.styleable.DayNightView_dnv_drawable)!!

    private var roundedEdges = a.useOrDefault(true) {
        getBoolean(
            R.styleable.DayNightView_dnv_roundEdges,
            it
        )
    }
        set(value) {
            if (value) {
                progressBackgroundPaint.strokeCap = Paint.Cap.ROUND
                progressPaint.strokeCap = Paint.Cap.ROUND
            } else {
                progressBackgroundPaint.strokeCap = Paint.Cap.SQUARE
                progressPaint.strokeCap = Paint.Cap.SQUARE
            }
            field = value
        }

    private var progressBackgroundPaint: Paint = makeProgressPaint(
        color = a.useOrDefault(context.getColor(android.R.color.darker_gray)) {
            getColor(
                R.styleable.DayNightView_dnv_progressBackgroundColor,
                it
            )
        },
        width = progressBackgroundWidth,
        useDash = true
    )

    private var progressPaint: Paint = makeProgressPaint(
        color = a.useOrDefault(context.getColor(android.R.color.holo_blue_light)) {
            getColor(
                R.styleable.DayNightView_dnv_progressColor,
                it
            )
        },
        width = progressWidth,
        useDash = false
    )

    init {
        a?.recycle()
    }

    private var drawerDataObservers: List<(DayNightViewData) -> Unit> = emptyList()

    private fun doWhenDrawerDataAreReady(f: (DayNightViewData) -> Unit) {
        if (drawData != null) f(drawData!!) else drawerDataObservers += f
    }

    private var drawData: DayNightViewData? = null
        set(value) {
            field = value ?: return
            val temp = drawerDataObservers.toList()
            temp.forEach { it(value) }
            drawerDataObservers -= temp
        }

    override fun onDraw(canvas: Canvas) {
        drawData?.run {
            canvas.drawArc(arcRect, startAngle, sweepAngle, false, progressBackgroundPaint)
            canvas.drawArc(arcRect, startAngle, progressSweepAngle, false, progressPaint)
            drawThumb(canvas)
        }
    }

    private fun DayNightViewData.drawThumb(canvas: Canvas) {
        val thumbHalfHeight = thumb.intrinsicHeight / 2
        val thumbHalfWidth = thumb.intrinsicWidth / 2
        thumb.setBounds(
            thumbX - thumbHalfWidth,
            thumbY - thumbHalfHeight,
            thumbX + thumbHalfWidth,
            thumbY + thumbHalfHeight
        )
        thumb.draw(canvas)
    }

    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height = View.getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)
        val width = View.getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        val dx = maxOf(thumb.intrinsicWidth.toFloat() / 2, this.progressWidth) + 2
        val dy = maxOf(thumb.intrinsicHeight.toFloat() / 2, this.progressWidth) + 2
        val realWidth = width.toFloat() - 2 * dx - paddingLeft - paddingRight
        val realHeight = minOf(
            height.toFloat() - 2 * dy - paddingTop - paddingBottom,
            realWidth / 2
        )
        drawData = DayNightViewData(
            dx + paddingLeft,
            dy + paddingTop,
            realWidth,
            realHeight,
            progress,
            maxProgress
        )
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        if (thumb.isStateful) {
            thumb.state = drawableState
        }
        invalidate()
    }

    fun setProgressBackgroundGradient(vararg colors: Int) {
        setGradient(progressBackgroundPaint, *colors)
    }

    fun setProgressGradient(vararg colors: Int) {
        setGradient(progressPaint, *colors)
    }

    private fun setGradient(paint: Paint, vararg colors: Int) {
        doWhenDrawerDataAreReady {
            paint.shader = LinearGradient(
                it.dx,
                0F,
                it.width,
                0F,
                colors,
                null,
                Shader.TileMode.CLAMP
            )
        }
        invalidate()
    }

    private fun makeProgressPaint(color: Int, width: Float, useDash: Boolean = false) = Paint().apply {
        this.color = color
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = width
        if(useDash)
            pathEffect = DashPathEffect(floatArrayOf(10f, 20f), 0f)
        if (roundedEdges)
            strokeCap = Paint.Cap.ROUND
    }

    fun <T, R> T?.useOrDefault(default: R, usage: T.(R) -> R) =
        if (this == null) default else usage(
            default
        )

    companion object {

        fun <T : Number> bound(min: T, value: T, max: T) = when {
            value.toDouble() > max.toDouble() -> max
            value.toDouble() < min.toDouble() -> min
            else -> value
        }

        @JvmStatic
        @BindingAdapter("progress")
        fun progress(view: DayNightView, value: Current?) {
            val percent = value?.let { info ->
                val total = info.sunset - info.sunrise
                val part = Date().time - info.sunrise
                val percent = (part * 100) / total
                if (percent > 100) 100 else percent
            } ?: run {
                0L
            }
            view.progress = percent.toInt()
        }

    }

    data class DayNightViewData(
        val dx: Float,
        val dy: Float,
        val width: Float,
        val height: Float,
        val progress: Int,
        val maxProgress: Int
    ) {
        private val pi = Math.PI.toFloat()
        private val zero = 0.0001F
        val r: Float = height / 2 + width * width / 8 / height
        private val circleCenterX: Float = width / 2 + dy
        private val circleCenterY: Float = r + dx
        private val alphaRad: Float =
            bound(zero, Math.acos((r - height).toDouble() / r).toFloat(), 2 * pi)
        val arcRect: RectF =
            RectF(circleCenterX - r, circleCenterY - r, circleCenterX + r, circleCenterY + r)
        val startAngle: Float = bound(180F, 270 - alphaRad / 2 / pi * 360F, 360F)
        val sweepAngle: Float = bound(zero, (2F * alphaRad) / 2 / pi * 360F, 180F)
        val progressSweepRad = if (maxProgress == 0) zero else bound(
            zero,
            progress.toFloat() / maxProgress * 2 * alphaRad,
            2 * pi
        )
        val progressSweepAngle: Float = progressSweepRad / 2 / pi * 360F
        val thumbX: Int = (r * Math.cos(alphaRad + Math.PI / 2 - progressSweepRad)
            .toFloat() + circleCenterX).toInt()
        val thumbY: Int = (-r * Math.sin(alphaRad + Math.PI / 2 - progressSweepRad)
            .toFloat() + circleCenterY).toInt()
    }

}