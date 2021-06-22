package org.xapps.apps.weatherx.views.adapters

import androidx.databinding.ViewDataBinding
import org.xapps.apps.weatherx.R
import org.xapps.apps.weatherx.databinding.ItemHourlySimpleInfoBinding
import org.xapps.apps.weatherx.core.models.Hourly


class HourlySimpleAdapter(items: List<Hourly>) : ListBindingAdapter<Hourly>(items) {

    override val itemLayout: Int = R.layout.item_hourly_simple_info

    override fun bind(binding: ViewDataBinding, item: Hourly) {
        when (binding) {
            is ItemHourlySimpleInfoBinding -> {
                binding.data = item
            }
        }
    }
}