package org.xapps.apps.weatherx.views.bindings

import android.view.View
import android.widget.LinearLayout
import androidx.databinding.BindingAdapter
import org.xapps.apps.weatherx.core.models.Daily


object LinearLayoutBindings {

    @JvmStatic
    @BindingAdapter("visibility")
    fun visibility(layout: LinearLayout, value: Daily?) {
        val visibilityValue = value?.let { info ->
            if (info.visibility == -1) {
                View.GONE
            }  else {
                View.VISIBLE
            }
        } ?: run {
            View.GONE
        }
        layout.visibility = visibilityValue
    }

}