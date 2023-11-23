package com.example.snack4diet.api

import com.example.snack4diet.api.addBookmark.AddBookmark
import com.example.snack4diet.api.analysisGraph.NutrientAnalysisGraph
import com.example.snack4diet.api.bestWorst.BestWorst
import com.example.snack4diet.api.createFood.CreateFood
import com.example.snack4diet.api.createFoodFromBookmark.CreateFoodFromBookmark
import com.example.snack4diet.api.editFood.EditFood
import com.example.snack4diet.api.foodOnDate.FoodOnDate
import com.example.snack4diet.api.getBookmark.GetBookmark
import com.example.snack4diet.api.nutritionSummary.NutritionSummary
import com.example.snack4diet.api.ocr.ResponseOcr
import com.example.snack4diet.api.searchUser.SearchUser
import com.example.snack4diet.api.searchUserResponse.SearchUserResponse
import com.example.snack4diet.api.updateFavoriteFood.UpdateFavoriteFoodDto
import com.example.snack4diet.api.updateUser.UpdateUserDto
import com.example.snack4diet.api.updateUserStandardIntake.UpdateUserStandardIntake
import com.example.snack4diet.api.userInfo.UserInfo
import com.example.snack4diet.api.userInfoSimple.SimpleUserInfo
import com.example.snack4diet.api.userStandardIntake.UserStandardIntake
import com.example.snack4diet.api.weeklyRank.WeeklyRank
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
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

    @DELETE("/api/food/{foodId}/delete")
    suspend fun deleteFood(
        @Path("foodId") foodId: Long,
        @Header("userId") userId: Long,
        @Query("dd") dd: Int,
        @Query("mm") mm: Int,
        @Query("yy") yy: Int,
    )

    @POST("/api/food/update")
    suspend fun editFood(
        @Body updateFoodDto: EditFood,
        @Query("dd") dd: Int,
        @Query("mm") mm: Int,
        @Query("yy") yy: Int,
    )

    @POST("/api/food/favorite")
    suspend fun addBookmark(@Body createFavoriteFoodDto: AddBookmark)

    @GET("/api/food/favorite/{userId}")
    suspend fun getFavoriteFood(@Path("userId") userId: Long?): GetBookmark

    @POST("/api/food/favorite/createfrom")
    suspend fun createFoodFromBookmark(@Body createFoodFromBookmark: CreateFoodFromBookmark)

    @DELETE("/api/food/favorite/{favoriteFoodId}")
    suspend fun deleteBookmark(
        @Path("favoriteFoodId") favoriteFoodId: Long,
        @Header("userId") userId: Long
    )

    @POST("/api/food/favorite/update")
    suspend fun updateBookmark(
        @Body updateFavoriteFoodDto: UpdateFavoriteFoodDto
    )

    @GET("/api/food/{userId}/analysis")
    suspend fun getAnalysisGraphData(
        @Path("userId") userId: Long,
        @Query("dd") dd: Int,
        @Query("mm") mm: Int,
        @Query("yy") yy: Int,
    ): NutrientAnalysisGraph

    @GET("/api/food/{userId}/score")
    suspend fun getBestWorstFood(
        @Path("userId") userId: Long,
        @Query("dd") dd: Int,
        @Query("mm") mm: Int,
        @Query("yy") yy: Int,
    ): BestWorst

    @GET("/api/user/{userId}/info/simple")
    suspend fun getSimpleUserInfo(@Path("userId") userId: Long): SimpleUserInfo

    @GET("/api/user/{userId}/info")
    suspend fun getUserInfo(@Path("userId") userId: Long): UserInfo

    @PUT("/api/user/update")
    suspend fun updateUserInfo(@Body updateUserDto: UpdateUserDto)

    @GET("/api/user/{userId}/nutrition")
    suspend fun getUserStandardIntake(@Path("userId") userId: Long): UserStandardIntake

    @PUT("/api/user/{userId}/nutrition")
    suspend fun updateUserStandardIntake(@Body updateUserStandardIntake: UpdateUserStandardIntake)

    @GET("/api/food/{userId}/rank")
    suspend fun getWeeklyRank(
        @Path("userId") userId: Long,
        @Query("dd") dd: Int,
        @Query("mm") mm: Int,
        @Query("yy") yy: Int,
    ): WeeklyRank

    @POST("/api/user/search")
    suspend fun searchUser(@Body searchUserDto: SearchUser): SearchUserResponse

    @POST("/api/user/{userId}/follow/{followId}")
    suspend fun followUser(@Path("userId") userId: Long, @Path("followId") followId: Long)

    @DELETE("/api/user/{userId}/follow/{followId}")
    suspend fun unfollowUser(@Path("userId") userId: Long, @Path("followId") followId: Long)
}