package com.example.snack4diet.calendar

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.snack4diet.R
import com.example.snack4diet.api.DayWithWeekday
import com.example.snack4diet.databinding.ItemDateBinding
import com.example.snack4diet.home.HomeFragment

class CalendarAdapter(
    private val dayList: MutableList<DayWithWeekday>,
    private var initialPosition: Int,
    private val context: Context,
    private val itemClickListener: HomeFragment.ItemClickListener): RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {

    inner class ViewHolder(binding: ItemDateBinding): RecyclerView.ViewHolder(binding.root) {
        val itemLayout = binding.itemLayout
        val day = binding.day
        val weekday = binding.weekday
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDateBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dayList[position]

        holder.day.text = item.day.toString()
        holder.weekday.text = item.weekday

        if (item.isClicked) {
            holder.day.setTextColor(ContextCompat.getColor(context, R.color.orange))
            holder.weekday.setTextColor(ContextCompat.getColor(context, R.color.orange))
        } else {
            holder.day.setTextColor(ContextCompat.getColor(context, R.color.black))
            holder.weekday.setTextColor(ContextCompat.getColor(context, R.color.black))
        }

        holder.itemLayout.setOnClickListener {
            itemClickListener.onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return dayList.size
    }
}