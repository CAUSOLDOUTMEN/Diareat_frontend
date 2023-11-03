package com.example.snack4diet.analysis.ranking

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.snack4diet.R
import com.example.snack4diet.api.UserRank
import com.example.snack4diet.databinding.FragmentWeeklyRankingBinding

class WeeklyRankingFragment : Fragment() {
    private lateinit var binding: FragmentWeeklyRankingBinding
    private lateinit var followers: List<UserRank>
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWeeklyRankingBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)

        followers = listOf(
            UserRank(1, "중앙대최고아웃풋", null, 20.0, 19.9, 20.15, 19.95, 92.5),
            UserRank(2, "양념치킨안먹음", null, 20.0, 19.9, 20.15, 19.95, 90.0),
            UserRank(3, "근데진짜너무더워", null, 20.0, 19.9, 20.15, 19.95, 89.2),
            UserRank(4, "벤쿠버", null, 20.0, 19.9, 20.15, 19.95, 82.5)
        )

        setRecyclerView()
    }

    private fun setRecyclerView() {
        val adapter = RankingAdapter(followers, 4)
        binding.rankingRecyclerView.adapter = adapter
        binding.rankingRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }
}