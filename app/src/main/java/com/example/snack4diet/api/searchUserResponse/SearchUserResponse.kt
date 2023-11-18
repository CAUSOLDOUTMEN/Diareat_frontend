package com.example.snack4diet.api.searchUserResponse

data class SearchUserResponse(
    val `data`: List<Data>,
    val header: Header,
    val msg: String
)