package com.example.snack4diet.api.foodOnDate

data class Data(
    val baseNutrition: BaseNutrition,
    val favoriteChecked: Boolean,
    val foodId: Long,
    val name: String,
    val userId: Long
)