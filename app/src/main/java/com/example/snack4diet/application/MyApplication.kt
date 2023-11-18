package com.example.snack4diet.application

import android.app.Application
import com.example.snack4diet.api.ApiService
import com.example.snack4diet.api.OcrService
import com.kakao.sdk.common.KakaoSdk
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class MyApplication : Application() {
    lateinit var apiService: ApiService
    lateinit var ocrService: OcrService
    override fun onCreate() {
        super.onCreate()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://diareat.thisiswandol.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val client = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

        val ocrRetrofit = Retrofit.Builder()
            .baseUrl("http://43.202.154.53:8000")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)
        ocrService = ocrRetrofit.create(OcrService::class.java)

        KakaoSdk.init(this, "032cc77ba17faa30703c6ff5d62735e6")
    }
}