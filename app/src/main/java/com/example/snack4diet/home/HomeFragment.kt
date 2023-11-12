package com.example.snack4diet.home

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment.STYLE_NORMAL
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.snack4diet.MainActivity
import com.example.snack4diet.R
import com.example.snack4diet.api.DayWithWeekday
import com.example.snack4diet.api.NutritionItem
import com.example.snack4diet.api.createFood.CreateFood
import com.example.snack4diet.api.foodOnDate.Data
import com.example.snack4diet.api.foodOnDate.FoodOnDate
import com.example.snack4diet.api.foodOnDate.Header
import com.example.snack4diet.api.nutritionSummary.NutritionSummary
import com.example.snack4diet.application.MyApplication
import com.example.snack4diet.bookmark.BookmarkFragment
import com.example.snack4diet.calendar.CalendarAdapter
import com.example.snack4diet.databinding.FragmentHomeBinding
import com.example.snack4diet.profile.ProfileFragment
import com.example.snack4diet.viewModel.NutrientsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var btnNutrition: Button
    private lateinit var dailyNutrition: NutritionSummary
    private lateinit var foodOnDate: FoodOnDate
    lateinit var viewModel: NutrientsViewModel
    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var dayList: MutableList<DayWithWeekday>
    private lateinit var itemClickListener: ItemClickListener
    private lateinit var app: MyApplication
    private var userId = -1L
    private lateinit var sharedPreferences: SharedPreferences
    private val today = LocalDate.now()
    private var year = today.year
    private var month = today.monthValue
    private var day = today.dayOfMonth
    private lateinit var jwt: String

    interface ItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        btnNutrition = binding.btnTodayNutrition
        sharedPreferences = (requireActivity() as MainActivity).getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        jwt = sharedPreferences.getString("jwt", "")!!
        app = (requireActivity() as MainActivity).application
        setCalendar(year, month, day)
        viewModel = (requireActivity() as MainActivity).getViewModel()
        userId = sharedPreferences.getLong("id", -1L)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.year.text = today.year.toString()
        binding.month.text = today.monthValue.toString()

        binding.btnTodayNutrition.setOnClickListener {// 오늘의 영양 탭 클릭 리스너
            todayNutritionClicked()

            setNutritionSummary()
        }

        binding.btnDiary.setOnClickListener {// 다이어리 탭 클릭 리스너
            diaryClicked()

            setDiaryDataSet()
        }

        binding.btnBookmark.setOnClickListener {// 북마크 버튼 클릭 리스너
            setBookmarkFragment()
        }

        binding.btnLeft.setOnClickListener {
            val currentMonth = binding.month.text.toString().toInt()
            val currentYear = binding.year.text.toString().toInt()

            if (currentMonth > 1) {
                month = currentMonth - 1
                binding.month.text = month.toString()
                setCalendar(currentYear, month, 1)
            } else if (currentMonth == 1) {
                val newYear = currentYear - 1
                binding.month.text = "12"
                binding.year.text = newYear.toString()
                setCalendar(newYear, 12, 1)
            }
        }

        binding.btnRight.setOnClickListener {
            val currentMonth = binding.month.text.toString().toInt()
            val currentYear = binding.year.text.toString().toInt()

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

        binding.btnProfile.setOnClickListener {
            setProfileFragment()
        }

        todayNutritionClicked()
    }

    private fun setEmptyFragment() {
        val fragment = EmptyFoodFragment()

        replaceSubFragment(fragment, "EmptyFoodFragment")
    }

    private fun setTodayNutritionFragment() {
        val fragment = TodayNutritionFragment()

        replaceSubFragment(fragment, "TodayNutritionFragment")
    }

    private fun setDiaryFragment() {
        val fragment = DiaryFragment()

        replaceSubFragment(fragment, "DiaryFragment")
    }

    private fun setDiaryDataSet() {
        if (!isAdded) {
            // 프래그먼트가 액티비티에 추가되지 않은 경우
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = app.apiService.getFoodOnDate(userId, year, month, day)
                if (response.data == null) {
                    foodOnDate = FoodOnDate(data = emptyList(), header = Header(code = 200, message = "SUCCESS"), msg = "음식")
                } else {
                    foodOnDate = response
                }
                Log.e("여기는 뭔데??????", foodOnDate.toString())
            } catch (e: Exception) {
                Log.e("HomeFragment", "Error during getFoodOnDate API call", e)
            }

            withContext(Dispatchers.Main) {
                Log.e("너냐???????????????", foodOnDate.toString())
                setDiaryFragment()
            }
        }
    }

    private fun setNutritionSummary() {
        todayNutritionClicked()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                dailyNutrition = app.apiService.getNutritionSummary(userId, day, month, year)
            } catch (e: Exception) {
                Log.e("HomeFragment", "Error during getNutritionSummary API call", e)
            }

            withContext(Dispatchers.Main) {
                if (dailyNutrition.data.totalKcal == 0) {
                    setEmptyFragment()
                } else {
                    setTodayNutritionFragment()
                }
            }
        }
    }

    private fun setBookmarkFragment() {
        val mainActivity = requireActivity() as MainActivity
        val fragment = BookmarkFragment()

        mainActivity.replaceFragment(fragment, "BookmarkFragment")
    }

    private fun setProfileFragment() {
        val mainActivity = requireActivity() as MainActivity
        val fragment = ProfileFragment()

        mainActivity.replaceFragment(fragment, "ProfileFragment")
    }

    private fun replaceSubFragment(fragment: Fragment, tag: String) {
        childFragmentManager.beginTransaction()
            .replace(R.id.subFrame, fragment, tag)
            .addToBackStack(null) // 이전 프래그먼트를 백스택에 추가
            .commit()
    }

    private fun setCalendar(pYear: Int, pMonth: Int, pDay: Int) {
        year = pYear
        month = pMonth
        day = pDay

        dayList = getDaysInMonth(year, month)
        var initialPosition = day - 1 // 리사이클러뷰의 시작 위치 인덱스는 0부터 시작
        dayList[initialPosition].isClicked = true

        itemClickListener = object : ItemClickListener {
            override fun onItemClick(position: Int) {
                dayList[initialPosition].isClicked = false
                initialPosition = position
                day = position + 1
                dayList[initialPosition].isClicked = true
                calendarAdapter.notifyDataSetChanged()
                setNutritionSummary()
            }
        }

        calendarAdapter = CalendarAdapter(dayList, initialPosition, requireContext(), itemClickListener)
        binding.recyclerViewDate.adapter = calendarAdapter

        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewDate.layoutManager = layoutManager

        // 리사이클러뷰 시작 위치 조정
        if (initialPosition - 3 < 0) layoutManager.scrollToPosition(0)
        else layoutManager.scrollToPosition(initialPosition - 3)

        setNutritionSummary()
    }

    private fun getDaysInMonth(year: Int, month: Int): MutableList<DayWithWeekday> {
        val daysWithWeekdays = mutableListOf<DayWithWeekday>()
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month - 1)
        val lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        val formatter = DateTimeFormatter.ofPattern("E", Locale("ko-KR")) // 요일을 해당 나라의 언어로 저장하는 방법
        for (i in 1..lastDay) {
            val date = LocalDate.of(year, month, i)
            val weekday = date.format(formatter)
            daysWithWeekdays.add(DayWithWeekday(i, weekday))
        }

        return daysWithWeekdays
    }

    private fun todayNutritionClicked() {
        btnNutrition.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange))
        binding.btnDiary.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray))
        binding.underLine1.visibility = View.VISIBLE
        binding.underLine2.visibility = View.GONE
    }

    private fun diaryClicked() {
        btnNutrition.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray))
        binding.btnDiary.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange))
        binding.underLine1.visibility = View.GONE
        binding.underLine2.visibility = View.VISIBLE
    }

    fun getDailyNutrition(): NutritionSummary {
        return dailyNutrition
    }

    fun getFoodList(): List<Data> {

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = app.apiService.getFoodOnDate(userId, year, month, day)
                if (response.data == null) {
                    foodOnDate = FoodOnDate(data = emptyList(), header = Header(code = 200, message = "SUCCESS"), msg = "음식")
                } else {
                    foodOnDate = response
                }
                Log.e("여기는 뭔데??????", foodOnDate.toString())
            } catch (e: Exception) {
                Log.e("HomeFragment", "Error during getFoodOnDate API call", e)
            }
        }

        return foodOnDate.data
    }
}