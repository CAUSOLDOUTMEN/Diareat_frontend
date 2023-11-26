package com.example.foodfood.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foodfood.R
import com.example.foodfood.api.foodOnDate.Data
import com.example.foodfood.databinding.ItemDiaryBinding

class DiaryAdapter(private var nutrients: List<Data>, private val itemClickListener: (Data) -> Unit): RecyclerView.Adapter<DiaryAdapter.ViewHolder> () {
    private var onItemClickCallback: ((Long) -> Unit)? = null

    fun setOnItemClickListener(callback: (Long) -> Unit) {
        onItemClickCallback = callback
    }

    private fun onItemClick(position: Long) {
        onItemClickCallback?.invoke(position)
    }

    inner class ViewHolder(binding: ItemDiaryBinding): RecyclerView.ViewHolder(binding.root) {
        val foodName = binding.foodName
        val kcal = binding.kcal
        val protein = binding.protein
        val province = binding.province
        val carbohydrate = binding.carbohydrate
        val btnBookmark = binding.btnBookmark
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDiaryBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = nutrients[position]

        holder.foodName.text = item.name
        holder.kcal.text = item.baseNutrition.kcal.toString() + "kcal"
        holder.protein.text = item.baseNutrition.protein.toString() + "g"
        holder.province.text = item.baseNutrition.fat.toString() + "g"
        holder.carbohydrate.text = item.baseNutrition.carbohydrate.toString() + "g"

        if (item.favoriteChecked) {
            holder.btnBookmark.setImageResource(R.drawable.ic_filled_star)
        } else {
            holder.btnBookmark.setImageResource(R.drawable.ic_empty_star)
        }

        holder.btnBookmark.setOnClickListener {
            holder.btnBookmark.setImageResource(R.drawable.ic_filled_star)
            itemClickListener(item)
        }

        holder.itemView.setOnClickListener {
            onItemClick(item.foodId)
        }
    }

    override fun getItemCount(): Int {
        return nutrients.size
    }
}