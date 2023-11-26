package com.example.foodfood.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.foodfood.R
import com.example.foodfood.api.NutritionItem
import com.example.foodfood.databinding.ItemMacroNutrientsBinding

class HomeAdapter(private val nutrients: List<NutritionItem>, private val context: Context): RecyclerView.Adapter<HomeAdapter.ViewHolder> () {

    inner class ViewHolder(binding: ItemMacroNutrientsBinding): RecyclerView.ViewHolder(binding.root) {
        val itemLayout = binding.itemLayout
        val nutrient = binding.nutrient
        val dailyIntake = binding.dailyIntake
        val percent = binding.percent
        val nutrientImage = binding.nutrientImage
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMacroNutrientsBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = nutrients[position]

        if (item.nutritionItem == "kcal") {
            holder.nutrient.text = "칼로리"
            holder.itemLayout.setBackgroundResource(R.drawable.round_frame_white_20_stroke_blue)
            holder.nutrientImage.setImageResource(R.drawable.ic_kcal)
            holder.dailyIntake.text = item.consumedAmount.toString() + "kcal / " + item.targetAmount.toString() + "kcal"
        } else if (item.nutritionItem == "carbohydrate") {
            holder.nutrient.text = "탄수화물"
            holder.itemLayout.setBackgroundResource(R.drawable.round_frame_white_20_stroke_yellow)
            holder.nutrientImage.setImageResource(R.drawable.ic_carbohydrate)
            holder.dailyIntake.text = item.consumedAmount.toString() + "g / " + item.targetAmount.toString() + "g"
        } else if (item.nutritionItem == "protein") {
            holder.nutrient.text = "단백질"
            holder.itemLayout.setBackgroundResource(R.drawable.round_frame_white_20_stroke_red)
            holder.nutrientImage.setImageResource(R.drawable.ic_protein)
            holder.dailyIntake.text = item.consumedAmount.toString() + "g / " + item.targetAmount.toString() + "g"
        } else if (item.nutritionItem == "province") {
            holder.nutrient.text = "지방"
            holder.itemLayout.setBackgroundResource(R.drawable.round_frame_white_20_stroke_gray)
            holder.nutrientImage.setImageResource(R.drawable.ic_province)
            holder.dailyIntake.text = item.consumedAmount.toString() + "g / " + item.targetAmount.toString() + "g"
        }

        if (item.consumedAmount > item.targetAmount) {
            holder.percent.text = "초과"
            holder.percent.setTextColor(ContextCompat.getColor(context, R.color.red))
        } else {
            holder.percent.text = (item.consumedAmount * 100 / item.targetAmount).toString() + "%"
        }
    }

    override fun getItemCount(): Int {
        return nutrients.size
    }
}