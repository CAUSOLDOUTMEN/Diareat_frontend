package com.example.foodfood.analysis

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.foodfood.R
import com.example.foodfood.api.bestWorst.Worst
import com.example.foodfood.databinding.ItemBestWorstBinding

class WorstAdapter(private val items: List<Worst>, private val context: Context): RecyclerView.Adapter<WorstAdapter.ViewHolder> () {

    inner class ViewHolder(binding: ItemBestWorstBinding): RecyclerView.ViewHolder(binding.root){
        val foodName = binding.foodName
        val kcal = binding.kcal
        val carbohydrate = binding.carbohydrate
        val protein = binding.protein
        val fat = binding.fat
        val date = binding.date
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBestWorstBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.foodName.text = item.name
        holder.kcal.text = item.calorie.toString() + "kcal"
        holder.carbohydrate.text = item.carbohydrate.toString() + "g"
        holder.protein.text = item.protein.toString() + "g"
        holder.fat.text = item.fat.toString() + "g"
        holder.fat.setTextColor(ContextCompat.getColor(context, R.color.red))
        holder.fat.setTypeface(holder.fat.typeface, Typeface.BOLD)
        holder.date.text = item.date
    }

    override fun getItemCount(): Int {
        return items.size
    }
}