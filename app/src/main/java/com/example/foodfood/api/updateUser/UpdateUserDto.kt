package com.example.foodfood.api.updateUser

data class UpdateUserDto(
    val age: Int,
    val height: Int,
    val name: String,
    val userId: Long,
    val weight: Int,
    val autoUpdateNutrition: Int
)