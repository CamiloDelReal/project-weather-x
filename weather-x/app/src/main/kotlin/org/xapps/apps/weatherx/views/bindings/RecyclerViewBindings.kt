package org.xapps.apps.weatherx.views.bindings

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import org.xapps.apps.weatherx.services.models.Hourly


object RecyclerViewBindings {

    @JvmStatic
    @BindingAdapter("entries")
    fun setEntries(view: RecyclerView, entries: List<Hourly>?) {
        entries?.let {
//            Timber.i("Checking adapter in list")
//            view.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
//            view.adapter = HourlySimpleAdapter(listOf())
//
//            val arrayAdapter = ArrayAdapter(view.context, R.layout.item_combobox_simple, entries)
//            arrayAdapter.setDropDownViewResource(R.layout.item_combobox_dropdown_simple)
//            view.setAdapter(arrayAdapter)
        }
    }

}