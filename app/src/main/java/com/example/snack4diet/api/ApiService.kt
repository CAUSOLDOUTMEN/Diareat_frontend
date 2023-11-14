package com.example.snack4diet.api

import com.example.snack4diet.api.addBookmark.AddBookmark
import com.example.snack4diet.api.createFood.CreateFood
import com.example.snack4diet.api.editFood.EditFood
import com.example.snack4diet.api.foodOnDate.FoodOnDate
import com.example.snack4diet.api.getBookmark.GetBookmark
import com.example.snack4diet.api.nutritionSummary.NutritionSummary
import com.example.snack4diet.api.ocr.ResponseOcr
import com.example.snack4diet.api.updateFavoriteFood.UpdateFavoriteFoodDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("/parse_nutrients")
    suspend fun sendImageKey(@Body imageKey: String): ResponseOcr

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

    @DELETE("/api/food/{foodId}/delete")
    suspend fun deleteFood(
        @Header("userId") userId: Long,
        @Path("foodId") foodId: Long
    )

    @POST("/api/food/update")
    suspend fun editFood(@Body updateFoodDto: EditFood)

    @POST("/api/food/favorite")
    suspend fun addBookmark(@Body createFavoriteFoodDto: AddBookmark)

    @GET("/api/food/favorite/{userId}")
    suspend fun getFavoriteFood(@Path("userId") userId: Long?): GetBookmark

    @DELETE("/api/food/favorite/{favoriteFoodId}")
    suspend fun deleteBookmark(
        @Path("favoriteFoodId") favoriteFoodId: Long,
        @Header("userId") userId: Long
    )

    @POST("/api/food/favorite/update")
    suspend fun updateBookmark(
        @Body updateFavoriteFoodDto: UpdateFavoriteFoodDto
    )
}