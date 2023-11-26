package com.example.foodfood

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.foodfood.application.MyApplication

class SplashActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var app: MyApplication
    private var jwt: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        app = applicationContext as MyApplication
        sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        jwt = sharedPreferences.getString("jwt", null)

        Handler(Looper.getMainLooper()).postDelayed({

//            if (jwt == null) {  // 신규 유저 로그인
//                val intent = Intent(this, LoginActivity::class.java)
//                startActivity(intent)
//            } else {    // 기존 유저 로그인
//                val intent = Intent(this, MainActivity::class.java)
//                startActivity(intent)
//            }

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

            // 이전 키를 눌렀을 때 스플래시 스크린 화면으로 이동을 방지하기 위해
            // 이동한 다음 사용안함으로 finish 처리
            finish()

        }, 2000) // 시간 2초 이후 실행
    }
}