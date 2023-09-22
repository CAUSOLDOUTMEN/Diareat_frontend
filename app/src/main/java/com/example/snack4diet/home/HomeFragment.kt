package com.example.snack4diet.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.VelocityTracker
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.snack4diet.R
import com.example.snack4diet.api.Macronutrients
import com.example.snack4diet.api.NutritionItem
import com.example.snack4diet.databinding.FragmentHomeBinding
import java.time.DayOfWeek
import java.time.LocalDate

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var btnNutrition: Button
    private lateinit var homeAdapter: HomeAdapter
    private lateinit var dailyNutrition: List<NutritionItem>
    private lateinit var diaryAdapter: DiaryAdapter
    private lateinit var nutrients: List<Macronutrients>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        nutrients = listOf(
            Macronutrients(325, 24,32,25),
            Macronutrients(325, 24,32,25),
            Macronutrients(325, 24,32,25),
            Macronutrients(325, 24,32,25)
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
    }

    private fun setEmptyFragment() {
        val fragment = EmptyFoodFragment()

        binding.recyclerView.visibility = View.GONE
        btnNutrition.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange))

        replaceSubFragment(fragment, "EmptyFoodFragment")
    }

    private fun setDiaryRecyclerView() {
        diaryAdapter = DiaryAdapter(nutrients)
        binding.recyclerView.adapter = diaryAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        btnNutrition.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray))
        binding.btnDiary.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange))
        binding.recyclerView.visibility = View.VISIBLE
    }

    private fun setRecyclerView() {
        homeAdapter = HomeAdapter(dailyNutrition, requireContext())
        binding.recyclerView.adapter = homeAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        btnNutrition.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange))
        binding.btnDiary.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray))
        binding.recyclerView.visibility = View.VISIBLE
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