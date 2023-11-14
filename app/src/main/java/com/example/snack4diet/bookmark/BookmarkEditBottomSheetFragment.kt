package com.example.snack4diet.bookmark

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import com.example.snack4diet.MainActivity
import com.example.snack4diet.api.updateFavoriteFood.BaseNutrition
import com.example.snack4diet.api.updateFavoriteFood.UpdateFavoriteFoodDto
import com.example.snack4diet.databinding.FragmentBookmarkEditBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

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

            if (foodName.isNullOrEmpty()) foodName = updateFavoriteFoodDto.name
            if (kcal.isNullOrEmpty()) kcal = updateFavoriteFoodDto.baseNutrition.kcal.toString()
            if (carbohydrate.isNullOrEmpty()) carbohydrate = updateFavoriteFoodDto.baseNutrition.carbohydrate.toString()
            if (protein.isNullOrEmpty()) protein = updateFavoriteFoodDto.baseNutrition.protein.toString()
            if (fat.isNullOrEmpty()) fat = updateFavoriteFoodDto.baseNutrition.fat.toString()

            val nutrition = BaseNutrition(carbohydrate.toInt(), fat.toInt(), kcal.toInt(), protein.toInt())
            val id = mainActivity.getUserId()
            val newBookmark = UpdateFavoriteFoodDto(nutrition, updateFavoriteFoodDto.favoriteFoodId, foodName, id)
            mainActivity.editBookmark(newBookmark)
            val fragment = requireParentFragment() as BookmarkFragment
            fragment.getBookmarkList()
            dismiss()
            notifyActionCompleted()
            Toast.makeText(requireContext(), "수정되었습니다.", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "제대로 입력해주세요.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun notifyActionCompleted() {
        // Fragment Result API를 통해 다이어리 프래그먼트에 알림
        parentFragmentManager.setFragmentResult("bookmarkEditBottomSheetResult", bundleOf("actionCompleted" to true))
    }
}