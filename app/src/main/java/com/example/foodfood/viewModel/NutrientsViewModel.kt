package com.example.foodfood.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.foodfood.api.Macronutrients
import com.example.foodfood.api.UserInfo
import com.example.foodfood.api.UserRank
import com.example.foodfood.api.UserSearch

class NutrientsViewModel: ViewModel() {

    private val nutrients = mutableListOf(
        Macronutrients(1,"음식1",325, 24,32,25, false),
        Macronutrients(2,"음식2",325, 24,32,25, false),
        Macronutrients(3,"음식3",325, 24,32,25, false),
        Macronutrients(4,"음식4",325, 24,32,25, false)
    )
    private val bookmarkList = mutableListOf<com.example.foodfood.api.getBookmark.Data>()
    private val user = UserInfo("품절남", 180.0, 78.0, true, 24)
    private val following = mutableListOf(
        UserRank(1, "중앙대최고아웃풋", null, 20.0, 19.9, 20.15, 19.95, 92.5),
        UserRank(2, "양념치킨안먹음", null, 20.0, 19.9, 20.15, 19.95, 90.0),
        UserRank(3, "근데진짜너무더워", null, 20.0, 19.9, 20.15, 19.95, 89.2),
        UserRank(4, "벤쿠버", null, 20.0, 19.9, 20.15, 19.95, 82.5)
    )
    private val searchUser = mutableListOf(
        UserSearch(true, null, "중앙대최고아웃풋", 1),
        UserSearch(false, null, "중앙대의자랑", 5),
        UserSearch(false, null, "중앙대소프트", 6),
        UserSearch(false, null, "중앙대학교", 7),
    )

    val nutrientsLiveData: LiveData<MutableList<Macronutrients>>
        get() = MutableLiveData(nutrients)

    val bookmarkLiveData: LiveData<MutableList<com.example.foodfood.api.getBookmark.Data>>
        get () = MutableLiveData(bookmarkList)

    val followingLiveData: LiveData<MutableList<UserRank>>
        get() = MutableLiveData(following)

    val searchUserLiveData: LiveData<MutableList<UserSearch>>
        get() = MutableLiveData(searchUser)

//    fun resisterBookmark (nutrient: Data) {
//        val new = nutrient.copy(foodId = bookmarkList.size + 1)
////        bookmarkList.add(new)
//        nutrients.find { it.foodId == nutrient.foodId }?.isBookmark = true
//    }


    fun registerDiary(nutrient: Macronutrients) {
        val new = nutrient.copy(foodId = nutrients.size + 1, isBookmark = true)
        nutrients.add(new)
    }

    fun deleteDiary(id: Int) {
        nutrients.removeIf { it.foodId == id }
    }

    fun addDiary(nutrient: Macronutrients) {
        nutrients.add(nutrient)
    }

    fun getNutrientsSize(): Int {
        return nutrients.size
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
}