package com.example.snack4diet.analysis

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.snack4diet.api.Macronutrients
import com.example.snack4diet.databinding.ItemBestWorstBinding

class BestWorstAdapter(private val items: List<Macronutrients>): RecyclerView.Adapter<BestWorstAdapter.ViewHolder> () {

    inner class ViewHolder(binding: ItemBestWorstBinding): RecyclerView.ViewHolder(binding.root){
        val foodName = binding.foodName
        val kcal = binding.kcal
        val carbohydrate = binding.carbohydrate
        val protein = binding.protein
        val province = binding.province
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBestWorstBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.foodName.text = item.foodName
        holder.kcal.text = item.kcal.toString() + "kcal"
        holder.carbohydrate.text = item.carbohydrate.toString() + "g"
        holder.protein.text = item.protein.toString() + "g"
        holder.province.text = item.province.toString() + "g"
    }

    override fun getItemCount(): Int {
        return items.size
    }
}