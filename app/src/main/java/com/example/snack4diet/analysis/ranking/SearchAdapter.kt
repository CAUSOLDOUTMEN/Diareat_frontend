package com.example.snack4diet.analysis.ranking

import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.snack4diet.R
import com.example.snack4diet.api.UserRank
import com.example.snack4diet.api.UserSearch
import com.example.snack4diet.databinding.ItemRankingBinding
import com.example.snack4diet.databinding.ItemSearchBinding

class SearchAdapter(private var searchResult: List<UserSearch>, private val searchedTitle: String): RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

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
            holder.profileImage.setImageURI(Uri.parse(item.image))
        }
        holder.nickname.text = item.name
        holder.btnFollow.isChecked = item.follow
        holder.btnFollow.setOnClickListener {
            item.follow = holder.btnFollow.isChecked
        }
        holder.nickname.text = highlightKeyword(item.name, searchedTitle)
    }

    override fun getItemCount(): Int {
        return searchResult.size
    }

    fun updateData(newData: List<UserSearch>) {
        searchResult = newData
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