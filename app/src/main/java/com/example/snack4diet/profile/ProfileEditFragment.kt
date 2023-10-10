package com.example.snack4diet.profile

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.snack4diet.MainActivity
import com.example.snack4diet.R
import com.example.snack4diet.api.UserInfo
import com.example.snack4diet.databinding.AddDiaryDialogLayoutBinding
import com.example.snack4diet.databinding.DialogProfileEditExceptionBinding
import com.example.snack4diet.databinding.FragmentProfileEditBinding
import com.example.snack4diet.viewModel.NutrientsViewModel

class ProfileEditFragment : Fragment() {
    private lateinit var binding: FragmentProfileEditBinding
    private lateinit var viewModel: NutrientsViewModel
    private lateinit var user: UserInfo

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileEditBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mainActivity = requireActivity() as MainActivity
        viewModel = mainActivity.getViewModel()
        user = viewModel.getUser()

        binding.nickname.hint = user.nickname
        binding.age.hint = user.age.toString()
        binding.height.hint = user.height.toInt().toString()
        binding.weight.hint = user.weight.toInt().toString()

        binding.btnFinishEdit.setOnClickListener {
            var newName = binding.nickname.text.toString()
            var newAge = binding.age.text.toString()
            var newHeight = binding.height.text.toString()
            var newWeight = binding.weight.text.toString()

            if (newName.isNullOrEmpty()) newName = user.nickname
            if (newAge.isNullOrEmpty()) newAge = user.age.toString()
            if (newHeight.isNullOrEmpty()) newHeight = user.height.toString()
            if (newWeight.isNullOrEmpty()) newWeight = user.weight.toString()

            if (newHeight.toInt() > 300 || newAge.toInt() > 120 || newWeight.toInt() > 200) {
                showProfileEditExceptionDialog()
            } else {
                viewModel.editUser(newName, newHeight.toDouble(), newWeight.toDouble(), newAge.toInt())
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