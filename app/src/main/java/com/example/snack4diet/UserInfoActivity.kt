package com.example.snack4diet

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.RadioGroup
import android.widget.Toast
import com.example.snack4diet.api.Join
import com.example.snack4diet.api.UserInfo
import com.example.snack4diet.application.MyApplication
import com.example.snack4diet.databinding.ActivityUserInfoBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class UserInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserInfoBinding
    private lateinit var radioGroup: RadioGroup
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var app: MyApplication
    private var accessToken = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = applicationContext as MyApplication
        sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        radioGroup = binding.sex

        accessToken = intent.getStringExtra("accessToken")!!

        val textWatcher = object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val isClickable =
                    binding.age.text.isNotEmpty()
                            && binding.nickname.text.isNotEmpty()
                            && binding.weight.text.isNotEmpty()
                            && binding.height.text.isNotEmpty()
                            && radioGroup.checkedRadioButtonId != -1

                if (isClickable) {
                    binding.btnSave.isClickable = true
                    binding.btnSave.setImageResource(R.drawable.ic_btn_save)
                } else {
                    binding.btnSave.isClickable = false
                    binding.btnSave.setImageResource(R.drawable.ic_btn_save_disabled)
                }

                binding.btnSave.setOnClickListener {
                    saveUserInfo()
                }
            }
        }

        binding.age.addTextChangedListener(textWatcher)
        binding.height.addTextChangedListener(textWatcher)
        binding.nickname.addTextChangedListener(textWatcher)
        binding.weight.addTextChangedListener(textWatcher)
    }

    private fun saveUserInfo() {
        val nickname = binding.nickname.text.toString()
        var gender = 0
        val height = binding.height.text.toString().toInt()
        val weight = binding.weight.text.toString().toInt()
        val age = binding.age.text.toString().toInt()
        val app = applicationContext as MyApplication

        if (radioGroup.checkedRadioButtonId == R.id.male) gender = 0
        else if (radioGroup.checkedRadioButtonId == R.id.female) gender = 1

        val user = Join(age, gender, height, nickname, accessToken, weight)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = app.apiService.joinUser(user)
                sharedPreferences.edit().putLong("id", response.data.id!!).apply()
                sharedPreferences.edit().putString("jwt", response.data.jwt).apply()
                val intent = Intent(this@UserInfoActivity, MainActivity::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                Log.e("UserInfoActivity", "Error during joinUser API call", e)
            }
        }

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

        finish()
    }
}