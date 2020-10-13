package org.xapps.apps.weatherx.views.binding

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_home.*
import org.xapps.apps.weatherx.services.models.Hourly
import org.xapps.apps.weatherx.views.adapters.HourlySimpleAdapter
import timber.log.Timber


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