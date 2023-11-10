package com.example.snack4diet.application

import android.app.Application
import com.example.snack4diet.api.ApiService
import com.kakao.sdk.common.KakaoSdk
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MyApplication : Application() {
    lateinit var apiService: ApiService
    override fun onCreate() {
        super.onCreate()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://diareat.thisiswandol.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        KakaoSdk.init(this, "032cc77ba17faa30703c6ff5d62735e6")
    }
}