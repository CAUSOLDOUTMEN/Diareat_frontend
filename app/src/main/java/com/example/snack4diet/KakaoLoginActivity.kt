package com.example.snack4diet

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
import java.lang.Exception

class KakaoLoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var app: MyApplication
    private lateinit var progressDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)

        app = applicationContext as MyApplication
        sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)

        //카카오톡 설치되어있는지 확인
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
            // 로딩 스피너
            showProgressDialog()
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

                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val response = app.apiService.kakaoLogin(token.accessToken)

                            if (response.data.id != null) { //id값이 null이 아닌 경우 이미 회원가입을 완료한 상태. MainActivity로 이동
                                sharedPreferences.edit().putLong("id", response.data.id).apply()
                                makeToast()
                            } else {    //id가 null인 경우 유저 정보 입력 페이지로 이동
                                val intent = Intent(this@KakaoLoginActivity, UserInfoActivity::class.java)
                                intent.putExtra("accessToken", token.accessToken)
                                startActivity(intent)
                            }

                        } catch (e: Exception) {
                            Log.e("KaKaoLoginActivity", "Error during Login API call", e)
                        } finally {
                            progressDialog.dismiss()
                        }
                    }
                }
            }
        } else {
            // 로딩 스피너
            showProgressDialog()
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

                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val response = app.apiService.kakaoLogin(token.accessToken)

                            if (response.data.id != null) { //id값이 null이 아닌 경우 이미 회원가입을 완료한 상태. MainActivity로 이동
                                sharedPreferences.edit().putLong("id", response.data.id).apply()
                                makeToast()
                            } else {    //id가 null인 경우 유저 정보 입력 페이지로 이동
                                val intent = Intent(this@KakaoLoginActivity, UserInfoActivity::class.java)
                                intent.putExtra("accessToken", token.accessToken)
                                startActivity(intent)
                            }

                        } catch (e: Exception) {
                            Log.e("KaKaoLoginActivity", "Error during Login API call", e)
                        } finally {
                            progressDialog.dismiss()
                        }
                    }
                }
            }
        }
    }

    private fun makeToast(){
        UserApiClient.instance.me { user, error ->
            Log.e(TAG, "닉네임 ${user?.kakaoAccount?.profile?.nickname}")

            val intent = Intent(this, MainActivity::class.java)
            Toast.makeText(this, "${user?.kakaoAccount?.profile?.nickname}님 환영합니다.", Toast.LENGTH_SHORT).show()
            startActivity(intent)
            finish()
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

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = app.apiService.kakaoLogin(token.accessToken)

                    if (response.data.id != null) { //id값이 null이 아닌 경우 이미 회원가입을 완료한 상태. MainActivity로 이동
                        println(response.data.jwt)
                        sharedPreferences.edit().putLong("id", response.data.id).apply()
                        makeToast()
                    } else {    //id가 null인 경우 유저 정보 입력 페이지로 이동
                        println(response.data.jwt)
                        val intent = Intent(this@KakaoLoginActivity, UserInfoActivity::class.java)
                        intent.putExtra("accessToken", token.accessToken)
                        startActivity(intent)
                    }

                } catch (e: Exception) {
                    Log.e("KaKaoLoginActivity", "Error during Login API call", e)
                } finally {
                    progressDialog.dismiss()
                }
            }
        }
    }

    private fun showProgressDialog() {
        progressDialog = AlertDialog.Builder(this)
            .setView(R.layout.progress_dialog)
            .setCancelable(false)
            .show()
    }
}