package com.example.foodfood.api.ocrRequest

import okhttp3.MultipartBody

data class OcrRequestMultiPartDto(
    val file: MultipartBody.Part
)
