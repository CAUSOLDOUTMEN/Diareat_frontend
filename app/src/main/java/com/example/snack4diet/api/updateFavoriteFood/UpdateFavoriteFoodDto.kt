package com.example.snack4diet.api.updateFavoriteFood

data class UpdateFavoriteFoodDto(
    val baseNutrition: BaseNutrition,
    val favoriteFoodId: Long,
    val name: String,
    val userId: Long
)