package com.example.snack4diet.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.snack4diet.MainActivity
import com.example.snack4diet.R
import com.example.snack4diet.api.foodOnDate.Data
import com.example.snack4diet.databinding.FragmentDiaryBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DiaryFragment : Fragment() {
    private lateinit var binding: FragmentDiaryBinding
    private lateinit var diaryAdapter: DiaryAdapter
    private lateinit var foodList: List<Data>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDiaryBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragment = requireParentFragment() as HomeFragment
        foodList = fragment.getFoodList()
        if (foodList.isEmpty()) {
            binding.emptyFoodLayout.visibility = View.VISIBLE
        } else {
            binding.emptyFoodLayout.visibility = View.GONE
        }

        setDiaryRecyclerView()

        binding.btnFoodEntry.setOnClickListener {
            setFoodEntryFragment()
        }
    }

    private fun setFoodEntryFragment() {
        val mainActivity = requireActivity() as MainActivity
        val fragment = FoodEntryFragment()

        mainActivity.replaceFragment(fragment, "FoodEntryFragment")
    }

    private fun setDiaryRecyclerView() {
        val fragment = requireParentFragment() as HomeFragment

        diaryAdapter = DiaryAdapter(foodList) { nutrient ->
            fragment.viewModel.resisterBookmark(nutrient)
            diaryAdapter.notifyDataSetChanged()
        }
        binding.recyclerView.adapter = diaryAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        diaryAdapter.setOnItemClickListener { position ->
            // 아이템 클릭 시 바텀시트 프래그먼트를 띄우는 코드
            val bottomSheetFragment = BottomSheetFragment()
            val bundle = Bundle()
            bundle.putInt("position", position) // 아이템 위치 전달
            bottomSheetFragment.arguments = bundle
            bottomSheetFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogCustomTheme)
            bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
        }
    }
}