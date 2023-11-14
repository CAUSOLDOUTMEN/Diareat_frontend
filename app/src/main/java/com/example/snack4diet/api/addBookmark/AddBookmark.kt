package com.example.snack4diet.api.addBookmark

data class AddBookmark(
    val baseNutrition: BaseNutrition,
    val foodId: Long,
    val name: String,
    val userId: Long
)