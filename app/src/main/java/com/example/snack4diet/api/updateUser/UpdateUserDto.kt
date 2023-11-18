package com.example.snack4diet.api.updateUser

data class UpdateUserDto(
    val age: Int,
    val height: Double,
    val name: String,
    val userId: Long,
    val weight: Double
)