package com.example.snack4diet.api.searchUserResponse

data class Data(
    var follow: Boolean,
    val image: String,
    val name: String,
    val userId: Long
)