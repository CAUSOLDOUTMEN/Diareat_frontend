package com.example.snack4diet.home

import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.VelocityTracker
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment.STYLE_NORMAL
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.snack4diet.MainActivity
import com.example.snack4diet.R
import com.example.snack4diet.api.Macronutrients
import com.example.snack4diet.api.NutritionItem
import com.example.snack4diet.bookmark.BookmarkFragment
import com.example.snack4diet.databinding.FragmentHomeBinding
import com.example.snack4diet.viewModel.NutrientsViewModel
import java.time.DayOfWeek
import java.time.LocalDate

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var btnNutrition: Button
    private lateinit var homeAdapter: HomeAdapter
    private lateinit var dailyNutrition: List<NutritionItem>
    private lateinit var diaryAdapter: DiaryAdapter
    private lateinit var viewModel: NutrientsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (requireActivity() as MainActivity).getViewModel()

        val today = LocalDate.now()
        var dayOfWeek = today.dayOfWeek.toString()

        when(dayOfWeek) {
            DayOfWeek.MONDAY.toString() -> dayOfWeek = "월"
            DayOfWeek.TUESDAY.toString() -> dayOfWeek = "화"
            DayOfWeek.WEDNESDAY.toString() -> dayOfWeek = "수"
            DayOfWeek.THURSDAY.toString() -> dayOfWeek = "목"
            DayOfWeek.FRIDAY.toString() -> dayOfWeek = "금"
            DayOfWeek.SATURDAY.toString() -> dayOfWeek = "토"
            DayOfWeek.SUNDAY.toString() -> dayOfWeek = "일"
        }

        binding.date.text = "${today.monthValue}월/${today.dayOfMonth}일 ($dayOfWeek)"

        btnNutrition = binding.btnTodayNutrition

        dailyNutrition = listOf(
            NutritionItem("kcal", 1250, 2500),
            NutritionItem("carbohydrate", 243, 324),
            NutritionItem("protein", 16, 64,),
            NutritionItem("province", 52, 51)
        )

        if (dailyNutrition == null) {
            setEmptyFragment()
        } else {
            setRecyclerView()
        }

        binding.btnTodayNutrition.setOnClickListener {
            onResume()
        }

        binding.btnDiary.setOnClickListener {
            setDiaryRecyclerView()
        }

        binding.btnBookmark.setOnClickListener {
            val mainActivity = requireActivity() as MainActivity
            val fragment = BookmarkFragment()

            mainActivity.replaceFragment(fragment, "BookmarkFragment")
        }
    }

    private fun setEmptyFragment() {
        val fragment = EmptyFoodFragment()

        binding.recyclerView.visibility = View.GONE
        btnNutrition.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange))
        binding.underLine1.visibility = View.VISIBLE

        replaceSubFragment(fragment, "EmptyFoodFragment")
    }

    private fun setDiaryRecyclerView() {
        //리사이클러뷰 설정
        diaryAdapter = DiaryAdapter(emptyList()) { nutrient ->
            viewModel.resisterBookmark(nutrient)
            diaryAdapter.notifyDataSetChanged()
        }
        binding.recyclerView.adapter = diaryAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        //클릭되지 않은 버튼 처리
        btnNutrition.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray))
        binding.underLine1.visibility = View.GONE
        //클릭된 버튼 처리
        binding.btnDiary.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange))
        binding.recyclerView.visibility = View.VISIBLE
        binding.underLine2.visibility = View.VISIBLE

        diaryAdapter.setOnItemClickListener { position ->
            // 아이템 클릭 시 바텀시트 프래그먼트를 띄우는 코드
            val bottomSheetFragment = BottomSheetFragment()
            val bundle = Bundle()
            bundle.putInt("position", position) // 아이템 위치 전달
            bottomSheetFragment.arguments = bundle
            bottomSheetFragment.setStyle(STYLE_NORMAL, R.style.DialogCustomTheme)
            bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
        }

        viewModel.nutrientsLiveData.observe(requireActivity()) { nutrientsLiveData ->
            diaryAdapter.nutrients = nutrientsLiveData
            diaryAdapter.notifyDataSetChanged()
        }
    }

    private fun setRecyclerView() {
        //리사이클러뷰 설정
        homeAdapter = HomeAdapter(dailyNutrition, requireContext())
        binding.recyclerView.adapter = homeAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        //클릭된 버튼 처리
        btnNutrition.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange))
        binding.underLine1.visibility = View.VISIBLE
        //클릭되지 않은 버튼 처리
        binding.btnDiary.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray))
        binding.recyclerView.visibility = View.VISIBLE
        binding.underLine2.visibility = View.GONE
    }

    private fun replaceSubFragment(fragment: Fragment, tag: String) {
        childFragmentManager.beginTransaction()
            .replace(R.id.subFrame, fragment, tag)
            .addToBackStack(null) // 이전 프래그먼트를 백스택에 추가
            .commit()
    }

    override fun onResume() {
        super.onResume()

        val currentFragment = childFragmentManager.findFragmentById(R.id.subFrame)

        if (currentFragment is EmptyFoodFragment) {
            setEmptyFragment()
        } else {
            setRecyclerView()
        }
    }
}