package com.example.foodfood.api.analysisGraph

data class Data(
    val calorieLastFourWeek: List<Int>,
    val calorieLastSevenDays: List<Int>,
    val carbohydrateLastFourWeek: List<Int>,
    val carbohydrateLastSevenDays: List<Int>,
    val fatLastFourWeek: List<Int>,
    val fatLastSevenDays: List<Int>,
    val proteinLastFourWeek: List<Int>,
    val proteinLastSevenDays: List<Int>,
    val totalScore: Double
)