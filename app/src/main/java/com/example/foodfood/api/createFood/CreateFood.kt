package com.example.foodfood.api.createFood

data class CreateFood(
    val baseNutrition: BaseNutrition,
    val day: Int,
    val month: Int,
    val name: String,
    val userId: Long,
    val year: Int
)