package com.example.snack4diet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioGroup
import android.widget.Toast
import com.example.snack4diet.api.UserInfo
import com.example.snack4diet.databinding.ActivityUserInfoBinding

class UserInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserInfoBinding
    private lateinit var radioGroup: RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        radioGroup = binding.sex

        binding.btnSave.setOnClickListener {
            saveUserInfo()
        }
    }

    private fun saveUserInfo() {
        if (binding.height.text.isNullOrEmpty() || binding.weight.text.isNullOrEmpty() || binding.age.text.isNullOrEmpty()) {
            Toast.makeText(this, "모든 항목을 채워주세요.",
                Toast.LENGTH_SHORT).show()
        } else {
            val selectedButton = radioGroup.checkedRadioButtonId

            if (selectedButton == -1) {
                Toast.makeText(this, "성별을 선택해주세요.",
                Toast.LENGTH_SHORT).show()
            } else {
                val sex: Boolean = selectedButton == R.id.male // false이면 여성 true이면 남성
                val height = binding.height.text.toString().toFloat()
                val weight = binding.weight.text.toString().toFloat()
                val age = binding.age.text.toString().toInt()
                val user = UserInfo(sex, height, weight, age)

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }
}