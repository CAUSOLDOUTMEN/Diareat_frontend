package com.example.snack4diet.api.createFood

data class CreateFood(
    val baseNutrition: BaseNutrition,
    val date: String,
    val name: String,
    val userId: Int
)