package com.example.snack4diet.home.camera

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.snack4diet.MainActivity
import com.example.snack4diet.R
import com.example.snack4diet.api.createFood.BaseNutrition
import com.example.snack4diet.api.createFood.CreateFood
import com.example.snack4diet.application.MyApplication
import com.example.snack4diet.databinding.DialogDeleteBookmarkBinding
import com.example.snack4diet.databinding.FragmentAnalysisResultBinding
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

class AnalysisResultFragment : Fragment() {
    private lateinit var binding: FragmentAnalysisResultBinding
    private lateinit var mainActivity: MainActivity
    private lateinit var application: MyApplication
    private lateinit var baseNutrition: BaseNutrition
    private var userId = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainActivity = requireActivity() as MainActivity
        application = mainActivity.application
        userId = mainActivity.getUserId()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAnalysisResultBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        baseNutrition = application.baseNutrition

        val currentTime = LocalDateTime.now()
        val hour = currentTime.hour
        val minute = currentTime.minute

        binding.foodName.setText(hour.toString() + "시 " + minute.toString() + "분의 음식")
        binding.editKcalAmount.setText(baseNutrition.kcal.toString())
        binding.editCarbohydrateAmount.setText(baseNutrition.carbohydrate.toString())
        binding.editProteinAmount.setText(baseNutrition.protein.toString())
        binding.editFatAmount.setText(baseNutrition.fat.toString())

        binding.btnFoodSave.setOnClickListener {
            val currentDate = LocalDate.now()
            val year = currentDate.year
            val month = currentDate.monthValue
            val day = currentDate.dayOfMonth
            val name = binding.foodName.text.toString()

            val food = CreateFood(baseNutrition, day, month, name, userId, year)

            Log.e("아 이번에는 또 뭐냐고", food.toString())

            mainActivity.createFood(food)
        }

        binding.btnRetry.setOnClickListener {
            showRetryDialog()
        }
    }

    private fun showRetryDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_retry)

        val dialogBinding = DialogDeleteBookmarkBinding.bind(dialog.findViewById(R.id.retryDialogLayout))

        dialogBinding.btnNo.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnYes.setOnClickListener {
            dialog.dismiss()
            mainActivity.executeCamera()
        }

        dialog.show()

        val window = dialog.window
        window?.setBackgroundDrawableResource(R.drawable.round_frame_white_20)
    }
}