package com.example.snack4diet.analysis.ranking

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.snack4diet.R
import com.example.snack4diet.api.UserRank
import com.example.snack4diet.databinding.ItemRankingBinding

class RankingAdapter(private val followers: List<UserRank>, private val userId: Long): RecyclerView.Adapter<RankingAdapter.ViewHolder>() {

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

        holder.ranking.text = (position + 1).toString()
        if (item.image != null) {
            holder.profileImage.setImageURI(Uri.parse(item.image))
        }
        holder.nickname.text = item.name
        holder.score.text = item.totalScore.toString()

        if (item.userId == userId) {
            holder.itemLayout.setBackgroundResource(R.drawable.round_frame_light_orange_20)
        }
    }

    override fun getItemCount(): Int {
        return followers.size
    }
}