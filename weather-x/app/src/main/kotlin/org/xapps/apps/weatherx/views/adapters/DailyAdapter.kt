package org.xapps.apps.weatherx.views.adapters

import androidx.databinding.ViewDataBinding
import org.xapps.apps.weatherx.R
import org.xapps.apps.weatherx.databinding.ItemDailyInfoBinding
import org.xapps.apps.weatherx.services.models.Daily


class DailyInfoSimpleAdapter(items: List<Daily>) : ListBindingAdapter<Daily>(items) {

    override val itemLayout: Int = R.layout.item_daily_info

    override fun bind(binding: ViewDataBinding, item: Daily) {
        when (binding) {
            is ItemDailyInfoBinding -> {
                binding.data = item
            }
        }
    }
}