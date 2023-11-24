package com.example.foodfood.api.weeklyRank

data class Data(
    val calorieScore: Double,
    val carbohydrateScore: Double,
    val fatScore: Double,
    val image: String,
    val name: String,
    val proteinScore: Double,
    val totalScore: Double,
    val userId: Long
)