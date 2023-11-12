package com.example.snack4diet.api

import com.example.snack4diet.api.createFood.CreateFood
import com.example.snack4diet.api.foodOnDate.FoodOnDate
import com.example.snack4diet.api.nutritionSummary.NutritionSummary
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("/api/auth/login")
    suspend fun kakaoLogin(@Header("accessToken") accessToken: String): Login // 카카오 인가 코드를 전달했을 때 유저 id, jwt를 돌려받음

    @POST("/api/auth/token")
    suspend fun validateJwt(@Header("jwt") jwt: String): Token // jwt가 유효한지 검증하는 함수

    @POST("/api/auth/join")
    suspend fun joinUser(@Body request: Join): Login    // 입력된 유저 정보를 서버로 전송하는 함수

    @GET("/api/food/{userId}")
    suspend fun getFoodOnDate(
        @Path("userId") userId: Long?,
        @Query("yy") yy: Int,
        @Query("mm") mm: Int,
        @Query("dd") dd: Int,
        ): FoodOnDate

    @GET("/api/food/{userId}/nutrition")
    suspend fun getNutritionSummary(
        @Path("userId") userId: Long?,
        @Query("dd") dd: Int,
        @Query("mm") mm: Int,
        @Query("yy") yy: Int,
    ): NutritionSummary

    @POST("/api/food/save")
    suspend fun createFood(@Body createFoodDto: CreateFood)
}