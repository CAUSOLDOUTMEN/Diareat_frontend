package com.example.snack4diet.api.nutritionSummary

data class Data(
    val checkDate: String,
    val nutritionSumType: Int,
    val ratioCarbohydrate: Int,
    val ratioFat: Int,
    val ratioKcal: Int,
    val ratioProtein: Int,
    val totalCarbohydrate: Int,
    val totalFat: Int,
    val totalKcal: Int,
    val totalProtein: Int,
    val userId: Int
)