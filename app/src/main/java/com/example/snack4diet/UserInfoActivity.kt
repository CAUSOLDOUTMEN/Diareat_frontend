package com.example.snack4diet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
        val sex: Boolean = radioGroup.checkedRadioButtonId == R.id.male // false이면 여성 true이면 남성
        val height = binding.height.text.toString().toFloat()
        val weight = binding.weight.text.toString().toFloat()
        val age = binding.age.text.toString().toInt()
        val user = UserInfo(nickname , height, weight, sex, age)

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}