package com.example.snack4diet.api.addBookmark

data class AddBookmark(
    val baseNutrition: BaseNutrition,
    val foodId: Int,
    val name: String,
    val userId: Int
)