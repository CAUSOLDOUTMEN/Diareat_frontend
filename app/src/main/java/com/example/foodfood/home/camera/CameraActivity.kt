package com.example.foodfood.home.camera

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.graphics.Rect
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.doinglab.foodlens.sdk.FoodLens
import com.doinglab.foodlens.sdk.NetworkService
import com.doinglab.foodlens.sdk.NutritionResultHandler
import com.doinglab.foodlens.sdk.RecognizeResultHandler
import com.doinglab.foodlens.sdk.errors.BaseError
import com.doinglab.foodlens.sdk.network.model.Food
import com.doinglab.foodlens.sdk.network.model.NutritionResult
import com.doinglab.foodlens.sdk.network.model.RecognitionResult
import com.example.foodfood.R
import com.example.foodfood.application.MyApplication
import com.example.foodfood.databinding.ActivityCameraBinding
import com.example.foodfood.loading.DialogLoading
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class CameraActivity : AppCompatActivity() {
    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private var savedUri: Uri? = null
    private lateinit var previewView: PreviewView
    private lateinit var binding: ActivityCameraBinding
    private lateinit var app: MyApplication
    private lateinit var ns: NetworkService
    private lateinit var progressDialog: DialogLoading
    private lateinit var graphicOverlay: GraphicOverlay
    private var btnIngredientChecked = false
    private var btnFoodChecked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ns = FoodLens.createNetworkService(this)

        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = applicationContext as MyApplication
        previewView = binding.previewView
        cameraExecutor = Executors.newSingleThreadExecutor()
        graphicOverlay = GraphicOverlay(this, null)

        btnIngredientChecked = true

        binding.btnIngredientPhoto.setOnClickListener {
            ingredientPressed()
        }

        binding.btnFoodPhoto.setOnClickListener {
            foodPressed()
        }

        binding.imageViewPhoto.setOnClickListener {
            savePhoto()
        }

        outputDirectory = getOutputDirectory()
        openCamera()
    }

    private fun ingredientPressed() {
        btnIngredientChecked = true
        btnFoodChecked = false
        binding.btnFoodPhoto.setImageResource(R.drawable.ic_btn_food_photo)
        binding.btnIngredientPhoto.setImageResource(R.drawable.ic_btn_ingredient_photo_pressed)
    }

    private fun foodPressed() {
        btnIngredientChecked = false
        btnFoodChecked = true
        binding.btnFoodPhoto.setImageResource(R.drawable.ic_btn_food_photo_pressed)
        binding.btnIngredientPhoto.setImageResource(R.drawable.ic_btn_ingredient_photo)
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    private fun openCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            binding.parentView.addView(graphicOverlay)

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            val camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            val cameraControl = camera.cameraControl

            if (imageCapture != null) {
                cameraControl.setZoomRatio(0.05f)

                if (binding.btnIngredientPhoto.isPressed) {
                    val image = imageCapture!!.takePicture(cameraExecutor, object : ImageCapture.OnImageCapturedCallback() {
                        override fun onCaptureSuccess(image: ImageProxy) {
                            image.close()
                        }

                        override fun onError(imageCaptureError: ImageCaptureException) {
                            // 오류 처리
                        }
                    })
                } else if (binding.btnFoodPhoto.isPressed) {
                    val image = imageCapture!!.takePicture(cameraExecutor, object : ImageCapture.OnImageCapturedCallback() {
                        override fun onCaptureSuccess(image: ImageProxy) {
                            image.close()
                        }

                        override fun onError(imageCaptureError: ImageCaptureException) {
                            // 오류 처리
                        }
                    })
                }
            }
        }, ContextCompat.getMainExecutor(this))
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun savePhoto() {
        imageCapture = imageCapture ?: return

        val photoFile = File(
            outputDirectory,
            SimpleDateFormat("yy-mm-dd", Locale.US).format(System.currentTimeMillis()) + ".jpeg"
        )
        val outputOption = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture?.takePicture(
            outputOption,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    savedUri = Uri.fromFile(photoFile)

                    if (savedUri != null) {
                        if (btnIngredientChecked) {
                            showProgressDialog()
                            val bitmap = uriToBitmap(this@CameraActivity, savedUri)
                            val rotatedBitmap = rotateImageIfRequired(this@CameraActivity, bitmap!!, savedUri!!)
                            val croppedBitmap = processCroppedImage(rotatedBitmap)
                            val croppedUri = bitmapToUri(this@CameraActivity, croppedBitmap!!)

                            CoroutineScope(Dispatchers.IO).launch {

                                val file = File(croppedUri!!.path!!)
                                val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
                                val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

                                try {
                                    val startTime = System.currentTimeMillis()
                                    val response = app.ocrService.sendFile(body)
                                    Log.e("제발제발제발제발제발제발", response.toString())

                                    val endTime = System.currentTimeMillis()
                                    val elapsedTime = endTime - startTime

                                    Log.e("작업 시간", "$elapsedTime 밀리초")

                                    var kcal = response.kcal.toInt()
                                    var carbohydrate = response.carbohydrate.toInt()
                                    var protein = response.protein.toInt()
                                    var fat = response.fat.toInt()

                                    if (kcal == -1) kcal = 0
                                    if (carbohydrate == -1) carbohydrate = 0
                                    if (protein == -1) protein = 0
                                    if (fat == -1) fat = 0

                                    val baseNutrition = com.example.foodfood.api.createFood.BaseNutrition(carbohydrate, fat, kcal, protein)
                                    app.baseNutrition = baseNutrition
                                    val resultIntent = Intent()
                                    resultIntent.putExtra("uri", croppedUri.toString())
                                    setResult(Activity.RESULT_OK, resultIntent)
                                    finish()
                                } catch (e: Exception) {
                                    Log.e("CameraActivity", "Error during uploadImage API call", e)
                                } finally {
                                    progressDialog.dismiss()
                                }
                            }
                        } else if (btnFoodChecked){
                            showProgressDialog()
                            val bitmap = uriToBitmap(this@CameraActivity, savedUri!!)
                            val rotatedBitmap = rotateImageIfRequired(this@CameraActivity, bitmap!!, savedUri!!)
                            val croppedBitmap = processCroppedImage(rotatedBitmap)
                            val croppedUri = bitmapToUri(this@CameraActivity, croppedBitmap!!)

                            ns.predictMultipleFood(croppedBitmap, object : RecognizeResultHandler {
                                override fun onSuccess(result: RecognitionResult) {
                                    val foodPosList = result.foodPositions
                                    for (fp in foodPosList) {
                                        val foods: List<Food> = fp.foods
                                        val food = foods[0]
                                        val name = food.getFoodName()
                                        ns.getNutritionInfo(food.foodId, object: NutritionResultHandler {
                                            override fun onSuccess(result: NutritionResult?) {
                                                var kcal = result?.getNutrition()?.getCalories()?.toInt()!!
                                                var carbohydrate = result?.getNutrition()?.getCarbonHydrate()?.toInt()!!
                                                var protein = result?.getNutrition()?.getProtein()?.toInt()!!
                                                var fat = result?.getNutrition()?.getFat()?.toInt()!!

                                                val baseNutrition = com.example.foodfood.api.createFood.BaseNutrition(carbohydrate, fat, kcal, protein)
                                                app.baseNutrition = baseNutrition
                                                val resultIntent = Intent()
                                                resultIntent.putExtra("foodName", name)
                                                resultIntent.putExtra("uri", croppedUri.toString())
                                                setResult(Activity.RESULT_OK, resultIntent)
                                                finish()

                                                Log.i("Foodlens", name)
                                                Log.i("FoodLens", String.format("Calorie : %f", result?.getNutrition()?.getCalories()))
                                                Log.i("FoodLens", String.format("Carbohydrate : %f", result?.getNutrition()?.getCarbonHydrate()))
                                                Log.i("FoodLens", String.format("Protein : %f", result?.getNutrition()?.getProtein()))
                                                Log.i("FoodLens", String.format("Fat : %f", result?.getNutrition()?.getFat()))
                                            }

                                            override fun onError(errorReason: BaseError?) {
                                                Log.e("FoodLens", errorReason!!.getMessage());
                                                progressDialog.dismiss()
                                            }
                                        })
                                    }
                                }

                                override fun onError(errorReason: BaseError) {
                                    Log.e("FoodLens", errorReason.message)
                                    progressDialog.dismiss()
                                }
                            })
                            progressDialog.dismiss()
                        }
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    exception.printStackTrace()
                    onBackPressed()
                }

            }
        )
    }

    fun uriToBitmap(context: Context, uri: Uri?): Bitmap? {
        if (uri == null) {
            return null
        }

        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun bitmapToUri(context: Context, bitmap: Bitmap): Uri? {
        val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val image = File(imagesDir, "image.jpg")

        val contentResolver: ContentResolver = context.contentResolver
        var outputStream: OutputStream? = null

        try {
            outputStream = FileOutputStream(image)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()

            // 파일이 저장된 후, 파일의 Uri를 가져옴
            return Uri.fromFile(image)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                outputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        return null
    }

    private fun showProgressDialog() {
        progressDialog = DialogLoading(this)
        progressDialog.show()
    }

    private fun cropImage(originalBitmap: Bitmap, guideLineRect: Rect): Bitmap {
        Log.e("GuideLineRect", "Left: ${guideLineRect.left}, Top: ${guideLineRect.top}, Right: ${guideLineRect.right}, Bottom: ${guideLineRect.bottom}")
        Log.e("OriginalBitmap Size", "${originalBitmap.width} x ${originalBitmap.height}")

        val width = originalBitmap.width
        val height = originalBitmap.height
        val x = width / 10
        val y = height / 40

        val croppedBitmap = Bitmap.createBitmap(
            originalBitmap,
            x,
            y,
            (width * 8) / 10,
            (height * 3) / 4,
        )
        Log.e("CroppedBitmap Size", "${croppedBitmap.width} x ${croppedBitmap.height}")
        return croppedBitmap
    }

    private fun processCroppedImage(originalBitmap: Bitmap): Bitmap {
        val guideLineRect = graphicOverlay.getGuideLineRect()
        val croppedImage = cropImage(originalBitmap, guideLineRect)

        binding.frameLayoutPreview.visibility = View.VISIBLE
        binding.imageViewPreview.setImageBitmap(croppedImage)
        binding.parentView.removeView(graphicOverlay)

        return croppedImage
    }

    private fun rotateImageIfRequired(context: Context, bitmap: Bitmap, uri: Uri): Bitmap {
        val inputStream = context.contentResolver.openInputStream(uri)
        val ei: ExifInterface
        try {
            ei = ExifInterface(inputStream!!)
        } catch (e: IOException) {
            e.printStackTrace()
            return bitmap
        } finally {
            try {
                inputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        return rotateBitmap(bitmap, orientation)
    }

    private fun rotateBitmap(bitmap: Bitmap, orientation: Int): Bitmap {
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(270f)
            else -> return bitmap
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}