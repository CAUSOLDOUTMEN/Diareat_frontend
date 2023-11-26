package com.example.foodfood.api.getBookmark

data class Data(
    val baseNutrition: BaseNutrition,
    val count: Int,
    val favoriteFoodId: Long,
    val name: String
)