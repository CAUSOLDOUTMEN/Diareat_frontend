package com.example.foodfood.analysis.ranking

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodfood.R
import com.example.foodfood.api.weeklyRank.Data
import com.example.foodfood.databinding.ItemRankingBinding


class RankingAdapter(private val context: Context, private var followers: List<Data>, private val userId: Long): RecyclerView.Adapter<RankingAdapter.ViewHolder>() {
    private var onItemClickCallback: ((Int) -> Unit)? = null
    fun showBottomSheet(callback: (Int) -> (Unit)) {
        onItemClickCallback = callback
    }

    private fun onItemClick(position: Int) {
        onItemClickCallback?.invoke(position)
    }

    inner class ViewHolder(binding: ItemRankingBinding): RecyclerView.ViewHolder(binding.root) {
        val itemLayout = binding.itemLayout
        val ranking = binding.ranking
        val profileImage = binding.profileImage
        val nickname = binding.nickname
        val score = binding.score
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRankingBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = followers[position]
        val rank = position + 1

        holder.ranking.text = rank.toString()
        if (item.image != null) {
            Glide.with(context)
                .load(item.image)
                .placeholder(R.drawable.ic_profile_image)
                .circleCrop()
                .into(holder.profileImage)
        } else {

        }
        holder.nickname.text = item.name
        holder.score.text = String.format("%.1f", item.totalScore)

        if (item.userId == userId) {
            holder.itemLayout.setBackgroundResource(R.drawable.round_frame_light_orange_20)
        }

        holder.itemLayout.setOnClickListener {
            onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return followers.size
    }
}