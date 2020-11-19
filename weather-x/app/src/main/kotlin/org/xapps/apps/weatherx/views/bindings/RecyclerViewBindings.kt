package org.xapps.apps.weatherx.views.bindings

import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableArrayList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.xapps.apps.weatherx.services.models.Daily
import org.xapps.apps.weatherx.services.models.Hourly
import org.xapps.apps.weatherx.views.adapters.DailyAdapter
import org.xapps.apps.weatherx.views.adapters.HourlyAdapter
import org.xapps.apps.weatherx.views.adapters.HourlySimpleAdapter


object RecyclerViewBindings {

    @JvmStatic
    @BindingAdapter("hourlySimple")
    fun setHourlySimpleEntries(view: RecyclerView, entries: ObservableArrayList<Hourly>?) {
        entries?.let {
            view.layoutManager = LinearLayoutManager(
                view.context,
                RecyclerView.HORIZONTAL,
                false
            )
            view.adapter = HourlySimpleAdapter(it)
        }
    }

    @JvmStatic
    @BindingAdapter("hourly")
    fun setHourlyEntries(view: RecyclerView, entries: ObservableArrayList<Hourly>?) {
        entries?.let {
            view.layoutManager = LinearLayoutManager(
                view.context,
                RecyclerView.HORIZONTAL,
                false
            )
            view.adapter = HourlyAdapter(it)
        }
    }

    @JvmStatic
    @BindingAdapter("daily")
    fun setDailyEntries(view: RecyclerView, entries: ObservableArrayList<Daily>?) {
        entries?.let {
            view.layoutManager = LinearLayoutManager(
                view.context,
                RecyclerView.HORIZONTAL,
                false
            )
            view.adapter = DailyAdapter(it)
        }
    }

}