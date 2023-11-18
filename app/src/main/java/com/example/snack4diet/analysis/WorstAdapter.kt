package com.example.snack4diet.analysis

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.snack4diet.api.bestWorst.Best
import com.example.snack4diet.api.bestWorst.Worst
import com.example.snack4diet.databinding.ItemBestWorstBinding

class WorstAdapter(private val items: List<Worst>): RecyclerView.Adapter<WorstAdapter.ViewHolder> () {

    inner class ViewHolder(binding: ItemBestWorstBinding): RecyclerView.ViewHolder(binding.root){
        val foodName = binding.foodName
        val kcal = binding.kcal
        val carbohydrate = binding.carbohydrate
        val protein = binding.protein
        val province = binding.province
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
        holder.province.text = item.fat.toString() + "g"
        holder.date.text = item.date
    }

    override fun getItemCount(): Int {
        return items.size
    }
}