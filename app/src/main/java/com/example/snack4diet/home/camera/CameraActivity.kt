package com.example.snack4diet.home.camera

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Rational
import android.util.Size
import android.view.View
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.example.snack4diet.R
import com.example.snack4diet.application.MyApplication
import com.example.snack4diet.databinding.ActivityCameraBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = applicationContext as MyApplication
        previewView = binding.previewView
        cameraExecutor = Executors.newSingleThreadExecutor()

        binding.btnIngredientPhoto.isPressed = true

        binding.btnIngredientPhoto.setOnClickListener {
            binding.btnFoodPhoto.isPressed = false
            binding.btnIngredientPhoto.isPressed = true
        }

        binding.btnFoodPhoto.setOnClickListener {
            binding.btnIngredientPhoto.isPressed = false
            binding.btnFoodPhoto.isPressed = true
        }

        binding.imageViewPhoto.setOnClickListener {
            savePhoto()
        }

        outputDirectory = getOutputDirectory()
        openCamera()
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
            SimpleDateFormat("yy-mm-dd", Locale.US).format(System.currentTimeMillis()) + ".png"
        )
        val outputOption = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture?.takePicture(
            outputOption,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    savedUri = Uri.fromFile(photoFile)

                    if (savedUri != null) {
                        CoroutineScope(Dispatchers.IO).launch {
                            val file = File(savedUri!!.path!!)
                            val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
                            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

                            try {
                                val startTime = System.currentTimeMillis()
                                val response = app.ocrService.sendFile(body)
                                Log.e("제발제발제발제발제발제발", response.toString())

                                val endTime = System.currentTimeMillis()
                                val elapsedTime = endTime - startTime

                                Log.e("작업 시간", "$elapsedTime 밀리초")

                                val baseNutrition = com.example.snack4diet.api.createFood.BaseNutrition(response.carbohydrate.toInt(), response.fat.toInt(), response.kcal.toInt(), response.protein.toInt())
                                app.baseNutrition = baseNutrition
                                val resultIntent = Intent()
                                setResult(Activity.RESULT_OK, resultIntent)
                                finish()
                            } catch (e: Exception) {
                                Log.e("CameraActivity", "Error during uploadImage API call", e)
                            }
                        }
                        binding.imageViewPreview.setImageURI(savedUri)
                        binding.frameLayoutPreview.visibility = View.VISIBLE
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    exception.printStackTrace()
                    onBackPressed()
                }

            }
        )
    }
}