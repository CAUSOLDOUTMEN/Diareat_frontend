package com.example.snack4diet.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.snack4diet.api.Macronutrients
import com.example.snack4diet.databinding.ItemDiaryBinding

class DiaryAdapter(private val nutrients: List<Macronutrients>): RecyclerView.Adapter<DiaryAdapter.ViewHolder> () {

    inner class ViewHolder(binding: ItemDiaryBinding): RecyclerView.ViewHolder(binding.root) {
        val kcal = binding.kcal
        val protein = binding.protein
        val province = binding.province
        val carbohydrate = binding.carbohydrate
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDiaryBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = nutrients[position]

        holder.kcal.text = item.kcal.toString()
        holder.protein.text = item.protein.toString()
        holder.province.text = item.province.toString()
        holder.carbohydrate.text = item.carbohydrate.toString()
    }

    override fun getItemCount(): Int {
        return nutrients.size
    }
}