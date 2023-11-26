package com.example.foodfood.analysis.ranking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.foodfood.api.weeklyRank.Data
import com.example.foodfood.databinding.FragmentWeeklyRankingBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class RankingBottomSheetFragment(private val data: Data) : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentWeeklyRankingBottomSheetBinding
    private var position = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWeeklyRankingBottomSheetBinding.inflate(layoutInflater, container, false)

        position = arguments?.getInt("position", -1)!!

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ranking.text = (position + 1).toString() + "위"
        binding.userName.text = data.name
        binding.kcal.text = String.format("%.1f", data.calorieScore)
        binding.carbohydrate.text = String.format("%.1f", data.carbohydrateScore)
        binding.protein.text = String.format("%.1f", data.proteinScore)
        binding.fat.text = String.format("%.1f", data.fatScore)
        binding.totalScore.text = String.format("%.1f", data.totalScore)
    }
}