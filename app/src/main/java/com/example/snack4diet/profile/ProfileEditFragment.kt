package com.example.snack4diet.profile

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.snack4diet.MainActivity
import com.example.snack4diet.R
import com.example.snack4diet.api.updateUser.UpdateUserDto
import com.example.snack4diet.databinding.DialogProfileEditExceptionBinding
import com.example.snack4diet.databinding.FragmentProfileEditBinding
import kotlinx.coroutines.launch

class ProfileEditFragment : Fragment() {
    private lateinit var binding: FragmentProfileEditBinding
    private lateinit var mainActivity: MainActivity
    private var userInfo: com.example.snack4diet.api.userInfo.UserInfo? = null
    private var userId = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainActivity = requireActivity() as MainActivity
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

            if (newName.isEmpty()) newName = binding.nickname.hint.toString()
            if (newAge.isEmpty()) newAge = binding.age.hint.toString()
            if (newHeight.isEmpty()) newHeight = binding.height.hint.toString()
            if (newWeight.isEmpty()) newWeight = binding.weight.hint.toString()

            val parsedAge = newAge.toIntOrNull()
            val parsedHeight = newHeight.toDoubleOrNull()
            val parsedWeight = newWeight.toDoubleOrNull()

            if (parsedHeight == null || parsedAge == null || parsedWeight == null ||
                parsedHeight.toDouble() > 300.0 || parsedAge.toInt() > 120 || parsedWeight.toDouble() > 200.0) {
                showProfileEditExceptionDialog()
            } else {
                val newUserInfo = UpdateUserDto(
                     parsedAge, parsedHeight, newName, userId, parsedWeight
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