package com.example.snack4diet.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.snack4diet.api.Macronutrients
import com.example.snack4diet.api.UserInfo
import com.example.snack4diet.api.UserNutrientInfo
import com.example.snack4diet.api.UserRank

class NutrientsViewModel: ViewModel() {
    private val nutrients = mutableListOf(
        Macronutrients(1,"음식1",325, 24,32,25, false),
        Macronutrients(2,"음식2",325, 24,32,25, false),
        Macronutrients(3,"음식3",325, 24,32,25, false),
        Macronutrients(4,"음식4",325, 24,32,25, false)
    )
    private val dailyNutrient = UserNutrientInfo(2250, 130, 75, 46)
    private val bookmarkList = mutableListOf<Macronutrients>()
    private val user = UserInfo("품절남", 180.0, 78.0, true, 24)
    private val following = mutableListOf(
        UserRank(1, "중앙대최고아웃풋", null, 20.0, 19.9, 20.15, 19.95, 92.5),
        UserRank(2, "양념치킨안먹음", null, 20.0, 19.9, 20.15, 19.95, 90.0),
        UserRank(3, "근데진짜너무더워", null, 20.0, 19.9, 20.15, 19.95, 89.2),
        UserRank(4, "벤쿠버", null, 20.0, 19.9, 20.15, 19.95, 82.5)
    )

    val nutrientsLiveData: LiveData<MutableList<Macronutrients>>
        get() = MutableLiveData(nutrients)

    val bookmarkLiveData: LiveData<MutableList<Macronutrients>>
        get () = MutableLiveData(bookmarkList)

    val dailyNutrientLiveData: LiveData<UserNutrientInfo>
        get () = MutableLiveData(dailyNutrient)

    val followingLiveData: LiveData<MutableList<UserRank>>
        get() = MutableLiveData(following)

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

    fun editUser(nickname: String, height: Double, weight: Double, age: Int) {
        user.nickname = nickname
        user.height = height
        user.weight = weight
        user.age = age
    }

    fun editDailyNutrient(dailyKcal: Int, dailyCarbohydrate: Int, dailyProtein: Int, dailyProvince: Int) {
        dailyNutrient.dailyKcal = dailyKcal
        dailyNutrient.dailyCarbohydrate = dailyCarbohydrate
        dailyNutrient.dailyProtein = dailyProtein
        dailyNutrient.dailyProvince = dailyProvince
    }
}