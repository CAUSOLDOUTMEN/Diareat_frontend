package com.example.foodfood.api.editFood

data class EditFood(
    val baseNutrition: BaseNutrition,
    val foodId: Long,
    val name: String,
    val userId: Long
)