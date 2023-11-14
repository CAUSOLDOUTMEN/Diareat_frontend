package com.example.snack4diet.bookmark

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.snack4diet.api.Macronutrients
import com.example.snack4diet.api.getBookmark.Data
import com.example.snack4diet.databinding.ItemBookmarkBinding

class BookmarkAdapter(
    var nutrients: List<Data>,
//    private val itemClickListener: BookmarkFragment.OnItemClickListener,
//    private val onDeleteListener: (Data) -> Unit
    ): RecyclerView.Adapter<BookmarkAdapter.ViewHolder> () {

    inner class ViewHolder(binding: ItemBookmarkBinding): RecyclerView.ViewHolder(binding.root) {
        val itemLayout = binding.itemLayout
        val foodName = binding.foodName
        val kcal = binding.kcal
        val protein = binding.protein
        val province = binding.province
        val carbohydrate = binding.carbohydrate
        val btnEdit = binding.btnEdit
        val btnDelete = binding.btnDelete
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBookmarkBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = nutrients[position]

        holder.foodName.text = item.name
        holder.kcal.text = item.baseNutrition.kcal.toString() + "kcal"
        holder.protein.text = item.baseNutrition.protein.toString() + "g"
        holder.province.text = item.baseNutrition.fat.toString() + "g"
        holder.carbohydrate.text = item.baseNutrition.carbohydrate.toString() + "g"
        holder.itemView.visibility = View.VISIBLE

//        holder.btnDelete.setOnClickListener {
//            onDeleteListener(item)
//            notifyItemRemoved(position)
//        }

//        holder.itemLayout.setOnClickListener {
//            itemClickListener.onItemClick(item.favoriteFoodId)
//        }
    }

    override fun getItemCount(): Int {
        return nutrients.size
    }
}