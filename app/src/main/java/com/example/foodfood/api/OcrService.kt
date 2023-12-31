package com.example.foodfood.api

import com.example.foodfood.api.ocr.ResponseOcr
import com.example.foodfood.api.ocrRequest.OcrRequestDto
import com.example.foodfood.api.ocrRequest.OcrRequestMultiPartDto
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface OcrService {
    @POST("/parse_nutrients")
    suspend fun sendImageKey(@Body ocrRequestDto: OcrRequestDto): ResponseOcr

    @POST("/parse_nutrients")
    @Multipart
    suspend fun uploadImage(@Part("file") file: OcrRequestMultiPartDto): ResponseOcr

    @Multipart
    @POST("/parse_nutrients")
    suspend fun sendFile(
        @Part file: MultipartBody.Part
    ): ResponseOcr

//    @POST("/parse_nutrients")
//    suspend fun sendFile(
//        @Body file: String
//    ): ResponseOcr
}