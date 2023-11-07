package com.example.snack4diet

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.snack4diet.application.MyApplication
import com.example.snack4diet.databinding.ActivityLoginBinding
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class KakaoLoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val app = applicationContext as MyApplication
        sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)

        //카카오톡 설치되어있는지 확인
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
            // 카카오톡 로그인
            UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                // 로그인 실패 부분
                if (error != null) {
                    Log.e(TAG, "로그인 실패 $error")
                    // 사용자가 취소
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled ) {
                        return@loginWithKakaoTalk
                    }
                    // 다른 오류
                    else {
                        UserApiClient.instance.loginWithKakaoAccount(this, callback = mCallback)
                    }
                }
                // 로그인 성공 부분
                else if (token != null) {
                    Log.e(TAG, "로그인 성공 ${token.accessToken}")

//                    CoroutineScope(Dispatchers.IO).launch {
//                        try {
//                            val response = app.apiService.getJwtToken(token.accessToken)
//                            Log.d("userId", response.userId.toString())
//                            sharedPreferences.edit().putLong("id", response.userId)
//                            saveAccessToken(response.tokenDto.accessToken)
//                            saveRefreshToken(response.tokenDto.refreshToken)
//                            checkStoredTokens()
//                        } catch (e: Exception) {
//                            Log.e("KaKaoLoginActivity", "Error during getJwtToken API call", e)
//                        }
//                        makeToast()
//                    }
                    makeToast()
                }
            }
        } else {
            //카카오 이메일 로그인
            UserApiClient.instance.loginWithKakaoAccount(this){ token, error ->
                // 로그인 실패 부분
                if (error != null) {
                    Log.e(TAG, "로그인 실패 $error")
                    // 사용자가 취소
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled ) {
                        return@loginWithKakaoAccount
                    }
                    // 다른 오류
                    else {
                        UserApiClient.instance.loginWithKakaoAccount(this, callback = mCallback)
                    }
                }
                // 로그인 성공 부분
                else if (token != null) {
                    Log.e(TAG, "로그인 성공 ${token.accessToken}")
//                    CoroutineScope(Dispatchers.IO).launch {
//                        try {
//                            val response = app.apiService.getJwtToken(token.accessToken)
//                            Log.d("userId", response.userId.toString())
//                            sharedPreferences.edit().putLong("id", response.userId)
//                            saveAccessToken(response.tokenDto.accessToken)
//                            saveRefreshToken(response.tokenDto.refreshToken)
//                            checkStoredTokens()
//                        } catch (e: Exception) {
//                            Log.e("KaKaoLoginActivity", "Error during getJwtToken API call", e)
//                        }
//                        makeToast()
//                    }
                    makeToast()
                }
            }
        }
    }

    private fun makeToast(){
        UserApiClient.instance.me { user, error ->
            Log.e(TAG, "닉네임 ${user?.kakaoAccount?.profile?.nickname}")
            Log.e(TAG, "이메일 ${user?.kakaoAccount?.email}")
            val app = applicationContext as MyApplication

            val intent = Intent(this, UserInfoActivity::class.java)
            startActivity(intent)
            finish()

//            if (email == user?.kakaoAccount?.email && nickname != null) {
//                val intent = Intent(this, MainActivity::class.java)
//                startActivity(intent)
//
//                finish()
//            } else {
//                val email = user?.kakaoAccount?.email!!
//                sharedPreferences.edit().putString("email", email).apply()
//
//                Toast.makeText(
//                    this,
//                    "${user?.kakaoAccount?.profile?.nickname}님 환영합니다.",
//                    Toast.LENGTH_SHORT
//                ).show()
//
//                val intent = Intent(this, MemberInformationActivity::class.java)
//
//                startActivity(intent)
//                finish()
//            }
        }
    }

    private val mCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            when {
                error.toString() == AuthErrorCause.AccessDenied.toString() -> {
                    Toast.makeText(this, "접근이 거부 됨(동의 취소)", Toast.LENGTH_SHORT).show()
                }
                error.toString() == AuthErrorCause.InvalidClient.toString() -> {
                    Toast.makeText(this, "유효하지 않은 앱", Toast.LENGTH_SHORT).show()
                }
                error.toString() == AuthErrorCause.InvalidGrant.toString() -> {
                    Toast.makeText(this, "인증 수단이 유효하지 않아 인증할 수 없는 상태", Toast.LENGTH_SHORT).show()
                }
                error.toString() == AuthErrorCause.InvalidRequest.toString() -> {
                    Toast.makeText(this, "요청 파라미터 오류", Toast.LENGTH_SHORT).show()
                }
                error.toString() == AuthErrorCause.InvalidScope.toString() -> {
                    Toast.makeText(this, "유효하지 않은 scope ID", Toast.LENGTH_SHORT).show()
                }
                error.toString() == AuthErrorCause.Misconfigured.toString() -> {
                    Toast.makeText(this, "설정이 올바르지 않음(android key hash)", Toast.LENGTH_SHORT).show()
                }
                error.toString() == AuthErrorCause.ServerError.toString() -> {
                    Toast.makeText(this, "서버 내부 에러", Toast.LENGTH_SHORT).show()
                }
                error.toString() == AuthErrorCause.Unauthorized.toString() -> {
                    Toast.makeText(this, "앱이 요청 권한이 없음", Toast.LENGTH_SHORT).show()
                }
                else -> { // Unknown
                    Toast.makeText(this, "기타 에러", Toast.LENGTH_SHORT).show()
                }
            }
        }
        else if (token != null) {
            Log.e(TAG, "로그인 성공 ${token.accessToken}")

            val app = applicationContext as MyApplication

//            CoroutineScope(Dispatchers.IO).launch {
//                try {
//                    val response = app.apiService.getJwtToken(token.accessToken)
//                    Log.d("userId", response.userId.toString())
//                    sharedPreferences.edit().putLong("id", response.userId)
//                    saveAccessToken(response.tokenDto.accessToken)
//                    saveRefreshToken(response.tokenDto.refreshToken)
//
//                    checkStoredTokens()
//                } catch (e: Exception) {
//                    Log.e("KaKaoLoginActivity", "Error during getJwtToken API call", e)
//                }
//                makeToast()
//            }
            makeToast()
        }
    }
}