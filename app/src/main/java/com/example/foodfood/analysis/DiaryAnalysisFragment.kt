package com.example.foodfood.analysis

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.example.foodfood.MainActivity
import com.example.foodfood.R
import com.example.foodfood.api.analysisGraph.Data
import com.example.foodfood.databinding.FragmentDiaryAnalysisBinding
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class DiaryAnalysisFragment(private val data: Data) : Fragment() {
    private lateinit var binding: FragmentDiaryAnalysisBinding
    private lateinit var weekLineChart: LineChart
    private lateinit var fourWeekLineChart: LineChart
    private lateinit var buttons: List<ImageButton>
    private val weekChartData = arrayListOf<Entry>()
    private val fourWeekChartData = arrayListOf<Entry>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDiaryAnalysisBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttons = listOf(
            binding.btnKcal,
            binding.btnCarbohydrate,
            binding.btnProtein,
            binding.btnProvince
        )

        for (i in buttons.indices) {
            buttons[i].setOnClickListener {
                selectButton(i)
            }
        }

        selectButton(0)

        binding.weeklyScore.text = data.totalScore.toString()

        binding.btnDiaryDetail.setOnClickListener {
            val mainActivity = requireActivity() as MainActivity
            mainActivity.setDiaryAnalysisDetailFragment()
        }
    }

    private fun weekLineChart(chartData: ArrayList<Entry>) {
        weekLineChart = binding.weekLineChart

        val lineDataSet = LineDataSet(chartData, "최근 7일간")

        weekLineChart.apply {
            data = LineData(lineDataSet)
            invalidate()
        }

        weekLineChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM   //x축의 표시를 정하는 것. 기본값은 TOP이다.
        }

        lineDataSet.apply {
            lineWidth = 3f
            circleRadius = 5f
            color = resources.getColor(R.color.orange)
            valueTextSize = 0f
            circleHoleColor = resources.getColor(R.color.orange)
            circleColors = mutableListOf(resources.getColor(R.color.orange))
        }
    }

    private fun fourWeekLineChart(chartData: ArrayList<Entry>) {
        fourWeekLineChart = binding.fourWeekLineChart

        val lineDataSet = LineDataSet(chartData, "최근 4주간")

        fourWeekLineChart.apply {
            data = LineData(lineDataSet)
            invalidate()
        }

        fourWeekLineChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM   //x축의 표시를 정하는 것. 기본값은 TOP이다.
        }

        lineDataSet.apply {
            lineWidth = 3f
            circleRadius = 5f
            color = resources.getColor(R.color.orange)
            valueTextSize = 0f
            circleHoleColor = resources.getColor(R.color.orange)
            circleColors = mutableListOf(resources.getColor(R.color.orange))
        }
    }

    private fun selectButton(i: Int) {
        buttons[0].setImageResource(R.drawable.ic_btn_kcal_unchecked)
        buttons[1].setImageResource(R.drawable.ic_btn_carbohydrate_unchecked)
        buttons[2].setImageResource(R.drawable.ic_btn_protein_unchecked)
        buttons[3].setImageResource(R.drawable.ic_btn_province_unchecked)

        when(i) {
            0 -> {
                buttons[0].setImageResource(R.drawable.ic_btn_kcal_checked)
                kcalSelected()
            }
            1 -> {
                buttons[1].setImageResource(R.drawable.ic_btn_carbohydrate_checked)
                carbohydrateSelected()
            }
            2 -> {
                buttons[2].setImageResource(R.drawable.ic_btn_protein_checked)
                proteinSelected()
            }
            3 -> {
                buttons[3].setImageResource(R.drawable.ic_btn_province_checked)
                fatSelected()
            }
        }
    }

    private fun kcalSelected() {
        weekChartData.clear()
        fourWeekChartData.clear()

        for (i in data.calorieLastSevenDays.indices) {
            weekChartData.add(Entry(i.toFloat(), data.calorieLastSevenDays[i].toFloat()))
        }

        for (i in data.calorieLastFourWeek.indices) {
            fourWeekChartData.add(Entry(i.toFloat(), data.calorieLastFourWeek[i].toFloat()))
        }

        Log.e("뭐가 문제냐", weekChartData.toString())
        Log.e("뭐가 문제냐", fourWeekChartData.toString())

        weekLineChart(weekChartData)
        fourWeekLineChart(fourWeekChartData)
    }

    private fun carbohydrateSelected() {
        weekChartData.clear()
        fourWeekChartData.clear()

        for (i in data.carbohydrateLastSevenDays.indices) {
            weekChartData.add(Entry(i.toFloat(), data.carbohydrateLastSevenDays[i].toFloat()))
        }

        for (i in data.carbohydrateLastFourWeek.indices) {
            fourWeekChartData.add(Entry(i.toFloat(), data.carbohydrateLastFourWeek[i].toFloat()))
        }

        Log.e("뭐가 문제냐", weekChartData.toString())
        Log.e("뭐가 문제냐", fourWeekChartData.toString())

        weekLineChart(weekChartData)
        fourWeekLineChart(fourWeekChartData)
    }

    private fun proteinSelected() {
        weekChartData.clear()
        fourWeekChartData.clear()

        for (i in data.proteinLastSevenDays.indices) {
            weekChartData.add(Entry(i.toFloat(), data.proteinLastSevenDays[i].toFloat()))
        }

        for (i in data.proteinLastFourWeek.indices) {
            fourWeekChartData.add(Entry(i.toFloat(), data.proteinLastFourWeek[i].toFloat()))
        }

        Log.e("뭐가 문제냐", weekChartData.toString())
        Log.e("뭐가 문제냐", fourWeekChartData.toString())

        weekLineChart(weekChartData)
        fourWeekLineChart(fourWeekChartData)
    }

    private fun fatSelected() {
        weekChartData.clear()
        fourWeekChartData.clear()

        for (i in data.fatLastSevenDays.indices) {
            weekChartData.add(Entry(i.toFloat(), data.fatLastSevenDays[i].toFloat()))
        }

        for (i in data.fatLastFourWeek.indices) {
            fourWeekChartData.add(Entry(i.toFloat(), data.fatLastFourWeek[i].toFloat()))
        }

        Log.e("뭐가 문제냐", weekChartData.toString())
        Log.e("뭐가 문제냐", fourWeekChartData.toString())

        weekLineChart(weekChartData)
        fourWeekLineChart(fourWeekChartData)
    }
}