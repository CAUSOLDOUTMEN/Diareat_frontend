package com.example.snack4diet

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.snack4diet.bookmark.BookmarkFragment
import com.example.snack4diet.databinding.ActivityMainBinding
import com.example.snack4diet.home.HomeFragment
import com.example.snack4diet.home.camera.CameraActivity
import com.example.snack4diet.profile.NutrientProfileEditFragment
import com.example.snack4diet.profile.ProfileFragment
import com.example.snack4diet.viewModel.NutrientsViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: NutrientsViewModel
    private lateinit var navigation: FrameLayout
    private lateinit var camera: ImageButton
    private lateinit var currentFragment: Fragment

    private val CAMERA_PERMISSION_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(NutrientsViewModel::class.java)
        navigation = binding.navigationFrame
        camera = binding.btnCamera

        setHomeFragment()

        binding.btnCamera.setOnClickListener {
            checkPermission()
        }
    }

    private fun setHomeFragment() {
        val homeFragment = HomeFragment()
        navigation.visibility = View.VISIBLE
        camera.visibility = View.VISIBLE

        replaceFragment(homeFragment, "homeFragment")
    }

    fun replaceFragment(fragment: Fragment, tag: String) {
        currentFragment = fragment

        if (fragment is BookmarkFragment || fragment is ProfileFragment || fragment is NutrientProfileEditFragment) {
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

    private fun checkPermission() {
        val cameraPermission = android.Manifest.permission.CAMERA
        val storagePermission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE // 또는 Manifest.permission.READ_EXTERNAL_STORAGE

        val cameraPermissionGranted = ContextCompat.checkSelfPermission(this, cameraPermission) == PackageManager.PERMISSION_GRANTED
        val storagePermissionGranted = ContextCompat.checkSelfPermission(this, storagePermission) == PackageManager.PERMISSION_GRANTED

        if (cameraPermissionGranted && storagePermissionGranted) {
            executeCamera()
        } else {
            val permissionsToRequest = ArrayList<String>()
            if (!cameraPermissionGranted) {
                permissionsToRequest.add(cameraPermission)
            }
            if (!storagePermissionGranted) {
                permissionsToRequest.add(storagePermission)
            }

            val permissionsArray = permissionsToRequest.toTypedArray()
            ActivityCompat.requestPermissions(this, permissionsArray, CAMERA_PERMISSION_REQUEST_CODE)
        }
    }


    private fun executeCamera() {
        val intent = Intent(this, CameraActivity::class.java)
        startActivity(intent)
    }
}