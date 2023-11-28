package com.example.foodfood.bookmark

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.foodfood.MainActivity
import com.example.foodfood.api.updateFavoriteFood.BaseNutrition
import com.example.foodfood.api.updateFavoriteFood.UpdateFavoriteFoodDto
import com.example.foodfood.databinding.FragmentBookmarkEditBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class BookmarkEditBottomSheetFragment(private val updateFavoriteFoodDto: UpdateFavoriteFoodDto) : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentBookmarkEditBottomSheetBinding
    private lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBookmarkEditBottomSheetBinding.inflate(layoutInflater, container, false)

        mainActivity = requireActivity() as MainActivity

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editFoodName.hint = updateFavoriteFoodDto.name
        binding.editKcalAmount.hint = updateFavoriteFoodDto.baseNutrition.kcal.toString()
        binding.editCarbohydrateAmount.hint = updateFavoriteFoodDto.baseNutrition.carbohydrate.toString()
        binding.editProteinAmount.hint = updateFavoriteFoodDto.baseNutrition.protein.toString()
        binding.editFatAmount.hint = updateFavoriteFoodDto.baseNutrition.fat.toString()

        binding.btnFinishEdit.setOnClickListener {
            editBookmark()
        }
    }

    private fun editBookmark() {
        try {
            var foodName = binding.editFoodName.text.toString()
            var kcal = binding.editKcalAmount.text.toString()
            var carbohydrate = binding.editCarbohydrateAmount.text.toString()
            var protein = binding.editProteinAmount.text.toString()
            var fat = binding.editFatAmount.text.toString()

            if (foodName.isNullOrEmpty()) foodName = binding.editFoodName.hint.toString()
            if (kcal.isNullOrEmpty()) kcal = binding.editKcalAmount.hint.toString()
            if (carbohydrate.isNullOrEmpty()) carbohydrate = binding.editCarbohydrateAmount.hint.toString()
            if (protein.isNullOrEmpty()) protein = binding.editProteinAmount.hint.toString()
            if (fat.isNullOrEmpty()) fat = binding.editFatAmount.hint.toString()

            val nutrition = BaseNutrition(carbohydrate.toInt(), fat.toInt(), kcal.toInt(), protein.toInt())
            val id = mainActivity.getUserId()
            val newBookmark = UpdateFavoriteFoodDto(nutrition, updateFavoriteFoodDto.favoriteFoodId, foodName, id)
            lifecycleScope.launch {
                mainActivity.editBookmark(newBookmark)
                (parentFragment as BookmarkFragment).getBookmarkList()
                dismiss()
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "제대로 입력해주세요.", Toast.LENGTH_SHORT).show()
        }
    }
}