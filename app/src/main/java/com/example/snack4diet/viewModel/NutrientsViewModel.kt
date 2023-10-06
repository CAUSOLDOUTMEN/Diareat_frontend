package com.example.snack4diet.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.snack4diet.api.Macronutrients
import com.example.snack4diet.api.UserInfo

class NutrientsViewModel: ViewModel() {
    private val nutrients = mutableListOf(
        Macronutrients(1,"음식1",325, 24,32,25, false),
        Macronutrients(2,"음식2",325, 24,32,25, false),
        Macronutrients(3,"음식3",325, 24,32,25, false),
        Macronutrients(4,"음식4",325, 24,32,25, false)
    )
    private val bookmarkList = mutableListOf<Macronutrients>()
    private val user = UserInfo("품절남", 180.0, 78.0, true, 24)

    val nutrientsLiveData: LiveData<MutableList<Macronutrients>>
        get() = MutableLiveData(nutrients)

    val bookmarkLiveData: LiveData<MutableList<Macronutrients>>
        get () = MutableLiveData(bookmarkList)

    fun resisterBookmark (nutrient: Macronutrients) {
        val new = nutrient.copy(foodId = bookmarkList.size + 1)
        bookmarkList.add(new)
        nutrients.find { it.foodId == nutrient.foodId }?.isBookmark = true
    }

    fun deleteBookmark(nutrient: Macronutrients) {
        bookmarkList.removeIf { it.foodId == nutrient.foodId }
        nutrients.find {it.foodId == nutrient.foodId}?.isBookmark = false
    }

    fun registerDiary(nutrient: Macronutrients) {
        val new = nutrient.copy(foodId = nutrients.size + 1, isBookmark = true)
        nutrients.add(new)
    }

    fun deleteDiary(id: Int) {
        nutrients.removeIf { it.foodId == id }
    }

    fun getUser(): UserInfo {
        return user
    }
}