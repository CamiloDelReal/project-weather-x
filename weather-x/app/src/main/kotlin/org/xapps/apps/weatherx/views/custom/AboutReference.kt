package org.xapps.apps.weatherx.views.custom

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources
import kotlinx.android.synthetic.main.content_about_reference.view.*
import org.xapps.apps.weatherx.R
import org.xapps.apps.weatherx.databinding.ContentAboutReferenceBinding


class AboutReference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyle, defStyleRes) {

    init {
        val a: TypedArray = getContext().obtainStyledAttributes(attrs, R.styleable.AboutReference)
        val icon = a.getResourceId(R.styleable.AboutReference_ar_icon, -1)
        val text = a.getString(R.styleable.AboutReference_ar_text)
        val link = a.getString(R.styleable.AboutReference_ar_link)
        a.recycle()

        val layoutInflater = LayoutInflater.from(context)
        ContentAboutReferenceBinding.inflate(layoutInflater, this, true)

        if (icon != -1) {
            imgIcon.setImageResource(icon)
        }

        txvText.text = text
        txvLink.text = link
        imgActionable.visibility = View.GONE
    }

    fun setText(text: String) {
        txvText.text = text
    }

    fun setLink(link: String) {
        txvLink.text = link
    }

    override fun setOnClickListener(newListener: OnClickListener?) {
        val fgValue = TypedValue()
        getContext().theme.resolveAttribute(
            android.R.attr.selectableItemBackground,
            fgValue,
            true
        )
        rootLayout.foreground = AppCompatResources.getDrawable(context, fgValue.resourceId)
        imgActionable.visibility = View.VISIBLE
        rootLayout.setOnClickListener(newListener)
    }

}