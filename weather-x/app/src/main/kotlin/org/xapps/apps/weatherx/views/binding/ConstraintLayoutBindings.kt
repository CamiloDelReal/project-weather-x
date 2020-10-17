package org.xapps.apps.weatherx.views.binding

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import org.xapps.apps.weatherx.services.models.Current


object ConstraintLayoutBindings {

    @JvmStatic
    @BindingAdapter("conditionBackground")
    fun conditionBackground(view: ConstraintLayout, data: Current?) {
        data?.let {

        }
    }

}