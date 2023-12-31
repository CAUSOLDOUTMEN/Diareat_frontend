package com.example.foodfood.application

import android.app.Application
import android.content.SharedPreferences
import com.example.foodfood.api.ApiService
import com.example.foodfood.api.OcrService
import com.example.foodfood.api.createFood.BaseNutrition
import com.kakao.sdk.common.KakaoSdk
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

class MyApplication : Application() {
    lateinit var apiService: ApiService
    lateinit var ocrService: OcrService
    var baseNutrition = BaseNutrition(0.0, 0.0, 0.0, 0.0)
    private lateinit var sharedPreferences: SharedPreferences
    private var accessToken: String? = ""
    override fun onCreate() {
        super.onCreate()
        sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://diareat.thisiswandol.com/")
//            .baseUrl("http://3.36.25.214:80")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val client = OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
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

    fun getSharedPrefs(): SharedPreferences {
        return sharedPreferences
    }
}