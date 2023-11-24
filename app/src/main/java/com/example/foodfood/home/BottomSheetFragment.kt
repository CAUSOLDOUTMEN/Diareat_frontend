package com.example.foodfood.home

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import com.example.foodfood.MainActivity
import com.example.foodfood.R
import com.example.foodfood.api.Macronutrients
import com.example.foodfood.api.editFood.BaseNutrition
import com.example.foodfood.api.editFood.EditFood
import com.example.foodfood.api.foodOnDate.Data
import com.example.foodfood.databinding.DialogDeleteBookmarkBinding
import com.example.foodfood.databinding.FragmentBottomSheetBinding
import com.example.foodfood.viewModel.NutrientsViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class BottomSheetFragment(private val food: Data, val yy: Int, val mm: Int, val dd: Int) : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentBottomSheetBinding
    private lateinit var nutrientList: List<Macronutrients>
    private lateinit var viewModel: NutrientsViewModel
    private lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomSheetBinding.inflate(layoutInflater, container, false)

        mainActivity = requireActivity() as MainActivity
        viewModel = (requireActivity() as MainActivity).getViewModel()
        viewModel.nutrientsLiveData.observe(requireActivity()) { nutrients ->
            nutrientList = nutrients
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editFoodName.hint = food.name
        binding.editKcalAmount.hint = food.baseNutrition.kcal.toString()
        binding.editCarbohydrateAmount.hint = food.baseNutrition.carbohydrate.toString()
        binding.editProteinAmount.hint = food.baseNutrition.protein.toString()
        binding.editFatAmount.hint = food.baseNutrition.fat.toString()

        binding.btnDelete.setOnClickListener {
            deleteFood()
        }

        binding.btnFinishEdit.setOnClickListener {
            editFood()
        }
    }

    private fun deleteFood() {
        showDeleteFoodDialog()
    }

    private fun editFood() {
        try {
            var foodName = binding.editFoodName.text.toString()
            var kcal = binding.editKcalAmount.text.toString()
            var carbohydrate = binding.editCarbohydrateAmount.text.toString()
            var protein = binding.editProteinAmount.text.toString()
            var fat = binding.editFatAmount.text.toString()

            if (foodName.isNullOrEmpty()) foodName = food.name
            if (kcal.isNullOrEmpty()) kcal = food.baseNutrition.kcal.toString()
            if (carbohydrate.isNullOrEmpty()) carbohydrate = food.baseNutrition.carbohydrate.toString()
            if (protein.isNullOrEmpty()) protein = food.baseNutrition.protein.toString()
            if (fat.isNullOrEmpty()) fat = food.baseNutrition.fat.toString()

            val nutrition = BaseNutrition(carbohydrate.toInt(), fat.toInt(), kcal.toInt(), protein.toInt())
            val id = mainActivity.getUserId()
            val newFood = EditFood(nutrition, food.foodId, foodName, id)
            lifecycleScope.launch {
                mainActivity.editFood(newFood)
                (requireParentFragment() as DiaryFragment).getFoodList()
                dismiss()
            }
            notifyActionCompleted()
            Toast.makeText(requireContext(), "수정되었습니다.", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "제대로 입력해주세요.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDeleteFoodDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_delete_food)

        val dialogBinding = DialogDeleteBookmarkBinding.bind(dialog.findViewById(R.id.deleteFoodDialogLayout))

        dialogBinding.btnNo.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnYes.setOnClickListener {
            dialog.dismiss()
            mainActivity.deleteFood(food.foodId, yy, mm, dd)
            Toast.makeText(requireContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show()
            val fragment = requireParentFragment() as DiaryFragment
            fragment.getFoodList()
            dismiss()
        }

        dialog.show()

        val window = dialog.window
        window?.setBackgroundDrawableResource(R.drawable.round_frame_white_20)
    }

    private fun notifyActionCompleted() {
        // Fragment Result API를 통해 다이어리 프래그먼트에 알림
        parentFragmentManager.setFragmentResult("bottomSheetResult", bundleOf("actionCompleted" to true))
    }
}