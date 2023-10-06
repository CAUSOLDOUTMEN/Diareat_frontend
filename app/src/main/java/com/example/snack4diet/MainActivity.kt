package com.example.snack4diet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.snack4diet.bookmark.BookmarkFragment
import com.example.snack4diet.databinding.ActivityMainBinding
import com.example.snack4diet.home.HomeFragment
import com.example.snack4diet.profile.ProfileFragment
import com.example.snack4diet.viewModel.NutrientsViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: NutrientsViewModel
    private lateinit var navigation: FrameLayout
    private lateinit var camera: ImageButton
    private lateinit var currentFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(NutrientsViewModel::class.java)
        navigation = binding.navigationFrame
        camera = binding.btnCamera

        setHomeFragment()
    }

    private fun setHomeFragment() {
        val homeFragment = HomeFragment()
        navigation.visibility = View.VISIBLE
        camera.visibility = View.VISIBLE

        replaceFragment(homeFragment, "homeFragment")
    }

    fun replaceFragment(fragment: Fragment, tag: String) {
        currentFragment = fragment

        if (fragment is BookmarkFragment || fragment is ProfileFragment) {
            navigation.visibility = View.GONE
            camera.visibility = View.GONE
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.mainFrame, fragment, tag)
            .addToBackStack(null) // 이전 프래그먼트를 백스택에 추가
            .commit()
    }

    fun popFragment() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        }
    }

    fun getViewModel(): NutrientsViewModel {
        return viewModel
    }

    override fun onBackPressed() {
        super.onBackPressed()

        if (currentFragment is BookmarkFragment) {
            navigation.visibility = View.VISIBLE
            camera.visibility = View.VISIBLE
        } else if (currentFragment is ProfileFragment) {
            navigation.visibility = View.VISIBLE
            camera.visibility = View.VISIBLE

            setHomeFragment()
        }
    }
}