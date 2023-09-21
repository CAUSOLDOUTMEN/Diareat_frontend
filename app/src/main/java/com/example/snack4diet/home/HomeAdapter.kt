package com.example.snack4diet.home

import android.content.Context
import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.snack4diet.R
import com.example.snack4diet.api.NutritionItem
import com.example.snack4diet.databinding.ItemMacroNutrientsBinding

class HomeAdapter(private val nutrients: List<NutritionItem>, private val context: Context): RecyclerView.Adapter<HomeAdapter.ViewHolder> () {

    inner class ViewHolder(binding: ItemMacroNutrientsBinding): RecyclerView.ViewHolder(binding.root) {
        val itemLayout = binding.itemLayout
        val nutrient = binding.nutrient
        val dailyIntake = binding.dailyIntake
        val targetIntake = binding.targetIntake
        val percent = binding.percent
        val progressbar = binding.progressBar
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMacroNutrientsBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = nutrients[position]

        if (item.nutritionItem == "kcal") {
            holder.nutrient.text = "칼로리"
            holder.itemLayout.setBackgroundResource(R.drawable.round_frame_kcal)
        } else if (item.nutritionItem == "carbohydrate") {
            holder.nutrient.text = "탄수화물"
            holder.itemLayout.setBackgroundResource(R.drawable.round_frame_carbohydrate)
        } else if (item.nutritionItem == "protein") {
            holder.nutrient.text = "단백질"
            holder.itemLayout.setBackgroundResource(R.drawable.round_frame_protein)
        } else if (item.nutritionItem == "province") {
            holder.nutrient.text = "지방"
            holder.itemLayout.setBackgroundResource(R.drawable.round_frame_province)
        }

        holder.dailyIntake.text = item.consumedAmount.toString()
        holder.targetIntake.text = item.targetAmount.toString()

        if (item.consumedAmount > item.targetAmount) {
            holder.percent.text = "초과"
            holder.percent.setTextColor(ContextCompat.getColor(context, R.color.red))
            holder.progressbar.progress = 100
            holder.progressbar.progressTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red))
        } else {
            holder.percent.text = (item.consumedAmount * 100 / item.targetAmount).toString() + "%"
            holder.progressbar.progress = (item.consumedAmount * 100 / item.targetAmount)
        }
    }

    override fun getItemCount(): Int {
        return nutrients.size
    }
}