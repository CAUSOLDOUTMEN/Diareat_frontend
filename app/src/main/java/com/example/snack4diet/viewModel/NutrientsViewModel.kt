package com.example.snack4diet.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.snack4diet.api.Macronutrients

class NutrientsViewModel: ViewModel() {
    private val nutrients = mutableListOf(
        Macronutrients(1,"음식1",325, 24,32,25, false),
        Macronutrients(2,"음식2",325, 24,32,25, false),
        Macronutrients(3,"음식3",325, 24,32,25, false),
        Macronutrients(4,"음식4",325, 24,32,25, false)
    )

    private val bookmarkList = mutableListOf<Macronutrients>()

    val nutrientsLiveData: LiveData<MutableList<Macronutrients>>
        get() = MutableLiveData(nutrients)

    val bookmarkLiveData: LiveData<MutableList<Macronutrients>>
        get () = MutableLiveData(bookmarkList)

    fun resisterBookmark (nutrient: Macronutrients) {
        if (bookmarkList.find {it.foodId == nutrient.foodId} == null) {
            bookmarkList.add(nutrient)
            nutrients.find {it.foodId == nutrient.foodId}?.isBookmark = true
        }
    }

    fun deleteBookmark(nutrient: Macronutrients) {
        bookmarkList.removeIf { it.foodId == nutrient.foodId }
        nutrients.find {it.foodId == nutrient.foodId}?.isBookmark = false
    }
}