package com.example.foodfood.api

import com.example.foodfood.api.addBookmark.AddBookmark
import com.example.foodfood.api.analysisGraph.NutrientAnalysisGraph
import com.example.foodfood.api.bestWorst.BestWorst
import com.example.foodfood.api.createFood.CreateFood
import com.example.foodfood.api.createFoodFromBookmark.CreateFoodFromBookmark
import com.example.foodfood.api.editFood.EditFood
import com.example.foodfood.api.foodOnDate.FoodOnDate
import com.example.foodfood.api.getBookmark.GetBookmark
import com.example.foodfood.api.nutritionSummary.NutritionSummary
import com.example.foodfood.api.searchUser.SearchUser
import com.example.foodfood.api.searchUserResponse.SearchUserResponse
import com.example.foodfood.api.updateFavoriteFood.UpdateFavoriteFoodDto
import com.example.foodfood.api.updateUser.UpdateUserDto
import com.example.foodfood.api.updateUserStandardIntake.UpdateUserStandardIntake
import com.example.foodfood.api.userInfo.UserInfo
import com.example.foodfood.api.userInfoSimple.SimpleUserInfo
import com.example.foodfood.api.userStandardIntake.UserStandardIntake
import com.example.foodfood.api.weeklyRank.WeeklyRank
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
    suspend fun getFoodOnDate(  //날짜별 먹은 음식 리스트 조회
        @Header("accessToken") accessToken: String,
        @Path("userId") userId: Long?,
        @Query("yy") yy: Int,
        @Query("mm") mm: Int,
        @Query("dd") dd: Int,
        ): FoodOnDate

    @GET("/api/food/{userId}/nutrition")
    suspend fun getNutritionSummary(    //날짜별 총 영양성분 섭취 요약
        @Header("accessToken") accessToken: String,
        @Path("userId") userId: Long?,
        @Query("dd") dd: Int,
        @Query("mm") mm: Int,
        @Query("yy") yy: Int,
    ): NutritionSummary

    @POST("/api/food/save")
    suspend fun createFood( // 음식 생성
        @Header("accessToken") accessToken: String,
        @Body createFoodDto: CreateFood
    )

    @DELETE("/api/food/{foodId}/delete")
    suspend fun deleteFood( // 음식 삭제
        @Header("accessToken") accessToken: String,
        @Path("foodId") foodId: Long,
        @Header("userId") userId: Long,
        @Query("dd") dd: Int,
        @Query("mm") mm: Int,
        @Query("yy") yy: Int,
    )

    @POST("/api/food/update")
    suspend fun editFood(   //음식 수정
        @Header("accessToken") accessToken: String,
        @Body updateFoodDto: EditFood,
        @Query("dd") dd: Int,
        @Query("mm") mm: Int,
        @Query("yy") yy: Int,
    )

    @POST("/api/food/favorite")
    suspend fun addBookmark(    //즐겨찾기 추가
        @Header("accessToken") accessToken: String,
        @Body createFavoriteFoodDto: AddBookmark
    )

    @GET("/api/food/favorite/{userId}")
    suspend fun getFavoriteFood(    //즐겨찾기 목록 조회
        @Header("accessToken") accessToken: String,
        @Path("userId") userId: Long?
    ): GetBookmark

    @POST("/api/food/favorite/createfrom")
    suspend fun createFoodFromBookmark( //즐겨찾기에서 다이어리로 음식 추가
        @Header("accessToken") accessToken: String,
        @Body createFoodFromBookmark: CreateFoodFromBookmark
    )

    @DELETE("/api/food/favorite/{favoriteFoodId}")
    suspend fun deleteBookmark( // 즐겨찾기 삭제
        @Header("accessToken") accessToken: String,
        @Path("favoriteFoodId") favoriteFoodId: Long,
        @Header("userId") userId: Long
    )

    @POST("/api/food/favorite/update")
    suspend fun updateBookmark( //즐겨찾기 수정
        @Header("accessToken") accessToken: String,
        @Body updateFavoriteFoodDto: UpdateFavoriteFoodDto
    )

    @GET("/api/food/{userId}/analysis")
    suspend fun getAnalysisGraphData(   //음식 그래프 조회
        @Header("accessToken") accessToken: String,
        @Path("userId") userId: Long,
        @Query("dd") dd: Int,
        @Query("mm") mm: Int,
        @Query("yy") yy: Int,
    ): NutrientAnalysisGraph

    @GET("/api/food/{userId}/score")
    suspend fun getBestWorstFood(   //베스트, 워스트 조회
        @Header("accessToken") accessToken: String,
        @Path("userId") userId: Long,
        @Query("dd") dd: Int,
        @Query("mm") mm: Int,
        @Query("yy") yy: Int,
    ): BestWorst

    @GET("/api/user/{userId}/info/simple")
    suspend fun getSimpleUserInfo(  //프로필 간편 조회
        @Header("accessToken") accessToken: String,
        @Path("userId") userId: Long
    ): SimpleUserInfo

    @GET("/api/user/{userId}/info")
    suspend fun getUserInfo(    //유저 정보 조회
        @Header("accessToken") accessToken: String,
        @Path("userId") userId: Long
    ): UserInfo

    @PUT("/api/user/update")
    suspend fun updateUserInfo( //유저 정보 수정
        @Header("accessToken") accessToken: String,
        @Body updateUserDto: UpdateUserDto
    )

    @GET("/api/user/{userId}/nutrition")
    suspend fun getUserStandardIntake(  //유저의 기준 섭취량 조회
        @Header("accessToken") accessToken: String,
        @Path("userId") userId: Long
    ): UserStandardIntake

    @PUT("/api/user/{userId}/nutrition")
    suspend fun updateUserStandardIntake(   //유저의 목표 영양성분 섭취량 조회
        @Header("accessToken") accessToken: String,
        @Body updateUserStandardIntake: UpdateUserStandardIntake
    )

    @GET("/api/food/{userId}/rank")
    suspend fun getWeeklyRank(  //주간 랭킹 조회
        @Header("accessToken") accessToken: String,
        @Path("userId") userId: Long,
        @Query("dd") dd: Int,
        @Query("mm") mm: Int,
        @Query("yy") yy: Int,
    ): WeeklyRank

    @POST("/api/user/search")
    suspend fun searchUser( //유저 검색
        @Header("accessToken") accessToken: String,
        @Body searchUserDto: SearchUser
    ): SearchUserResponse

    @POST("/api/user/{userId}/follow/{followId}")
    suspend fun followUser( //특정 유저 팔로우
        @Header("accessToken") accessToken: String,
        @Path("userId") userId: Long,
        @Path("followId") followId: Long
    )

    @DELETE("/api/user/{userId}/follow/{followId}")
    suspend fun unfollowUser(   //특정 유저 팔로우 취소
        @Header("accessToken") accessToken: String,
        @Path("userId") userId: Long,
        @Path("followId") followId: Long
    )
}