package com.example.foodfood

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
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
import com.example.foodfood.loading.DialogLoading
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
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var bookmarkList: List<Data>
    private lateinit var progressDialog: DialogLoading
    private var accessToken = ""

    private val CAMERA_PERMISSION_REQUEST_CODE = 100
    private val CAMERA_REQUEST_CODE = 123   //카메라 액티비티 실행 후 결과 처리를 위한 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        application = applicationContext as MyApplication
        sharedPreferences = application.getSharedPrefs()
        viewModel = ViewModelProvider(this).get(NutrientsViewModel::class.java)
        navigation = binding.navigationFrame
        camera = binding.btnCamera
        userId = sharedPreferences.getLong("id", -1L)
        accessToken = sharedPreferences.getString("accessToken", "")!!
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

//        binding.temp.setOnClickListener {
//            val intent = Intent(this, FoodlensActivity::class.java)
//            startActivity(intent)
//        }
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

                val response = application.apiService.getBestWorstFood(accessToken, userId, day, month, year)
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

        if (fragment is AnalysisResultFragment) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.mainFrame, fragment, tag)
                .commit()
        } else {
            supportFragmentManager.beginTransaction()
                .replace(R.id.mainFrame, fragment, tag)
                .addToBackStack(null) // 이전 프래그먼트를 백스택에 추가
                .commit()
        }
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
        } else if (currentFragment is AnalysisResultFragment) {
            navigation.visibility = View.VISIBLE
            camera.visibility = View.VISIBLE
            setHomeFragment()
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
                val foodName = result.data?.getStringExtra("foodName")
                val stringUri = result.data?.getStringExtra("uri")
                val uri = Uri.parse(stringUri)

                Log.e("음식 이름 전달 확인", foodName.toString())

                val fragment = AnalysisResultFragment(foodName, uri)

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
        showProgressDialog()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.e("ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ", food.toString())
                application.apiService.createFood(accessToken, food)
            } catch (e: Exception) {
                Log.e("MainActivity", "Error during createFood API call", e)
            } finally {
                progressDialog.dismiss()
            }
        }

        runOnUiThread {
            Toast.makeText(this, "음식이 추가되었습니다.", Toast.LENGTH_SHORT).show()
            setHomeFragment()
        }
    }

    fun deleteFood(foodId: Long, yy: Int, mm: Int, dd: Int) {
        showProgressDialog()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.e("뭐가 문젠데ㅔㅔㅔㅔㅔ", foodId.toString())
                Log.e("뭐가 문젠데ㅔㅔㅔㅔㅔ", userId.toString())
                application.apiService.deleteFood(accessToken, foodId, userId, dd, mm, yy)
            } catch (e: Exception) {
                Log.e("MainActivity", "Error during deleteFood API call", e)
            } finally {
                progressDialog.dismiss()
            }
        }
    }

    fun editFood(newFood: EditFood) {
        showProgressDialog()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val currentDate = LocalDate.now()
                val year = currentDate.year
                val month = currentDate.monthValue
                val day = currentDate.dayOfMonth

                application.apiService.editFood(accessToken, newFood, day, month, year)
            } catch (e: Exception) {
                Log.e("MainActivity", "Error during editFood API call", e)
            } finally {
                progressDialog.dismiss()
            }
        }
    }

    fun addBookmark(food: AddBookmark) {
        showProgressDialog()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                application.apiService.addBookmark(accessToken, food)
                Log.e("뭐야ㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏ", food.toString())
            } catch (e: Exception) {
                Log.e("MainActivity", "Error during addBookmark API call", e)
            } finally {
                progressDialog.dismiss()
            }
        }
    }

    suspend fun getBookmarkList(): List<Data>? {
        showProgressDialog()
        return withContext(Dispatchers.Main) {
            try {
                val response = application.apiService.getFavoriteFood(accessToken, userId)
                Log.e("ㅓㅓㅓㅓㅓㅓㅓㅓㅓㅓㅓㅓㅓㅓㅓㅓ", response.data.toString())
                return@withContext response.data
            } catch (e: Exception) {
                Log.e("MainActivity", "Error during getFavoriteFood API call", e)
                return@withContext null
            } finally {
                progressDialog.dismiss()
            }
        }
    }

    fun createFoodFromBookmark(favoriteFoodId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val createFoodFromBookmark = CreateFoodFromBookmark(favoriteFoodId, userId)
                Log.e("데이터 확인해보자", createFoodFromBookmark.toString())
                application.apiService.createFoodFromBookmark(accessToken, createFoodFromBookmark)

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
                application.apiService.deleteBookmark(accessToken, favoriteFoodId, userId)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error during deleteBookmark API call", e)
            }
        }
    }

    fun editBookmark(updateFavoriteFoodDto: UpdateFavoriteFoodDto) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.e("너는 뭐냐", updateFavoriteFoodDto.toString())
                application.apiService.updateBookmark(accessToken, updateFavoriteFoodDto)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "수정되었습니다.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error during updateBookmark API call", e)
            }
        }
    }

    suspend fun getSimpleUserInfo(): com.example.foodfood.api.userInfoSimple.Data? {
        showProgressDialog()
        return withContext(Dispatchers.Main) {
            try {
                val response = application.apiService.getSimpleUserInfo(accessToken, userId)
                Log.e("무ㅏ냐고 ", response.data.toString())

                return@withContext response.data
            } catch (e: Exception) {
                Log.e("MainActivity", "Error during getSimpleUserInfo API call", e)
                return@withContext null
            } finally {
                progressDialog.dismiss()
            }
        }
    }

    suspend fun getUserInfo(): UserInfo? {
        showProgressDialog()
        return withContext(Dispatchers.Main) {
            try {
                val response = application.apiService.getUserInfo(accessToken, userId)
                Log.e("무ㅏㅓㅇ;ㅁ워밍뭐ㅣㅁ", response.data.toString())
                return@withContext response
            } catch (e: Exception) {
                Log.e("MainActivity", "Error during getUserInfo API call", e)
                return@withContext null
            } finally {
                progressDialog.dismiss()
            }
        }
    }

    fun updateUserInfo(updateUserDto: UpdateUserDto) {
        showProgressDialog()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                application.apiService.updateUserInfo(accessToken, updateUserDto)
                Log.e("뭐가 문제냐ㅑㅑㅑㅑㅑ", updateUserDto.toString())
            } catch (e: Exception) {
                Log.e("MainActivity", "Error during updateUserInfo API call", e)
            } finally {
                progressDialog.dismiss()
            }
        }
    }

    suspend fun getUserStandardIntake(): UserStandardIntake? {
        showProgressDialog()
        return withContext(Dispatchers.Main) {
            try {
                val response = application.apiService.getUserStandardIntake(accessToken, userId)
                Log.e("너는 뭐냐", response.data.toString())
                return@withContext response
            } catch (e: Exception) {
                Log.e("MainActivity", "Error during getUserStandardIntake API call", e)
                return@withContext null
            } finally {
                progressDialog.dismiss()
            }
        }
    }

    fun updateUserStandardIntake(updateUserStandardIntake: UpdateUserStandardIntake) {
        showProgressDialog()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                application.apiService.updateUserStandardIntake(accessToken, updateUserStandardIntake)
            } catch (e: Exception) {
                Log.e("MainActivity", "Error during updateUserStandardIntake API call", e)
            } finally {
                progressDialog.dismiss()
            }
        }
    }

    suspend fun searchUser(searchUser: SearchUser): SearchUserResponse? {
        showProgressDialog()
        return withContext(Dispatchers.Main) {
            try {
                val response = application.apiService.searchUser(accessToken, searchUser)
                Log.e("정체가 뭐냐", response.data.toString())
                return@withContext response
            } catch (e: Exception) {
                Log.e("MainActivity", "Error during searchUser API call", e)
                return@withContext null
            } finally {
                progressDialog.dismiss()
            }
        }
    }

    fun followUser(followId: Long) {
        showProgressDialog()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.e("뭐가 문제냐", userId.toString())
                Log.e("뭐가 문제냐", followId.toString())
                application.apiService.followUser(accessToken, userId, followId)
            } catch (e: Exception) {
                Log.e("MainActivity", "Error during followUser API call", e)
            } finally {
                progressDialog.dismiss()
            }
        }
    }

    fun unfollowUser(followId: Long) {
        showProgressDialog()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.e("뭐가 문제냐", userId.toString())
                Log.e("뭐가 문제냐", followId.toString())
                application.apiService.unfollowUser(accessToken, userId, followId)
            } catch (e: Exception) {
                Log.e("MainActivity", "Error during unfollowUser API call", e)
            } finally {
                progressDialog.dismiss()
            }
        }
    }

    suspend fun getWeeklyRankingData(): List<com.example.foodfood.api.weeklyRank.Data>? {
        showProgressDialog()
        return withContext(Dispatchers.Main) {
            try {
                val currentDate = LocalDate.now()
                val year = currentDate.year
                val month = currentDate.monthValue
                val day = currentDate.dayOfMonth
                val response = application.apiService.getWeeklyRank(accessToken, userId, day, month, year)
                Log.e("너느 뭐임???", response.data.toString())
                return@withContext response.data
            } catch (e: Exception) {
                Log.e("MainActivity", "Error during getWeeklyRank API call", e)
                return@withContext null
            } finally {
                progressDialog.dismiss()
            }
        }
    }

    private fun showProgressDialog() {
        progressDialog = DialogLoading(this)
        progressDialog.show()
    }
}