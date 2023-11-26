package com.example.foodfood.api.addBookmark

data class AddBookmark(
    val baseNutrition: BaseNutrition,
    val foodId: Long,
    val name: String,
    val userId: Long
)