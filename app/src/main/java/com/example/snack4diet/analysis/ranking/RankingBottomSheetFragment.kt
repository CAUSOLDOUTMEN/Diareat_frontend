package com.example.snack4diet.analysis.ranking

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.snack4diet.MainActivity
import com.example.snack4diet.R
import com.example.snack4diet.api.UserRank
import com.example.snack4diet.databinding.FragmentWeeklyRankingBottomSheetBinding
import com.example.snack4diet.viewModel.NutrientsViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class RankingBottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentWeeklyRankingBottomSheetBinding
    private lateinit var viewModel: NutrientsViewModel
    private lateinit var followings: List<UserRank>
    private var position = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWeeklyRankingBottomSheetBinding.inflate(layoutInflater, container, false)

        position = arguments?.getInt("position", -1)!!
        viewModel = (requireActivity() as MainActivity).getViewModel()
        viewModel.followingLiveData.observe(requireActivity()) {following ->
            followings = following
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ranking.text = (position + 1).toString() + "위"
        binding.userName.text = followings[position].name
        binding.kcal.text = followings[position].kcalScore.toString()
        binding.carbohydrate.text = followings[position].carbohydrateScore.toString()
        binding.protein.text = followings[position].proteinScore.toString()
        binding.province.text = followings[position].provinceScore.toString()
        binding.totalScore.text = followings[position].totalScore.toString()
    }
}