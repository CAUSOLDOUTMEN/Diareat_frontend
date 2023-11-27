package com.example.foodfood.profile

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.foodfood.MainActivity
import com.example.foodfood.R
import com.example.foodfood.api.UserInfo
import com.example.foodfood.api.userInfoSimple.Data
import com.example.foodfood.databinding.FragmentProfileBinding
import com.example.foodfood.viewModel.NutrientsViewModel
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var user: UserInfo
    private lateinit var viewModel: NutrientsViewModel
    private lateinit var mainActivity: MainActivity
    private var userData: Data? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainActivity = requireActivity() as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater, container,false)

        lifecycleScope.launch {
            userData = mainActivity.getSimpleUserInfo()
            binding.nickname.text = userData?.name
            Glide.with(requireContext())
                .load(userData?.image)
                .placeholder(R.drawable.ic_profile_image)
                .circleCrop()
                .into(binding.profileImage)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editProfile.setOnClickListener {
            setProfileEditFragment()
        }

        binding.editDailyNutrient.setOnClickListener {
            setNutrientProfileEditFragment()
        }
    }

    private fun setProfileEditFragment() {
        val fragment = ProfileEditFragment()
        mainActivity.replaceFragment(fragment, "ProfileEditFragment")
    }

    private fun setNutrientProfileEditFragment() {
        val fragment = NutrientProfileEditFragment()
        mainActivity.replaceFragment(fragment, "NutrientProfileEditFragment")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallBack)
    }

    private val onBackPressedCallBack = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            mainActivity.onBackPressed()
        }
    }
}