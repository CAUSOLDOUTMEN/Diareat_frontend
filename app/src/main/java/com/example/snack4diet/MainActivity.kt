package com.example.snack4diet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.snack4diet.databinding.ActivityMainBinding
import com.example.snack4diet.home.HomeFragment
import com.example.snack4diet.viewModel.NutrientsViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: NutrientsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(NutrientsViewModel::class.java)

        setHomeFragment()
    }

    private fun setHomeFragment() {
        val homeFragment = HomeFragment()

        replaceFragment(homeFragment, "homeFragment")
    }

    fun replaceFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.mainFrame, fragment, tag)
            .addToBackStack(null) // 이전 프래그먼트를 백스택에 추가
            .commit()
    }

    fun getViewModel(): NutrientsViewModel {
        return viewModel
    }
}