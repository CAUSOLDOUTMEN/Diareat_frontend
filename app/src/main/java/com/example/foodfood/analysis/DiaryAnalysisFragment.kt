package com.example.foodfood.analysis

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.example.foodfood.MainActivity
import com.example.foodfood.R
import com.example.foodfood.api.analysisGraph.Data
import com.example.foodfood.databinding.FragmentDiaryAnalysisBinding
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.Locale


class DiaryAnalysisFragment(private val data: Data) : Fragment() {
    private lateinit var binding: FragmentDiaryAnalysisBinding
    private lateinit var weekLineChart: LineChart
    private lateinit var fourWeekLineChart: LineChart
    private lateinit var buttons: List<ImageButton>
    private val weekChartData = arrayListOf<Entry>()
    private val fourWeekChartData = arrayListOf<Entry>()
    private var selectedButtonIndex = 0
    private lateinit var weekMarkerView: MyMarkerView
    private lateinit var fourWeekMarkerView: MyMarkerView

    inner class DateAxisValueFormatter(private val dates: List<Map<String, Int>>) : IndexAxisValueFormatter() {
        override fun getFormattedValue(value: Float): String { // 여기서 전달받는 value는 x축의 label을 나타내는 값
            val index = value.toInt()
            if (index >= 0 && index < dates.size) {
                val dateString = dates[index].keys.first()

                return formatDateToMonthDay(dateString)
            } else {
                return ""
            }
        }
    }

    inner class WeekAxisValueFormatter(private val dates: List<Int>): IndexAxisValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            val index = value.toInt()
            if (index >= 0 && index < dates.size) {
                if (dates.size - index - 1 == 0) {
                    return "이번주"
                } else {
                    val newXAxis = "${dates.size - index - 1}주전"

                    return newXAxis
                }
            }
            return ""
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDiaryAnalysisBinding.inflate(layoutInflater, container, false)

        weekMarkerView = MyMarkerView(requireContext(), R.layout.custom_marker_view)
        fourWeekMarkerView = MyMarkerView(requireContext(), R.layout.custom_marker_view)

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

        selectButton(selectedButtonIndex)

        binding.weeklyScore.text = data.totalScore.toString()

