package com.example.snack4diet.api.createFood

import java.time.LocalDate

data class CreateFood(
    val baseNutrition: BaseNutrition,
    val day: Int,
    val month: Int,
    val name: String,
    val userId: Long,
    val year: Int
)