package com.example.snack4diet

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.snack4diet.analysis.AnalysisFragment
import com.example.snack4diet.analysis.DiaryAnalysisDetailFragment
import com.example.snack4diet.api.addBookmark.AddBookmark
import com.example.snack4diet.api.createFood.CreateFood
import com.example.snack4diet.api.editFood.EditFood
import com.example.snack4diet.api.getBookmark.Data
import com.example.snack4diet.api.getBookmark.GetBookmark
import com.example.snack4diet.application.MyApplication
import com.example.snack4diet.bookmark.BookmarkFragment
import com.example.snack4diet.databinding.ActivityMainBinding
import com.example.snack4diet.databinding.DialogCameraGuideBinding
import com.example.snack4diet.home.FoodEntryFragment
import com.example.snack4diet.home.HomeFragment
import com.example.snack4diet.home.camera.CameraActivity
import com.example.snack4diet.profile.NutrientProfileEditFragment
import com.example.snack4diet.profile.ProfileFragment
import com.example.snack4diet.viewModel.NutrientsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: NutrientsViewModel
    private lateinit var navigation: FrameLayout
    private lateinit var camera: ImageButton
    lateinit var application: MyApplication
    private lateinit var currentFragment: Fragment
    private var doubleBackToExitPressedOnce = false
    private val backButtonInterval = 200
    private var userId = -1L
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var bookmarkList: List<Data>

    private val CAMERA_PERMISSION_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        application = applicationContext as MyApplication

        sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        viewModel = ViewModelProvider(this).get(NutrientsViewModel::class.java)
        navigation = binding.navigationFrame
        camera = binding.btnCamera
        userId = sharedPreferences.getLong("id", -1L)
        bookmarkList = emptyList()

        setHomeFragment()

        binding.btnCamera.setOnClickListener {
            checkPermission()
        }

        binding.btnHome.setOnClickListener {
            setHomeFragment()
        }

        binding.btnStatistics.setOnClickListener {
            setAnalysisFragment()
        }
    }

    fun setHomeFragment() {
        val homeFragment = HomeFragment()
        navigation.visibility = View.VISIBLE
        camera.visibility = View.VISIBLE
        binding.btnHome.setImageResource(R.drawable.ic_home_checked)
        binding.btnStatistics.setImageResource(R.drawable.ic_statistics_unchecked)

        replaceFragment(homeFragment, "homeFragment")
    }

    private fun setAnalysisFragment() {
        val analysisFragment = AnalysisFragment()
        navigation.visibility = View.VISIBLE
        camera.visibility = View.VISIBLE
        binding.btnHome.setImageResource(R.drawable.ic_home_unchecked)
        binding.btnStatistics.setImageResource(R.drawable.ic_statistics_checked)

        replaceFragment(analysisFragment, "analysisFragment")
    }

    fun setDiaryAnalysisDetailFragment() {
        val diaryAnalysisDetailFragment = DiaryAnalysisDetailFragment()

        replaceFragment(diaryAnalysisDetailFragment, "diaryAnalysisDetailFragment")
    }

    fun replaceFragment(fragment: Fragment, tag: String) {
        currentFragment = fragment

        if (
            fragment is BookmarkFragment ||
            fragment is ProfileFragment ||
            fragment is NutrientProfileEditFragment ||
            fragment is FoodEntryFragment
        ) {
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
        val currentFragment = supportFragmentManager.findFragmentById(R.id.mainFrame)

        if (currentFragment is BookmarkFragment || currentFragment is FoodEntryFragment) {
            navigation.visibility = View.VISIBLE
            camera.visibility = View.VISIBLE
            super.onBackPressed()
        } else if (currentFragment is ProfileFragment) {
            navigation.visibility = View.VISIBLE
            camera.visibility = View.VISIBLE

            setHomeFragment()
        } else if (currentFragment is HomeFragment || currentFragment is AnalysisFragment) {
            if (doubleBackToExitPressedOnce) {
                finish()
            } else {
                this.doubleBackToExitPressedOnce = true
                Toast.makeText(this, "뒤로가기 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()

                Handler().postDelayed({
                    doubleBackToExitPressedOnce = false
                }, backButtonInterval.toLong())
            }
        } else {
            super.onBackPressed()
        }
    }

    private fun checkPermission() {
        val cameraPermission = android.Manifest.permission.CAMERA
        val storagePermission =
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE // 또는 Manifest.permission.READ_EXTERNAL_STORAGE

        val cameraPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            cameraPermission
        ) == PackageManager.PERMISSION_GRANTED
        val storagePermissionGranted = ContextCompat.checkSelfPermission(
            this,
            storagePermission
        ) == PackageManager.PERMISSION_GRANTED

        if (cameraPermissionGranted && storagePermissionGranted) {
            openCameraGuideDialog()
        } else {
            val permissionsToRequest = ArrayList<String>()
            if (!cameraPermissionGranted) {
                permissionsToRequest.add(cameraPermission)
            }
            if (!storagePermissionGranted) {
                permissionsToRequest.add(storagePermission)
            }

            val permissionsArray = permissionsToRequest.toTypedArray()
            ActivityCompat.requestPermissions(
                this,
                permissionsArray,
                CAMERA_PERMISSION_REQUEST_CODE
            )
        }
    }


    private fun executeCamera() {
        val intent = Intent(this, CameraActivity::class.java)
        startActivity(intent)
    }

    private fun openCameraGuideDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_camera_guide)

        val dialogBinding =
            DialogCameraGuideBinding.bind(dialog.findViewById(R.id.dialogCameraGuide))

        dialogBinding.btnStartCamera.setOnClickListener {
            executeCamera()
            dialog.dismiss()
        }

        dialog.show()

        val window = dialog.window
        window?.setBackgroundDrawableResource(R.drawable.round_frame_white_20)
    }

    fun getUserId(): Long {
        return userId
    }

    fun createFood(food: CreateFood) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                application.apiService.createFood(food)
                Log.e("ㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏ", food.toString())
            } catch (e: Exception) {
                Log.e("MainActivity", "Error during createFood API call", e)
            }
        }

        runOnUiThread {
            Toast.makeText(this, "저장이 완료되었습니다.", Toast.LENGTH_SHORT)
            setHomeFragment()
        }
    }

    fun deleteFood(foodId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                application.apiService.deleteFood(userId, foodId)
            } catch (e: Exception) {
                Log.e("MainActivity", "Error during deleteFood API call", e)
            }
        }
    }

    fun editFood(newFood: EditFood) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                application.apiService.editFood(newFood)
            } catch (e: Exception) {
                Log.e("MainActivity", "Error during editFood API call", e)
            }
        }
    }

    fun addBookmark(food: AddBookmark) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                application.apiService.addBookmark(food)
                Log.e("뭐야ㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏ", food.toString())
            } catch (e: Exception) {
                Log.e("MainActivity", "Error during addBookmark API call", e)
            }
        }
    }

    fun setBookmarkList() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = application.apiService.getFavoriteFood(userId)
                bookmarkList = response.data
                Log.e("ㅓㅓㅓㅓㅓㅓㅓㅓㅓㅓㅓㅓㅓㅓㅓㅓ", bookmarkList.toString())
            } catch (e: Exception) {
                Log.e("MainActivity", "Error during getFavoriteFood API call", e)
            }

            withContext(Dispatchers.Main) {
                val fragment = BookmarkFragment()
                replaceFragment(fragment, "BookmarkFragment")
            }
        }
    }

    fun getBookmarkList(): List<Data> {
        return bookmarkList
    }
}