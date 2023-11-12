package com.example.snack4diet.api.editFood

data class EditFood(
    val baseNutrition: BaseNutrition,
    val foodId: Int,
    val name: String,
    val userId: Int
)