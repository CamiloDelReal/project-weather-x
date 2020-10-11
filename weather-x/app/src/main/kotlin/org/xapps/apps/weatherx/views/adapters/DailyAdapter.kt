package org.xapps.apps.weatherx.views.adapters

import androidx.databinding.ViewDataBinding
import org.xapps.apps.weatherx.R
import org.xapps.apps.weatherx.databinding.ItemDailyInfoBinding
import org.xapps.apps.weatherx.services.models.DailyInfo


class DailyInfoSimpleAdapter(items: List<DailyInfo>) : ListBindingAdapter<DailyInfo>(items) {

    override val itemLayout: Int = R.layout.item_daily_info

    override fun bind(binding: ViewDataBinding, item: DailyInfo) {
        when (binding) {
            is ItemDailyInfoBinding -> {
                binding.data = item
            }
        }
    }
}