package com.example.snack4diet.api

data class UserInfo(
    val sex: Boolean,
    val height: Float,
    val weight: Float,
    val age: Int
)

data class Macronutrients(
    val kcal: Int,
    val carbohydrate: Int,
    val protein: Int,
    val province: Int
)

data class NutritionItem(
    val nutritionItem: String,
    val consumedAmount: Int,
    val targetAmount: Int
)