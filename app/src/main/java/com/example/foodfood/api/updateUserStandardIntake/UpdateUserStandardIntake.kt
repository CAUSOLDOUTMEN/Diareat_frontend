package com.example.foodfood.api.updateUserStandardIntake

data class UpdateUserStandardIntake(
    val calorie: Int,
    val carbohydrate: Int,
    val fat: Int,
    val protein: Int,
    val userId: Long
)