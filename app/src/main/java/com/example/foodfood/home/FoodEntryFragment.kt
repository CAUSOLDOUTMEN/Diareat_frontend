package com.example.foodfood.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.foodfood.MainActivity
import com.example.foodfood.api.createFood.BaseNutrition
import com.example.foodfood.api.createFood.CreateFood
import com.example.foodfood.databinding.FragmentFoodEntryBinding
import com.example.foodfood.viewModel.NutrientsViewModel
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

class FoodEntryFragment : Fragment() {
    private lateinit var binding: FragmentFoodEntryBinding
    private lateinit var viewModel: NutrientsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFoodEntryBinding.inflate(layoutInflater, container, false)
        viewModel = (requireActivity() as MainActivity).getViewModel()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
        binding.foodName.hint = currentTime.hour.toString() + "시 " + currentTime.minute.toString() + "분의 음식"

        binding.btnFoodSave.setOnClickListener {
            saveFood()
        }
    }

    private fun saveFood() {
        var foodName = ""

        if (binding.foodName.text.isEmpty()) {
            foodName = binding.foodName.hint.toString()
        }
        else foodName = binding.foodName.text.toString()

        val kcal = binding.editKcalAmount
        val carbohydrate = binding.editCarbohydrateAmount
        val protein = binding.editProteinAmount
        val province = binding.editProvinceAmount

        if (kcal.text.isNullOrEmpty() || carbohydrate.text.isNullOrEmpty() ||
            protein.text.isNullOrEmpty() || province.text.isNullOrEmpty()) {

            Toast.makeText(requireContext(), "모든 항목을 채워주세요.", Toast.LENGTH_SHORT).show()
        } else {
            val mainActivity = requireActivity() as MainActivity
            val id = mainActivity.getUserId()
            val baseNutrition = BaseNutrition(carbohydrate.text.toString().toInt(), province.text.toString().toInt(), kcal.text.toString().toInt(), protein.text.toString().toInt())
            val currentDate = LocalDate.now()
            val food = CreateFood(baseNutrition, currentDate.dayOfMonth, currentDate.monthValue, foodName, id, currentDate.year)

            mainActivity.createFood(food)
        }
    }
}