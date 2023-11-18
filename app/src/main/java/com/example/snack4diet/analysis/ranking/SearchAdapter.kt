package com.example.snack4diet.analysis.ranking

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.snack4diet.R
import com.example.snack4diet.api.UserRank
import com.example.snack4diet.api.UserSearch
import com.example.snack4diet.api.searchUserResponse.Data
import com.example.snack4diet.databinding.ItemRankingBinding
import com.example.snack4diet.databinding.ItemSearchBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchAdapter(
    private val context: Context,
    private var searchResult: List<Data>,
    private val searchedTitle: String,
    private val followListener: WeeklyRankingFragment.FollowListener
): RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    inner class ViewHolder(binding: ItemSearchBinding): RecyclerView.ViewHolder(binding.root) {
        val ranking = binding.ranking
        val profileImage = binding.profileImage
        val nickname = binding.nickname
        val btnFollow = binding.btnFollow
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = searchResult[position]

        holder.ranking.text = (position + 1).toString()
        if (item.image != null) {
            Glide.with(context)
                .load(item.image)
                .placeholder(R.drawable.ic_profile_image)
                .circleCrop()
                .into(holder.profileImage)
        }
        holder.nickname.text = item.name
        holder.btnFollow.isChecked = item.follow
//        holder.btnFollow.setOnClickListener {
//            item.follow = holder.btnFollow.isChecked
//            if (item.follow) {
//                followListener.followUser(item.userId)
//            } else {
//                followListener.unfollowUser(item.userId)
//            }
//        }

        holder.btnFollow.setOnClickListener {
            val isChecked = holder.btnFollow.isChecked
            holder.btnFollow.isChecked = !isChecked // UI를 우선 업데이트

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    if (isChecked) {
                        followListener.unfollowUser(item.userId)
                    } else {
                        followListener.followUser(item.userId)
                    }
                    withContext(Dispatchers.Main) {
                        // 팔로우 상태가 성공적으로 변경된 경우 UI 업데이트
                        item.follow = isChecked
                        holder.btnFollow.isChecked = isChecked
                    }
                } catch (e: Exception) {
                    Log.e("YourAdapter", "Error during follow/unfollow API call", e)
                    withContext(Dispatchers.Main) {
                        // 실패 시 다시 원래 상태로 돌려놓기
                        holder.btnFollow.isChecked = !isChecked
                    }
                }
            }
        }


        holder.nickname.text = highlightKeyword(item.name, searchedTitle)
    }

    override fun getItemCount(): Int {
        return searchResult.size
    }

    private fun highlightKeyword(text: String, keyword: String): SpannableStringBuilder {
        val spannableBuilder = SpannableStringBuilder(text)

        val startIndex = text.indexOf(keyword, ignoreCase = true)
        if (startIndex != -1) {
            val endIndex = startIndex + keyword.length

            // 검색한 단어의 색상을 변경할 스팬을 생성
            val colorSpan = ForegroundColorSpan(Color.BLACK)

            // 검색한 단어에 볼드 처리할 스팬을 생성
            val boldSpan = StyleSpan(Typeface.BOLD)

            // 스팬을 적용하여 검색한 단어만 색상을 변경
            spannableBuilder.setSpan(
                colorSpan,
                startIndex,
                endIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannableBuilder.setSpan(
                boldSpan,
                startIndex,
                endIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        return spannableBuilder
    }
}