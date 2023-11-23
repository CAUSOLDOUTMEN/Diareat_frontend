package com.example.snack4diet.api.ocrRequest

import okhttp3.MultipartBody

data class OcrRequestMultiPartDto(
    val file: MultipartBody.Part
)
