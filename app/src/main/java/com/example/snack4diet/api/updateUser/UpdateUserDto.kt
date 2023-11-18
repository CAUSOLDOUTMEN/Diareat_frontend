package com.example.snack4diet.api.updateUser

data class UpdateUserDto(
    val age: Int,
    val height: Int,
    val name: String,
    val userId: Int,
    val weight: Int
)