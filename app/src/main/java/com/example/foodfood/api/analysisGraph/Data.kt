package com.example.foodfood.api.analysisGraph

import java.time.LocalDate

data class Data(
    val calorieLastFourWeek: List<Int>,
    val calorieLastSevenDays: List<Map<String, Int>>,
    val carbohydrateLastFourWeek: List<Int>,
    val carbohydrateLastSevenDays: List<Map<String, Int>>,
    val fatLastFourWeek: List<Int>,
    val fatLastSevenDays: List<Map<String, Int>>,
    val proteinLastFourWeek: List<Int>,
    val proteinLastSevenDays: List<Map<String, Int>>,
    val totalScore: Double
)