package com.example.snack4diet.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.snack4diet.MainActivity
import com.example.snack4diet.R
import com.example.snack4diet.api.Macronutrients
import com.example.snack4diet.databinding.FragmentFoodEntryBinding
import com.example.snack4diet.viewModel.NutrientsViewModel
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

        if (binding.foodName.text.isEmpty()) foodName = binding.foodName.hint.toString()
        else foodName = binding.foodName.text.toString()

        val kcal = binding.editKcalAmount
        val carbohydrate = binding.EditCarbohydrateAmount
        val protein = binding.EditProteinAmount
        val province = binding.EditProvinceAmount

        if (kcal.text.isNullOrEmpty() || carbohydrate.text.isNullOrEmpty() ||
            protein.text.isNullOrEmpty() || province.text.isNullOrEmpty()) {

            Toast.makeText(requireContext(), "모든 항목을 채워주세요.", Toast.LENGTH_SHORT).show()
        } else {
            val id = viewModel.getNutrientsSize() + 1
            val nutrient = Macronutrients(id, foodName, kcal.text.toString().toInt(), carbohydrate.text.toString().toInt(), protein.text.toString().toInt(), province.text.toString().toInt(), false)

            viewModel.addDiary(nutrient)
            Toast.makeText(requireContext(), "저장이 완료되었습니다.", Toast.LENGTH_SHORT).show()

            (requireActivity() as MainActivity).setHomeFragment()
        }
    }
}