package com.example.todolistapp

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todolistapp.databinding.ListItemBinding
import com.example.todolistapp.dataclasses.Item

class ListAdapter(
    private val onLayoutClickListener: MainFragment
) : RecyclerView.Adapter<ListAdapter.ItemHolder>() {
    private val itemsList = ArrayList<Item>()

    inner class ItemHolder(view: View): RecyclerView.ViewHolder(view) {
        private val binding = ListItemBinding.bind(view)

        fun bind(item: Item) = with(binding) {
            thingTextView.text = item.thing
            timeTextView.text = item.time
            dateTextView.text = item.date

            itemHolder.setOnClickListener {
                onLayoutClickListener.onLayoutClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.list_item, parent, false
        )
        return ItemHolder(view)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.bind(itemsList[position])
    }

    override fun getItemCount(): Int = itemsList.size

    @SuppressLint("NotifyDataSetChanged")
    fun addItem(item: Item) {
        itemsList.add(item)
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        itemsList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun replaceItem(position: Int, item: Item) {
        itemsList.removeAt(position)
        itemsList.add(position, item)
    }

    fun removeItemByData(item: Item) {
        for (index in itemsList.indices) {
            if (itemsList[index].thing == item.thing &&
                itemsList[index].time == item.time &&
                itemsList[index].date == item.date) {

                itemsList.removeAt(index)
                notifyItemRemoved(index)

                break
            }
        }
    }

    interface OnLayoutClickListener {
        fun onLayoutClick(item: Item)
    }
}