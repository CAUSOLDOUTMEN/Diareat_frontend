package com.example.snack4diet.api.foodOnDate

data class Data(
    val baseNutrition: BaseNutrition,
    val date: String,
    val favorite: Boolean,
    val foodId: Int,
    val name: String,
    val time: Time,
    val userId: Int
)