package com.example.snack4diet.api.createFoodFromBookmark

data class CreateFoodFromBookmark(
    val favoriteFoodId: Long,
    val userId: Long
)