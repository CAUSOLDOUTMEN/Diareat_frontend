package com.example.foodfood.analysis

import android.content.Context
import android.widget.TextView
import com.example.foodfood.R
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Utils


class MyMarkerView(context: Context, layoutResource: Int) : MarkerView(context, layoutResource) {

    private val tvContent: TextView = findViewById(R.id.tvContentHead)

    // MarkerView가 다시 그려질 때마다 호출되는 콜백으로, 콘텐츠를 업데이트하는 데 사용할 수 있다.
    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        e?.let {
            val text = if (it is CandleEntry) {
                "${Utils.formatNumber(it.high, 0, true)}"
            } else {
                "${Utils.formatNumber(it.y, 0, true)}"
            }
            tvContent.text = text
        }

        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF(-(width / 2f), -height.toFloat())
    }
}