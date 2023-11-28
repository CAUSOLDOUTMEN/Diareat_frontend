package com.example.foodfood.profile

import android.app.Dialog
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.foodfood.MainActivity
import com.example.foodfood.R
import com.example.foodfood.api.updateUser.UpdateUserDto
import com.example.foodfood.databinding.DialogProfileEditExceptionBinding
import com.example.foodfood.databinding.FragmentProfileEditBinding
import kotlinx.coroutines.launch

class ProfileEditFragment : Fragment() {
    private lateinit var binding: FragmentProfileEditBinding
    private lateinit var mainActivity: MainActivity
    private var userInfo: com.example.foodfood.api.userInfo.UserInfo? = null
    private var userId = -1L
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainActivity = requireActivity() as MainActivity
        sharedPreferences = mainActivity.sharedPreferences
        userId = mainActivity.getUserId()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileEditBinding.inflate(layoutInflater, container, false)

        lifecycleScope.launch{
            userInfo = mainActivity.getUserInfo()
            binding.nickname.hint = userInfo?.data?.name
            binding.age.hint = userInfo?.data?.age.toString()
            binding.height.hint = userInfo?.data?.height.toString()
            binding.weight.hint = userInfo?.data?.weight.toString()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnFinishEdit.setOnClickListener {
            var newName = binding.nickname.text.toString()
            var newAge = binding.age.text.toString()
            var newHeight = binding.height.text.toString()
            var newWeight = binding.weight.text.toString()
            var isChecked = binding.autoUpdateNutrition.isChecked
            val autoUpdateNutrition = if (isChecked) 1 else 0
            sharedPreferences.edit().putInt("autoUpdateNutrition", autoUpdateNutrition).apply()

            if (newName.isEmpty()) newName = binding.nickname.hint.toString()
            if (newAge.isEmpty()) newAge = binding.age.hint.toString()
            if (newHeight.isEmpty()) newHeight = binding.height.hint.toString()
            if (newWeight.isEmpty()) newWeight = binding.weight.hint.toString()

            val parsedAge = newAge.toIntOrNull()
            val parsedHeight = newHeight.toIntOrNull()
            val parsedWeight = newWeight.toIntOrNull()

            if (parsedHeight == null || parsedAge == null || parsedWeight == null ||
                parsedHeight.toInt() > 300.0 || parsedAge.toInt() > 120 || parsedWeight.toInt() > 200.0) {
                showProfileEditExceptionDialog()
            } else {
                val newUserInfo = UpdateUserDto(
                     parsedAge, parsedHeight, newName, userId, parsedWeight, autoUpdateNutrition
                )
                mainActivity.updateUserInfo(newUserInfo)
                mainActivity.popFragment()
                Toast.makeText(requireContext(),"수정이 완료되었습니다.",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showProfileEditExceptionDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_profile_edit_exception)

        val dialogBinding = DialogProfileEditExceptionBinding.bind(dialog.findViewById(R.id.profileEditExceptionLayout))

        dialogBinding.btnOkay.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

        val window = dialog.window
        window?.setBackgroundDrawableResource(R.drawable.round_frame_white_20)
    }
}