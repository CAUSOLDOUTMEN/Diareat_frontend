package com.example.snack4diet.profile

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.snack4diet.MainActivity
import com.example.snack4diet.R
import com.example.snack4diet.api.UserNutrientInfo
import com.example.snack4diet.api.updateUserStandardIntake.UpdateUserStandardIntake
import com.example.snack4diet.api.userStandardIntake.UserStandardIntake
import com.example.snack4diet.databinding.DialogNutrientProfileEditExceptionBinding
import com.example.snack4diet.databinding.FragmentNutrientProfileEditBinding
import com.example.snack4diet.viewModel.NutrientsViewModel
import kotlinx.coroutines.launch

class NutrientProfileEditFragment : Fragment() {
    private lateinit var binding: FragmentNutrientProfileEditBinding
    private lateinit var mainActivity: MainActivity
    private var userStandardIntake: UserStandardIntake? = null
    private var userId = -1L

    private val textWatcher = object : TextWatcher{
        override fun afterTextChanged(p0: Editable?) {}
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            calculateKcal()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainActivity = requireActivity() as MainActivity
        userId = mainActivity.getUserId()
        lifecycleScope.launch {
            userStandardIntake = mainActivity.getUserStandardIntake()
            binding.kcal.text = userStandardIntake?.data?.calorie.toString()
            binding.carbohydrate.setText(userStandardIntake?.data?.carbohydrate.toString())
            binding.protein.setText(userStandardIntake?.data?.protein.toString())
            binding.fat.setText(userStandardIntake?.data?.fat.toString())

            binding.carbohydrate.addTextChangedListener(textWatcher)
            binding.protein.addTextChangedListener(textWatcher)
            binding.fat.addTextChangedListener(textWatcher)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNutrientProfileEditBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnFinishEdit.setOnClickListener {
            val newKcal = binding.kcal.text.toString()
            val newCarbohydrate = binding.carbohydrate.text.toString()
            val newProtein = binding.protein.text.toString()
            val newFat = binding.fat.text.toString()

//            if (newKcal.isEmpty()) {
//                newKcal = binding.kcal.hint.toString()
//            }
//            if (newCarbohydrate.isEmpty()) {
//                newCarbohydrate = binding.carbohydrate.hint.toString()
//            }
//            if (newProtein.isEmpty()) {
//                newProtein = binding.protein.hint.toString()
//            }
//            if (newFat.isEmpty()) {
//                newFat = binding.fat.hint.toString()
//            }

            val parsedKcal = newKcal.toIntOrNull()
            val parsedCarbohydrate = newCarbohydrate.toIntOrNull()
            val parsedProtein = newProtein.toIntOrNull()
            val parsedFat = newFat.toIntOrNull()

            if (
                parsedKcal != null &&
                parsedCarbohydrate != null &&
                parsedProtein != null &&
                parsedFat != null &&
                parsedKcal.toInt() in 1000..10000 &&
                parsedCarbohydrate.toInt() in 100..500 &&
                parsedProtein.toInt() in 25..500 &&
                parsedFat.toInt() in 25..500
            ) {
                val newStandardIntake = UpdateUserStandardIntake(
                    parsedKcal.toInt(),
                    parsedCarbohydrate.toInt(),
                    parsedFat.toInt(),
                    parsedProtein.toInt(),
                    userId
                )
                mainActivity.updateUserStandardIntake(newStandardIntake)
                mainActivity.popFragment()
                Toast.makeText(requireContext(), "수정이 완료되었습니다.", Toast.LENGTH_SHORT).show()
            } else {
                val dialog = Dialog(requireContext())
                dialog.setContentView(R.layout.dialog_nutrient_profile_edit_exception)

                val dialogBinding = DialogNutrientProfileEditExceptionBinding.bind(dialog.findViewById(R.id.nutrientProfileEditExceptionLayout))

                dialogBinding.btnOkay.setOnClickListener {
                    dialog.dismiss()
                }

                dialog.show()

                val window = dialog.window
                window?.setBackgroundDrawableResource(R.drawable.round_frame_white_20)
            }
        }
    }

    private fun calculateKcal() {
        val carbohydrate = binding.carbohydrate.text.toString().toIntOrNull() ?: 0
        val protein = binding.protein.text.toString().toIntOrNull() ?: 0
        val fat = binding.fat.text.toString().toIntOrNull() ?: 0

        val kcal = carbohydrate * 4 + protein * 4 + fat * 9
        binding.kcal.text = kcal.toString()
    }
}