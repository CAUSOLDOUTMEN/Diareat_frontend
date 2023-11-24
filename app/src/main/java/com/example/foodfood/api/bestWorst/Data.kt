package com.example.foodfood.api.bestWorst

data class Data(
    val best: List<Best>,
    val calorieScore: Double,
    val carbohydrateScore: Double,
    val fatScore: Double,
    val proteinScore: Double,
    val totalScore: Double,
    val worst: List<Worst>
)