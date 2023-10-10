package com.example.snack4diet.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.snack4diet.MainActivity
import com.example.snack4diet.R
import com.example.snack4diet.api.UserNutrientInfo
import com.example.snack4diet.databinding.FragmentNutrientProfileEditBinding
import com.example.snack4diet.viewModel.NutrientsViewModel

class NutrientProfileEditFragment : Fragment() {
    private lateinit var binding: FragmentNutrientProfileEditBinding
    private lateinit var viewModel: NutrientsViewModel
    private lateinit var userDailyNutrient: UserNutrientInfo

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNutrientProfileEditBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mainActivity = requireActivity() as MainActivity
        viewModel = mainActivity.getViewModel()
        viewModel.dailyNutrientLiveData.observe(requireActivity()) { dailyNutrient ->
            userDailyNutrient = dailyNutrient
        }

        binding.kcal.hint = userDailyNutrient.dailyKcal.toString()
        binding.carbohydrate.hint = userDailyNutrient.dailyCarbohydrate.toString()
        binding.protein.hint = userDailyNutrient.dailyProtein.toString()
        binding.province.hint = userDailyNutrient.dailyProvince.toString()

        binding.btnFinishEdit.setOnClickListener {
            var newKcal = binding.kcal.text.toString()
            var newCarbohydrate = binding.carbohydrate.text.toString()
            var newProtein = binding.protein.text.toString()
            var newProvince = binding.province.text.toString()

            if (newKcal.isEmpty()) {
                newKcal = binding.kcal.hint.toString()
            }
            if (newCarbohydrate.isEmpty()) {
                newCarbohydrate = binding.carbohydrate.hint.toString()
            }
            if (newProtein.isEmpty()) {
                newProtein = binding.protein.hint.toString()
            }
            if (newProvince.isEmpty()) {
                newProvince = binding.province.hint.toString()
            }

            if (
                newKcal.toInt() in 1000..10000 &&
                newCarbohydrate.toInt() in 100..500 &&
                newProtein.toInt() in 25..500 &&
                newProvince.toInt() in 25..500
            ) {
                val mainActivity = requireActivity() as MainActivity
                viewModel.editDailyNutrient(newKcal.toInt(), newCarbohydrate.toInt(), newProtein.toInt(), newProvince.toInt())
                mainActivity.popFragment()
                Toast.makeText(requireContext(), "수정이 완료되었습니다.", Toast.LENGTH_SHORT).show()
            } else {

            }
        }
    }
}