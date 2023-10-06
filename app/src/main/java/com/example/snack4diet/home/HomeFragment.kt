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
import com.example.snack4diet.api.DayWithWeekday
import com.example.snack4diet.api.Macronutrients
import com.example.snack4diet.api.NutritionItem
import com.example.snack4diet.bookmark.BookmarkFragment
import com.example.snack4diet.calendar.CalendarAdapter
import com.example.snack4diet.databinding.FragmentHomeBinding
import com.example.snack4diet.viewModel.NutrientsViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var btnNutrition: Button
    private lateinit var homeAdapter: HomeAdapter
    private lateinit var dailyNutrition: List<NutritionItem>
    private lateinit var diaryAdapter: DiaryAdapter
    private lateinit var viewModel: NutrientsViewModel
    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var dayList: MutableList<DayWithWeekday>
    private lateinit var itemClickListener: ItemClickListener

    interface ItemClickListener {
        fun onItemClick(position: Int)
    }

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
        val currentYear = today.year
        val currentMonth = today.monthValue
        val currentDay = today.dayOfMonth

        setCalendar(currentYear, currentMonth, currentDay)

        binding.year.text = today.year.toString()
        binding.month.text = today.monthValue.toString()

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
            setBookmarkFragment()
        }

        binding.btnLeft.setOnClickListener {
            var currentMonth = binding.month.text.toString().toInt()
            var currentYear = binding.year.text.toString().toInt()

            if (currentMonth > 1) {
                val newMonth = currentMonth - 1
                binding.month.text = newMonth.toString()
                setCalendar(currentYear, newMonth, 1)
            } else if (currentMonth == 1) {
                val newYear = currentYear - 1
                binding.month.text = "12"
                binding.year.text = newYear.toString()
                setCalendar(newYear, 12, 1)
            }
        }

        binding.btnRight.setOnClickListener {
            var currentMonth = binding.month.text.toString().toInt()
            var currentYear = binding.year.text.toString().toInt()

            if (currentMonth < 12) {
                val newMonth = currentMonth + 1
                binding.month.text = newMonth.toString()
                setCalendar(currentYear, newMonth, 1)
            } else if (currentMonth == 12) {
                val newYear = currentYear + 1
                binding.month.text = "1"
                binding.year.text = newYear.toString()
                setCalendar(newYear, 1, 1)
            }
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
            setDiaryDataSet()
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

        setDiaryDataSet()
    }

    private fun setDiaryDataSet() {
        viewModel.nutrientsLiveData.observe(requireActivity()) { nutrientsLiveData ->
            diaryAdapter.nutrients = nutrientsLiveData
            diaryAdapter.notifyDataSetChanged()
        }
    }

    private fun setBookmarkFragment() {
        val mainActivity = requireActivity() as MainActivity
        val fragment = BookmarkFragment()

        mainActivity.replaceFragment(fragment, "BookmarkFragment")
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

    private fun setCalendar(year: Int, month: Int, day: Int) {
        dayList = getDaysInMonth(year, month)
        var initialPosition = day - 1 // 리사이클러뷰의 시작 위치 인덱스는 0부터 시작
        dayList[initialPosition].isClicked = true

        itemClickListener = object : ItemClickListener {
            override fun onItemClick(position: Int) {
                dayList[initialPosition].isClicked = false
                initialPosition = position
                dayList[initialPosition].isClicked = true
                calendarAdapter.notifyDataSetChanged()
            }
        }

        calendarAdapter = CalendarAdapter(dayList, initialPosition, requireContext(), itemClickListener)

        binding.recyclerViewDate.adapter = calendarAdapter

        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewDate.layoutManager = layoutManager

        // 리사이클러뷰 시작 위치 조정
        if (initialPosition - 3 < 0) layoutManager.scrollToPosition(0)
        else layoutManager.scrollToPosition(initialPosition - 3)
    }

    private fun getDaysInMonth(year: Int, month: Int): MutableList<DayWithWeekday> {
        val daysWithWeekdays = mutableListOf<DayWithWeekday>()
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month - 1)
        val lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        for (i in 1..lastDay) {
            val date = LocalDate.of(year, month, i)
            val weekday = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()) // 요일을 해당 나라의 언어로 저장하는 방법
            daysWithWeekdays.add(DayWithWeekday(i, weekday))
        }

        return daysWithWeekdays
    }
}