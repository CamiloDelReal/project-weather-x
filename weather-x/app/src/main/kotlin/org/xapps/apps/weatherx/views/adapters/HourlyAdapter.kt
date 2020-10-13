package org.xapps.apps.weatherx.views.adapters

import androidx.databinding.ViewDataBinding
import org.xapps.apps.weatherx.R
import org.xapps.apps.weatherx.databinding.ItemHourlyInfoBinding
import org.xapps.apps.weatherx.services.models.Hourly


class HourlyAdapter(items: List<Hourly>) : ListBindingAdapter<Hourly>(items) {

    override val itemLayout: Int = R.layout.item_hourly_info

    override fun bind(binding: ViewDataBinding, item: Hourly) {
        when (binding) {
            is ItemHourlyInfoBinding -> {
                binding.data = item
            }
        }
    }

}