package org.xapps.apps.weatherx.views.adapters

import androidx.databinding.ViewDataBinding
import org.xapps.apps.weatherx.services.models.HourlyInfo
import org.xapps.apps.weatherx.R
import org.xapps.apps.weatherx.databinding.ItemHourlyInfoBinding


class HourlySimpleInfoAdapter(items: List<HourlyInfo>) : ListBindingAdapter<HourlyInfo>(items) {

    override val itemLayout: Int = R.layout.item_hourly_simple_info

    override fun bind(binding: ViewDataBinding, item: HourlyInfo) {
        when (binding) {
            is ItemHourlyInfoBinding -> {
                binding.data = item
            }
        }
    }
}