package com.example.snack4diet.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.snack4diet.api.Macronutrients

class NutrientsViewModel: ViewModel() {
    private val nutrients = mutableListOf(
        Macronutrients("음식1",325, 24,32,25, false),
        Macronutrients("음식2",325, 24,32,25, false),
        Macronutrients("음식3",325, 24,32,25, false),
        Macronutrients("음식4",325, 24,32,25, false)
    )

    val nutrientsLiveData: LiveData<MutableList<Macronutrients>>
        get() = MutableLiveData(nutrients)

    fun resisterBookmark (nutrient: Macronutrients) {
        if (!nutrient.isBookmark) {
            nutrient.isBookmark = true
        }
    }

    fun deleteBookmark (nutrient: Macronutrients) {
        nutrient.isBookmark = false
    }
}