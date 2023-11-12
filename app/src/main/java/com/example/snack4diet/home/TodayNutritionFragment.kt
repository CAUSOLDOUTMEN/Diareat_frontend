package com.example.snack4diet.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.snack4diet.R
import com.example.snack4diet.api.nutritionSummary.NutritionSummary
import com.example.snack4diet.databinding.FragmentTodayNutritionBinding

class TodayNutritionFragment : Fragment() {
    private lateinit var binding: FragmentTodayNutritionBinding
    private lateinit var dailyNutrition: NutritionSummary
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTodayNutritionBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragment = requireParentFragment() as HomeFragment
        dailyNutrition = fragment.getDailyNutrition()
        Log.e("ㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏ", dailyNutrition.toString())

        binding.dailyKcal.text = dailyNutrition.data.totalKcal.toString() + "kcal / " + dailyNutrition.data.baseNutrition.kcal.toString() + "kcal"
        binding.dailyCarbohydrate.text = dailyNutrition.data.totalCarbohydrate.toString() + "g / " + dailyNutrition.data.baseNutrition.carbohydrate.toString() + "g"
        binding.dailyProtein.text = dailyNutrition.data.totalProtein.toString() + "g / " + dailyNutrition.data.baseNutrition.protein.toString() + "g"
        binding.dailyFat.text = dailyNutrition.data.totalFat.toString() + "g / " + dailyNutrition.data.baseNutrition.fat.toString() + "g"

        binding.percentKcal.text = dailyNutrition.data.ratioKcal.toString() + "%"
        binding.percentCarbohydrate.text = dailyNutrition.data.ratioCarbohydrate.toString() + "%"
        binding.percentProtein.text = dailyNutrition.data.ratioProtein.toString() + "%"
        binding.percentFat.text = dailyNutrition.data.ratioFat.toString() + "%"

        if (dailyNutrition.data.ratioKcal > 100.0) {
            binding.percentKcal.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
        }
        if (dailyNutrition.data.ratioCarbohydrate > 100.0) {
            binding.percentCarbohydrate.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
        }
        if (dailyNutrition.data.ratioProtein > 100.0) {
            binding.percentProtein.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
        }
        if (dailyNutrition.data.ratioFat > 100.0) {
            binding.percentFat.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
        }
    }
}