package com.example.snack4diet.home

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.snack4diet.MainActivity
import com.example.snack4diet.R
import com.example.snack4diet.api.Macronutrients
import com.example.snack4diet.api.NutritionItem
import com.example.snack4diet.databinding.FragmentBottomSheetBinding
import com.example.snack4diet.viewModel.NutrientsViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentBottomSheetBinding
    private lateinit var nutrientList: List<Macronutrients>
    private lateinit var viewModel: NutrientsViewModel
    private var position = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomSheetBinding.inflate(layoutInflater, container, false)

        position = arguments?.getInt("position", -1)!!

        viewModel = (requireActivity() as MainActivity).getViewModel()
        viewModel.nutrientsLiveData.observe(requireActivity()) { nutrients ->
            nutrientList = nutrients
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }
}