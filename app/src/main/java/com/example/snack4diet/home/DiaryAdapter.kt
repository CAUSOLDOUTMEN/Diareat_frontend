package com.example.snack4diet.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import androidx.recyclerview.widget.RecyclerView
import com.example.snack4diet.R
import com.example.snack4diet.api.Macronutrients
import com.example.snack4diet.databinding.ItemDiaryBinding

class DiaryAdapter( var nutrients: List<Macronutrients>, private val itemClickListener: (Macronutrients) -> Unit): RecyclerView.Adapter<DiaryAdapter.ViewHolder> () {
    private var onItemClickCallback: ((Int) -> Unit)? = null

    fun setOnItemClickListener(callback: (Int) -> Unit) {
        onItemClickCallback = callback
    }

    private fun onItemClick(position: Int) {
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

        holder.foodName.text = item.foodName
        holder.kcal.text = item.kcal.toString() + "kcal"
        holder.protein.text = item.protein.toString() + "g"
        holder.province.text = item.province.toString() + "g"
        holder.carbohydrate.text = item.carbohydrate.toString() + "g"

        if (item.isBookmark) {
            holder.btnBookmark.setImageResource(R.drawable.ic_filled_star)
        } else {
            holder.btnBookmark.setImageResource(R.drawable.ic_empty_star)
            holder.btnBookmark.setOnClickListener {
                itemClickListener(item)
            } // 즐겨찾기에 등록되어 있지 않는 아이템에 대해서만 클릭 리스너 추가
        }

        holder.itemView.setOnClickListener {
            onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return nutrients.size
    }
}