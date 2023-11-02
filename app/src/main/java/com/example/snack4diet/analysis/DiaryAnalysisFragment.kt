package com.example.snack4diet.analysis

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.example.snack4diet.R
import com.example.snack4diet.databinding.FragmentDiaryAnalysisBinding
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.ChartData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet

class DiaryAnalysisFragment : Fragment() {
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

        weekChartData.add(Entry(10.0f, 30.0f))
        weekChartData.add(Entry(11.0f, 35.0f))
        weekChartData.add(Entry(12.0f, 39.0f))
        weekChartData.add(Entry(13.0f, 31.0f))
        weekChartData.add(Entry(14.0f, 36.0f))
        weekChartData.add(Entry(15.0f, 34.0f))
        weekChartData.add(Entry(16.0f, 30.0f))

        fourWeekChartData.add(Entry(10.0f, 34.0f))
        fourWeekChartData.add(Entry(11.0f, 39.0f))
        fourWeekChartData.add(Entry(12.0f, 36.0f))
        fourWeekChartData.add(Entry(13.0f, 33.0f))
        fourWeekChartData.add(Entry(14.0f, 31.0f))
        fourWeekChartData.add(Entry(15.0f, 38.0f))
        fourWeekChartData.add(Entry(16.0f, 35.0f))

        weekLineChart(weekChartData)
        fourWeekLineChart(fourWeekChartData)
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
            valueTextSize = 15f
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
            valueTextSize = 15f
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
            }
            1 -> {
                buttons[1].setImageResource(R.drawable.ic_btn_carbohydrate_checked)
            }
            2 -> {
                buttons[2].setImageResource(R.drawable.ic_btn_protein_checked)
            }
            3 -> {
                buttons[3].setImageResource(R.drawable.ic_btn_province_checked)
            }
        }
    }
}