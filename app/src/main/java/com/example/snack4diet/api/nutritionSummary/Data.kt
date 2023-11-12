package com.example.snack4diet.api.nutritionSummary

data class Data(
    val baseNutrition: BaseNutrition,
    val checkDate: String,
    val nutritionSumType: Int,
    val ratioCarbohydrate: Double,
    val ratioFat: Double,
    val ratioKcal: Double,
    val ratioProtein: Double,
    val totalCarbohydrate: Int,
    val totalFat: Int,
    val totalKcal: Int,
    val totalProtein: Int,
    val userId: Int
)