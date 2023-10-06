package com.example.snack4diet.api

data class UserInfo(
    var nickname: String,
    var height: Double,
    var weight: Double,
    var sex: Boolean,
    var age: Int
)

data class Macronutrients(
    val foodId: Int,
    val foodName: String,
    val kcal: Int,
    val carbohydrate: Int,
    val protein: Int,
    val province: Int,
    var isBookmark: Boolean
)

data class NutritionItem(
    val nutritionItem: String,
    val consumedAmount: Int,
    val targetAmount: Int
)

data class DayWithWeekday(
    val day: Int,
    val weekday: String,
    var isClicked: Boolean = false
)