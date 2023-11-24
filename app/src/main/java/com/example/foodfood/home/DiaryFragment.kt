package com.example.foodfood.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodfood.MainActivity
import com.example.foodfood.R
import com.example.foodfood.api.addBookmark.AddBookmark
import com.example.foodfood.api.addBookmark.BaseNutrition
import com.example.foodfood.api.foodOnDate.Data
import com.example.foodfood.databinding.FragmentDiaryBinding
import kotlinx.coroutines.launch

class DiaryFragment : Fragment(), FragmentResultListener {
    private lateinit var binding: FragmentDiaryBinding
    private lateinit var diaryAdapter: DiaryAdapter
    private lateinit var foodList: List<Data>
    private lateinit var mainActivity: MainActivity
    private var id = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Fragment Result API를 사용하기 위해 FragmentResultListener를 등록
        parentFragmentManager.setFragmentResultListener("bottomSheetResult", this, this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDiaryBinding.inflate(layoutInflater, container, false)

        mainActivity = requireActivity() as MainActivity
        id = mainActivity.getUserId()
        getFoodList()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getFoodList()

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
        diaryAdapter = DiaryAdapter(foodList) { nutrient ->
            val nutrition = BaseNutrition(nutrient.baseNutrition.carbohydrate, nutrient.baseNutrition.fat, nutrient.baseNutrition.kcal, nutrient.baseNutrition.protein)
            if (!nutrient.favoriteChecked) {
                val food = AddBookmark(nutrition, nutrient.foodId, nutrient.name, id)
                mainActivity.addBookmark(food)
            }
        }
        binding.recyclerView.adapter = diaryAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        diaryAdapter.setOnItemClickListener { id ->
            // 아이템 클릭 시 바텀시트 프래그먼트를 띄우는 코드
            val homeFragment = requireParentFragment() as HomeFragment

            val bottomSheetFragment = BottomSheetFragment(foodList.find { it.foodId == id }!!, homeFragment.year, homeFragment.month, homeFragment.day)

            bottomSheetFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogCustomTheme)
            bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
        }
    }

    fun getFoodList() {
        lifecycleScope.launch {
            val fragment = requireParentFragment() as HomeFragment
            foodList = fragment.getFoodList()

            if (foodList.isEmpty()) {
                binding.emptyFoodLayout.visibility = View.VISIBLE
            } else {
                binding.emptyFoodLayout.visibility = View.GONE
            }
            setDiaryRecyclerView()
        }
    }

    override fun onFragmentResult(requestKey: String, result: Bundle) { // FragmentResultListener를 사용하기 위해 구현해야 하는 함수
        if (requestKey == "bottomSheetResult") {
            // 작업 완료 시 호출되는 로직
            if (result.getBoolean("actionCompleted", false)) {
                updateDiaryList()
            }
        }
    }

    private fun updateDiaryList() {
        // 다이어리 프래그먼트를 갱신하는 작업 수행
        getFoodList()
        diaryAdapter.notifyDataSetChanged()
    }
}