        binding.btnDiaryDetail.setOnClickListener {
            val mainActivity = requireActivity() as MainActivity
            mainActivity.setDiaryAnalysisDetailFragment()
        }
    }

    private fun weekLineChart(chartData: ArrayList<Entry>) {
        weekLineChart = binding.weekLineChart
        weekLineChart.description.isEnabled = false
        weekMarkerView.chartView = weekLineChart
        weekLineChart.marker = weekMarkerView

        val lineDataSet = LineDataSet(chartData, "최근 7일간")


        val legend: Legend = weekLineChart.getLegend()  //범례 가져오기
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM //범례의 수직 위치 설정
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER //범례의 수평 위치 설정
        legend.form = Legend.LegendForm.CIRCLE  // 범례를 원 모양으로 표시
        legend.formSize = 10f   //범례의 크기
        legend.textSize = 10f   // 범례의 텍스트 크기
        legend.textColor = Color.parseColor("#ff6444")  //범례의 텍스트 색 조정
        legend.orientation = Legend.LegendOrientation.VERTICAL  //범례의 배치 방향 설정
        legend.setDrawInside(false) // 범례가 그래프 안에 그려지지 않도록 설정
        legend.yEntrySpace = 5f // 범례 항목 사이의 간격 설정 (하나의 차트에 그려야 할 데이터가 많은 경우)
        legend.isWordWrapEnabled = true //범례의 텍스트가 긴 경우 자동으로 줄 바꿈
        legend.xOffset = 80f
        legend.yOffset = 20f
        legend.calculatedLineSizes

        weekLineChart.apply {
            data = LineData(lineDataSet)
            invalidate()
        }

        weekLineChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM   //x축의 표시를 정하는 것. 기본값은 TOP이다.
//            setDrawAxisLine(false) //x축의 라인을 그리지 않도록 설정
            setDrawGridLines(false) //x축의 그리드 라인을 그리지 않도록 설정
            granularity = 1f //x축의 간격을 설정.
            textSize = 10f //x축의 텍스트 크기를 설정
            textColor = Color.rgb(118, 118, 118) //x축 텍스트의 색 조정
            spaceMin = 0.1f
            spaceMax = 0.1f
            when(selectedButtonIndex) {
                0 -> {
                    valueFormatter = DateAxisValueFormatter(data.calorieLastSevenDays)
                }
                1 -> {
                    valueFormatter = DateAxisValueFormatter(data.carbohydrateLastSevenDays)
                }
                2 -> {
                    valueFormatter = DateAxisValueFormatter(data.proteinLastSevenDays)
                }
                3 -> {
                    valueFormatter = DateAxisValueFormatter(data.fatLastSevenDays)
                }

            }
        }

        weekLineChart.axisLeft.apply{
//            setDrawGridLines(false)
            textSize = 14f
            textColor = Color.rgb(118, 118, 118)
        }

        weekLineChart.axisRight.apply {
            setDrawGridLines(false)
            isEnabled = false
        }

        lineDataSet.apply {
            lineWidth = 3f
            circleRadius = 6f
            color = resources.getColor(R.color.orange)
            valueTextSize = 0f
            circleHoleColor = resources.getColor(R.color.orange)
            circleColors = mutableListOf(resources.getColor(R.color.orange))
        }
    }

    private fun fourWeekLineChart(chartData: ArrayList<Entry>) {
        fourWeekLineChart = binding.fourWeekLineChart
        fourWeekLineChart.description.isEnabled = false
        fourWeekMarkerView.chartView = fourWeekLineChart
        fourWeekLineChart.marker = fourWeekMarkerView

        val lineDataSet = LineDataSet(chartData, "최근 4주간")

        val legend: Legend = fourWeekLineChart.getLegend()  //범례 가져오기
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM //범례의 수직 위치 설정
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER //범례의 수평 위치 설정
        legend.form = Legend.LegendForm.CIRCLE  // 범례를 원 모양으로 표시
        legend.formSize = 10f   //범례의 크기
        legend.textSize = 10f   // 범례의 텍스트 크기
        legend.textColor = Color.parseColor("#ff6444")  //범례의 텍스트 색 조정
        legend.orientation = Legend.LegendOrientation.VERTICAL  //범례의 배치 방향 설정
        legend.setDrawInside(false) // 범례가 그래프 안에 그려지지 않도록 설정
        legend.yEntrySpace = 5f // 범례 항목 사이의 간격 설정 (하나의 차트에 그려야 할 데이터가 많은 경우)
        legend.isWordWrapEnabled = true //범례의 텍스트가 긴 경우 자동으로 줄 바꿈
        legend.xOffset = 80f
        legend.yOffset = 20f
        legend.calculatedLineSizes

        fourWeekLineChart.apply {
            data = LineData(lineDataSet)
            invalidate()
        }

        fourWeekLineChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM   //x축의 표시를 정하는 것. 기본값은 TOP이다.
//            setDrawAxisLine(false) //x축의 라인을 그리지 않도록 설정
            setDrawGridLines(false) //x축의 그리드 라인을 그리지 않도록 설정
            granularity = 1f //x축의 간격을 설정.
            textSize = 14f //x축의 텍스트 크기를 설정
            textColor = Color.rgb(118, 118, 118) //x축 텍스트의 색 조정
            spaceMin = 0.1f
            spaceMax = 0.1f
            when(selectedButtonIndex) {
                0 -> {
                    valueFormatter = WeekAxisValueFormatter(data.calorieLastFourWeek)
                }
                1 -> {
                    valueFormatter = WeekAxisValueFormatter(data.carbohydrateLastFourWeek)
                }
                2 -> {
                    valueFormatter = WeekAxisValueFormatter(data.proteinLastFourWeek)
                }
                3 -> {
                    valueFormatter = WeekAxisValueFormatter(data.fatLastFourWeek)
                }

            }
        }

        fourWeekLineChart.axisLeft.apply{
//            setDrawGridLines(false)
            textSize = 14f
            textColor = Color.rgb(118, 118, 118)
        }

        fourWeekLineChart.axisRight.apply {
            setDrawGridLines(false)
            isEnabled = false
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
                selectedButtonIndex = 0
                kcalSelected()
            }
            1 -> {
                buttons[1].setImageResource(R.drawable.ic_btn_carbohydrate_checked)
                selectedButtonIndex = 1
                carbohydrateSelected()
            }
            2 -> {
                buttons[2].setImageResource(R.drawable.ic_btn_protein_checked)
                selectedButtonIndex = 2
                proteinSelected()
            }
            3 -> {
                buttons[3].setImageResource(R.drawable.ic_btn_province_checked)
                selectedButtonIndex = 3
                fatSelected()
            }
        }
    }

    private fun kcalSelected() {
        weekChartData.clear()
        fourWeekChartData.clear()

        for ((index, map) in data.calorieLastSevenDays.withIndex()) {
            val (_, value) = map.entries.first()
            weekChartData.add(Entry(index.toFloat(), value.toFloat()))
        }

        for (i in data.calorieLastFourWeek.indices) {
            fourWeekChartData.add(Entry(i.toFloat(), data.calorieLastFourWeek[i].toFloat()))
        }

        weekLineChart(weekChartData)
        fourWeekLineChart(fourWeekChartData)
    }

    private fun carbohydrateSelected() {
        weekChartData.clear()
        fourWeekChartData.clear()

        for ((index, map) in data.carbohydrateLastSevenDays.withIndex()) {
            val (_, value) = map.entries.first()
            weekChartData.add(Entry(index.toFloat(), value.toFloat()))
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

        for ((index, map) in data.proteinLastSevenDays.withIndex()) {
            val (_, value) = map.entries.first()
            weekChartData.add(Entry(index.toFloat(), value.toFloat()))
        }

        for (i in data.proteinLastFourWeek.indices) {
            fourWeekChartData.add(Entry(i.toFloat(), data.proteinLastFourWeek[i].toFloat()))
        }

        weekLineChart(weekChartData)
        fourWeekLineChart(fourWeekChartData)
    }

    private fun fatSelected() {
        weekChartData.clear()
        fourWeekChartData.clear()

        for ((index, map) in data.fatLastSevenDays.withIndex()) {
            val (_, value) = map.entries.first()
            weekChartData.add(Entry(index.toFloat(), value.toFloat()))
        }

        for (i in data.fatLastFourWeek.indices) {
            fourWeekChartData.add(Entry(i.toFloat(), data.fatLastFourWeek[i].toFloat()))
        }

        weekLineChart(weekChartData)
        fourWeekLineChart(fourWeekChartData)
    }

    private fun formatDateToMonthDay(dateString: String): String {
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputDateFormat = SimpleDateFormat("MM-dd", Locale.getDefault())
        val date = inputDateFormat.parse(dateString)
        // inputDateFormat의 형태로 즉, yyyy-MM-dd의 형태로 파라미터로 받은 dateString을 Date 객체로 변환한다.
        // 현재 전달받고 있는 dateString의 형태가 yyyy-MM-dd 형태이다.

        Log.e("진짜 ㅈ같다", outputDateFormat.format(date!!))
        return outputDateFormat.format(date!!)  //outputDateFormat의 형태(MM-dd)로 date를 String으로 변환한다
    }
}