package com.example.snack4diet.api.weeklyRank

data class Data(
    val calorieScore: Int,
    val carbohydrateScore: Int,
    val fatScore: Int,
    val image: String,
    val name: String,
    val proteinScore: Int,
    val totalScore: Int,
    val userId: Int
)