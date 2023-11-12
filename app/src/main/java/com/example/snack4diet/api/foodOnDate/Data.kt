package com.example.snack4diet.api.foodOnDate

data class Data(
    val baseNutrition: BaseNutrition,
    val favorite: Boolean,
    val foodId: Int,
    val name: String,
    val userId: Int
)