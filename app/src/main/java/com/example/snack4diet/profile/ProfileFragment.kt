package com.example.snack4diet.profile

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import com.example.snack4diet.MainActivity
import com.example.snack4diet.R
import com.example.snack4diet.api.UserInfo
import com.example.snack4diet.databinding.FragmentProfileBinding
import com.example.snack4diet.viewModel.NutrientsViewModel

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var user: UserInfo
    private lateinit var viewModel: NutrientsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater, container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mainActivity = requireActivity() as MainActivity
        viewModel = mainActivity.getViewModel()
        user = viewModel.getUser()

        binding.nickname.text = user.nickname

        binding.editProfile.setOnClickListener {
            setProfileEditFragment()
        }

        binding.editDailyNutrient.setOnClickListener {
            setNutrientProfileEditFragment()
        }
    }

    private fun setProfileEditFragment() {
        val mainActivity = requireActivity() as MainActivity
        val fragment = ProfileEditFragment()
        mainActivity.replaceFragment(fragment, "ProfileEditFragment")
    }

    private fun setNutrientProfileEditFragment() {
        val mainActivity = requireActivity() as MainActivity
        val fragment = NutrientProfileEditFragment()
        mainActivity.replaceFragment(fragment, "NutrientProfileEditFragment")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallBack)
    }

    private val onBackPressedCallBack = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val mainActivity = requireActivity() as MainActivity
            mainActivity.onBackPressed()
        }
    }
}