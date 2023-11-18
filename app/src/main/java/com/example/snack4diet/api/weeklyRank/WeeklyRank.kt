package com.example.snack4diet.api.weeklyRank

data class WeeklyRank(
    val `data`: List<Data>,
    val header: Header,
    val msg: String
)