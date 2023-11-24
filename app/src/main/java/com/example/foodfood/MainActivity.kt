package com.example.foodfood

import android.app.Activity
import android.app.Dialog
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.foodfood.analysis.AnalysisFragment
import com.example.foodfood.analysis.DiaryAnalysisDetailFragment
import com.example.foodfood.api.addBookmark.AddBookmark
import com.example.foodfood.api.createFood.CreateFood
import com.example.foodfood.api.createFoodFromBookmark.CreateFoodFromBookmark
import com.example.foodfood.api.editFood.EditFood
import com.example.foodfood.api.getBookmark.Data
import com.example.foodfood.api.searchUser.SearchUser
import com.example.foodfood.api.searchUserResponse.SearchUserResponse
import com.example.foodfood.api.updateFavoriteFood.UpdateFavoriteFoodDto
import com.example.foodfood.api.updateUser.UpdateUserDto
import com.example.foodfood.api.updateUserStandardIntake.UpdateUserStandardIntake
import com.example.foodfood.api.userInfo.UserInfo
import com.example.foodfood.api.userStandardIntake.UserStandardIntake
import com.example.foodfood.application.MyApplication
import com.example.foodfood.bookmark.BookmarkFragment
import com.example.foodfood.databinding.ActivityMainBinding
import com.example.foodfood.databinding.DialogCameraGuideBinding
import com.example.foodfood.home.FoodEntryFragment
import com.example.foodfood.home.HomeFragment
import com.example.foodfood.home.camera.AnalysisResultFragment
import com.example.foodfood.home.camera.CameraActivity
import com.example.foodfood.home.camera.foodlens.FoodlensActivity
import com.example.foodfood.profile.NutrientProfileEditFragment
import com.example.foodfood.profile.ProfileFragment
import com.example.foodfood.viewModel.NutrientsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import kotlin.Exception

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
    private val CAMERA_REQUEST_CODE = 123   //카메라 액티비티 실행 후 결과 처리를 위한 변수

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

        binding.btnTemp.setOnClickListener {
            val intent = Intent(this, FoodlensActivity::class.java)
            startActivity(intent)
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
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val currentDate = LocalDate.now()
                val year = currentDate.year
                val month = currentDate.monthValue
                val day = currentDate.dayOfMonth

                val response = application.apiService.getBestWorstFood(userId, day, month, year)
                Log.e("정체가 뭐냐", response.data.toString())

                withContext(Dispatchers.Main) {
                    val diaryAnalysisDetailFragment = DiaryAnalysisDetailFragment(response.data)
                    replaceFragment(diaryAnalysisDetailFragment, "diaryAnalysisDetailFragment")
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error during getBestWorst API call", e)
            }
        }
    }

    fun replaceFragment(fragment: Fragment, tag: String) {
        currentFragment = fragment

        if (
            fragment is BookmarkFragment ||
            fragment is ProfileFragment ||
            fragment is NutrientProfileEditFragment ||
            fragment is FoodEntryFragment ||
            fragment is AnalysisResultFragment
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

    private val startCameraForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val fragment = AnalysisResultFragment()

                replaceFragment(fragment, "AnalysisResultFragment")
            }
        }

    fun executeCamera() {
        val intent = Intent(this, CameraActivity::class.java)
        startCameraForResult.launch(intent)
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

    fun deleteFood(foodId: Long, yy: Int, mm: Int, dd: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.e("뭐가 문젠데ㅔㅔㅔㅔㅔ", foodId.toString())
                Log.e("뭐가 문젠데ㅔㅔㅔㅔㅔ", userId.toString())
                application.apiService.deleteFood(foodId, userId, dd, mm, yy)
            } catch (e: Exception) {
                Log.e("MainActivity", "Error during deleteFood API call", e)
            }
        }
    }

    fun editFood(newFood: EditFood) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val currentDate = LocalDate.now()
                val year = currentDate.year
                val month = currentDate.monthValue
                val day = currentDate.dayOfMonth

                application.apiService.editFood(newFood, day, month, year)
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

    suspend fun getBookmarkList(): List<Data>? {
        return withContext(Dispatchers.Main) {
            try {
                val response = application.apiService.getFavoriteFood(userId)
                Log.e("ㅓㅓㅓㅓㅓㅓㅓㅓㅓㅓㅓㅓㅓㅓㅓㅓ", response.data.toString())
                return@withContext response.data
            } catch (e: Exception) {
                Log.e("MainActivity", "Error during getFavoriteFood API call", e)
                return@withContext null
            }
        }
    }

    fun createFoodFromBookmark(favoriteFoodId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val createFoodFromBookmark = CreateFoodFromBookmark(favoriteFoodId, userId)
                Log.e("데이터 확인해보자", createFoodFromBookmark.toString())
                application.apiService.createFoodFromBookmark(createFoodFromBookmark)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "등록되었습니다.", Toast.LENGTH_SHORT).show()
                    setHomeFragment()
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error during createFoodFromBookmark API call", e)
            }
        }
    }

    fun deleteBookmark(favoriteFoodId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                application.apiService.deleteBookmark(favoriteFoodId, userId)
            } catch (e: Exception) {
                Log.e("MainActivity", "Error during deleteBookmark API call", e)
            }
        }
    }

    fun editBookmark(updateFavoriteFoodDto: UpdateFavoriteFoodDto) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.e("너는 뭐냐", updateFavoriteFoodDto.toString())
                application.apiService.updateBookmark(updateFavoriteFoodDto)
            } catch (e: Exception) {
                Log.e("MainActivity", "Error during updateBookmark API call", e)
            }
        }
    }

    suspend fun getSimpleUserInfo(): com.example.foodfood.api.userInfoSimple.Data? {
        return withContext(Dispatchers.Main) {
            try {
                val response = application.apiService.getSimpleUserInfo(userId)
                Log.e("무ㅏ냐고 ", response.data.toString())

                return@withContext response.data
            } catch (e: Exception) {
                Log.e("MainActivity", "Error during getSimpleUserInfo API call", e)
                return@withContext null
            }
        }
    }

    suspend fun getUserInfo(): UserInfo? {
        return withContext(Dispatchers.Main) {
            try {
                val response = application.apiService.getUserInfo(userId)
                Log.e("무ㅏㅓㅇ;ㅁ워밍뭐ㅣㅁ", response.data.toString())
                return@withContext response
            } catch (e: Exception) {
                Log.e("MainActivity", "Error during getUserInfo API call", e)
                return@withContext null
            }
        }
    }

    fun updateUserInfo(updateUserDto: UpdateUserDto) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                application.apiService.updateUserInfo(updateUserDto)
            } catch (e: Exception) {
                Log.e("MainActivity", "Error during updateUserInfo API call", e)
            }
        }
    }

    suspend fun getUserStandardIntake(): UserStandardIntake? {
        return withContext(Dispatchers.Main) {
            try {
                val response = application.apiService.getUserStandardIntake(userId)
                Log.e("너는 뭐냐", response.data.toString())
                return@withContext response
            } catch (e: Exception) {
                Log.e("MainActivity", "Error during getUserStandardIntake API call", e)
                return@withContext null
            }
        }
    }

    fun updateUserStandardIntake(updateUserStandardIntake: UpdateUserStandardIntake) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                application.apiService.updateUserStandardIntake(updateUserStandardIntake)
            } catch (e: Exception) {
                Log.e("MainActivity", "Error during updateUserStandardIntake API call", e)
            }
        }
    }

    suspend fun searchUser(searchUser: SearchUser): SearchUserResponse? {
        return withContext(Dispatchers.Main) {
            try {
                val response = application.apiService.searchUser(searchUser)
                Log.e("정체가 뭐냐", response.data.toString())
                return@withContext response
            } catch (e: Exception) {
                Log.e("MainActivity", "Error during searchUser API call", e)
                return@withContext null
            }
        }
    }

    fun followUser(followId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.e("뭐가 문제냐", userId.toString())
                Log.e("뭐가 문제냐", followId.toString())
                application.apiService.followUser(userId, followId)
            } catch (e: Exception) {
                Log.e("MainActivity", "Error during followUser API call", e)
            }
        }
    }

    fun unfollowUser(followId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.e("뭐가 문제냐", userId.toString())
                Log.e("뭐가 문제냐", followId.toString())
                application.apiService.unfollowUser(userId, followId)
            } catch (e: Exception) {
                Log.e("MainActivity", "Error during unfollowUser API call", e)
            }
        }
    }

    suspend fun getWeeklyRankingData(): List<com.example.foodfood.api.weeklyRank.Data>? {
        return withContext(Dispatchers.Main) {
            try {
                val currentDate = LocalDate.now()
                val year = currentDate.year
                val month = currentDate.monthValue
                val day = currentDate.dayOfMonth
                val response = application.apiService.getWeeklyRank(userId, day, month, year)
                Log.e("너느 뭐임???", response.data.toString())
                return@withContext response.data
            } catch (e: Exception) {
                Log.e("MainActivity", "Error during getWeeklyRank API call", e)
                return@withContext null
            }
        }
    }
}