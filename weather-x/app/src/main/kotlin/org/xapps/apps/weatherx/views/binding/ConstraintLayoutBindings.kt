package org.xapps.apps.weatherx.views.binding

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import org.xapps.apps.weatherx.R
import org.xapps.apps.weatherx.services.models.Current


object ConstraintLayoutBindings {

    @JvmStatic
    @BindingAdapter("conditionBackground")
    fun conditionBackground(view: ConstraintLayout, data: Current?) {
        data?.let {

        }
    }

    fun conditionBackground(code: Int, isDayLight: Boolean): Int {
        return when(code) {
            else -> R.drawable.ic_gradient_day_clear
        }
    }

}