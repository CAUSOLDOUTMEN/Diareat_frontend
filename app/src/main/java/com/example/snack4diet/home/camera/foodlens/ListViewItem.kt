package com.example.snack4diet.home.camera.foodlens

import android.graphics.drawable.Drawable

data class ListViewItem(
    var iconDrawable: Drawable? = null,
    var titleStr: String? = null,
    var foodPosition: String? = null,
    var foodNutrition: String? = null
)