package com.example.snack4diet.api.bestWorst

data class Data(
    val best: List<Best>,
    val calorieScore: Int,
    val carbohydrateScore: Int,
    val fatScore: Int,
    val proteinScore: Int,
    val totalScore: Int,
    val worst: List<Worst>
)