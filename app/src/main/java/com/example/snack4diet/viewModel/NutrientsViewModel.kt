package com.example.snack4diet.viewModel

import androidx.lifecycle.ViewModel
import com.example.snack4diet.api.Macronutrients

class NutrientsViewModel: ViewModel() {
    val nutrients = mutableListOf<Macronutrients>()
}