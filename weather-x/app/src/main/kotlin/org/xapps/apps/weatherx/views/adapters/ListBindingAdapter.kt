package org.xapps.apps.weatherx.views.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableList
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView


abstract class ListBindingAdapter<TItem>(items: List<TItem>) :
    RecyclerView.Adapter<ListBindingAdapter.BindingViewHolder>() {

    var items: List<TItem> = ArrayList()
        set(value) {
            val lastList = field
            if (lastList !== value) {
                if (lastList is ObservableList) {
                    lastList.removeOnListChangedCallback(onListChangedCallback)
                }
                if (value is ObservableList) {
                    value.addOnListChangedCallback(onListChangedCallback)
                }
                if (lastList.isNotEmpty()) {
                    notifyItemRangeRemoved(0, lastList.size)
                }
            }
            field = value
            notifyItemRangeInserted(0, value.size)
        }

    class BindingViewHolder constructor(val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder {
        val binding = createBinding(parent)
        return BindingViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    private fun createBinding(parent: ViewGroup): ViewDataBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            itemLayout,
            parent,
            false
        )
    }

    abstract val itemLayout: Int

    override fun onBindViewHolder(holder: BindingViewHolder, position: Int) {
        if (position < itemCount) {
            bind(holder.binding, getItem(position))
            holder.binding.executePendingBindings()
        }
    }

    private fun getItem(position: Int): TItem {
        return items[position]
    }

    protected abstract fun bind(binding: ViewDataBinding, item: TItem)

    private val onListChangedCallback =
        object : ObservableList.OnListChangedCallback<ObservableList<TItem>>() {
            override fun onChanged(sender: ObservableList<TItem>?) {
                notifyDataSetChanged()
            }

            override fun onItemRangeRemoved(
                sender: ObservableList<TItem>?,
                positionStart: Int,
                itemCount: Int
            ) {
                notifyItemRangeRemoved(positionStart, itemCount)
            }

            override fun onItemRangeMoved(
                sender: ObservableList<TItem>?,
                fromPosition: Int,
                toPosition: Int,
                itemCount: Int
            ) {
                notifyItemMoved(fromPosition, toPosition)
            }

            override fun onItemRangeInserted(
                sender: ObservableList<TItem>?,
                positionStart: Int,
                itemCount: Int
            ) {
                notifyItemRangeChanged(positionStart, itemCount)
            }

            override fun onItemRangeChanged(
                sender: ObservableList<TItem>?,
                positionStart: Int,
                itemCount: Int
            ) {
                notifyItemRangeChanged(positionStart, itemCount)
            }
        }

    init {
        this.items = items
    }
}