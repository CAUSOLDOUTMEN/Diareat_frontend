package com.example.foodfood.home.camera.foodlens

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.doinglab.foodlens.sdk.*
import com.doinglab.foodlens.sdk.errors.BaseError
import com.doinglab.foodlens.sdk.network.model.RecognitionResult
import com.doinglab.foodlens.sdk.network.model.UserSelectedResult
import com.example.foodfood.databinding.ActivityFoodlensBinding
import java.io.ByteArrayOutputStream

class FoodlensActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFoodlensBinding

    private val uiService by lazy {
        FoodLens.createUIService(this)
    }
    private val ns by lazy {
        FoodLens.createNetworkService(applicationContext)
    }
    private val tv_title by lazy {
        binding.tvTitle
    }
    private val btn_run_foodlens by lazy {
        binding.btnRunFoodlens
    }
    private val btn_camera by lazy {
        binding.btnRunCamera
    }
    private val listview by lazy {
        binding.listview
    }
    private val adapter by lazy {
        ListViewAdapter()
    }

    var recognitionResult: RecognitionResult? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFoodlensBinding.inflate(layoutInflater)
        setContentView(binding.root)

        uiService.setUiServiceMode(UIServiceMode.USER_SELECTED_WITH_CANDIDATES)
        //uiService onActivityResult 호출 필요 여부. default는 true
        uiService.setUseActivityResult(false)

        try {
            val bundle = FoodLensBundle()
            bundle.isEnableManualInput = true;  //검색입력 활성화 여부
            bundle.isSaveToGallery = false;      //갤러리 기능 활성화 여부
            uiService.setDataBundle(bundle)
        } catch (e: java.lang.Exception) {

        }

        btn_camera.setOnClickListener {
            openPicture()
        }

        btn_run_foodlens.setOnClickListener {
            val companyToken = getMetaData("com.doinglab.foodlens.sdk.companytoken")
            val appToken = getMetaData("com.doinglab.foodlens.sdk.apptoken")

            Log.d("YourActivity", "Company Token: $companyToken")
            Log.d("YourActivity", "App Token: $appToken")
            uiService.startFoodLensCamera(this, object : UIServiceResultHandler {
                override fun onSuccess(result: UserSelectedResult) {
                    recognitionResult = result
                    tv_title?.text = "Result from UI Service"
                    recognitionResult?.let {
                        setRecognitionResultData(it)
                    }
                }

                override fun onCancel() {
                    Log.d("FOODLENS_LOG", "Recognition Cancel")
                    Toast.makeText(applicationContext, "Recognition Cancel", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onError(error: BaseError) {
                    Log.e("FOODLENS_LOG", error.message)
                    Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
                }
            })
        }

        binding.btnRunEditmode.setOnClickListener {
            uiService.startFoodLensDataEdit(
                this,
                recognitionResult,
                object : UIServiceResultHandler {
                    override fun onSuccess(result: UserSelectedResult) {
                        recognitionResult = result
                        recognitionResult?.let {
                            setRecognitionResultData(it)
                        }
                    }

                    override fun onCancel() {
                        Log.d("FOODLENS_LOG", "Recognition Cancel")
                        Toast.makeText(applicationContext, "Recognition Cancel", Toast.LENGTH_SHORT)
                            .show()
                    }

                    override fun onError(error: BaseError) {
                        Log.e("FOODLENS_LOG", error.message)
                        Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    private fun openPicture() {
        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        pictureResultLauncher.launch(intent)
    }

    private var pictureResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.data?.let { uri ->
                    var bitmap = uri.parseBitmap(this)

                    val baos = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos)

                    ns.setNutritionRetrieveMode(NutritionRetrieveMode.TOP1_NUTRITION_ONLY)
                    ns.setLanguageConfig(LanguageConfig.KO)
                    ns.predictMultipleFood(baos.toByteArray(), object : RecognizeResultHandler {
                        override fun onSuccess(result: RecognitionResult?) {
                            result?.let {
                                recognitionResult = result
                                tv_title!!.text = "Result from Network Service"
                                setRecognitionResultData(result)
                            }
                        }
                        override fun onError(errorReason: BaseError?) {
                            errorReason?.message?.let { Log.e("FOODLENS_LOG", it) }
                        }
                    })
                }
            }
        }

    private fun setRecognitionResultData(recognitionResultData: RecognitionResult) {
        listview!!.adapter = null
        adapter!!.clearItems()
        val foodPositions = recognitionResultData.foodPositions
        var foodName = ""
        var foodNutritionInfo = ""
        var foodLocation = ""
        for (i in foodPositions.indices) {
            foodName = ""
            foodNutritionInfo = ""
            foodLocation = ""
            val foodPosition = foodPositions[i]
            val foodList = foodPosition.foods
            val amount = foodPosition.eatAmount
            foodName = foodList[0].foodName
            val nutrition = foodList[0].nutrition
            if (nutrition != null) {
                val carbon = "탄수화물: " + nutrition.carbonHydrate
                val protein = "단백질: " + nutrition.protein
                val fat = "지방: " + nutrition.fat
                val foodType = "타입: " + nutrition.foodType
                foodNutritionInfo += "$carbon $protein $fat $foodType"
            }
            val bitmap = BitmapFactory.decodeFile(foodPosition.foodImagePath)
            val drawable: Drawable = BitmapDrawable(resources, bitmap)

            val box = foodPosition.imagePosition
            if (box != null) {
                foodLocation = "음식 위치: "
                foodLocation += box.xmin.toString() + " " + box.xmax.toString() + " " + box.ymin.toString() + " " + box.ymax.toString()
                foodLocation += " 음식양 : $amount"
            }
            adapter!!.addItem(drawable, foodName, foodNutritionInfo, foodLocation)
        }
        listview!!.adapter = adapter
    }


    private fun Uri.parseBitmap(context: Context): Bitmap {
        return when (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            true -> {
                val source = ImageDecoder.createSource(context.contentResolver, this)
                ImageDecoder.decodeBitmap(source)
            }
            else -> {
                MediaStore.Images.Media.getBitmap(context.contentResolver, this)
            }
        }
    }

    private fun getMetaData(name: String): String {
        try {
            val appInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            val metaData = appInfo.metaData
            return metaData?.getString(name) ?: ""
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return ""
    }
}