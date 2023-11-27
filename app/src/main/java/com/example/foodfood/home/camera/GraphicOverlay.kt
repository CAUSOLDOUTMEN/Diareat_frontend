package com.example.foodfood.home.camera

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import com.example.foodfood.R

class GraphicOverlay(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private val paint = Paint()
    private val centerY = height / 2

    // 가이드라인 색상과 두께 설정
    init {
        paint.color = resources.getColor(R.color.orange)
        paint.strokeWidth = 10f
        paint.style = Paint.Style.STROKE
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2
        val centerY = height / 2
        canvas.drawRect((width / 30.0).toFloat(), (height / 40.0).toFloat(), (width * 29.0 / 30.0).toFloat(), (centerY * 3.0 / 2.0).toFloat(), paint)
    }

    fun getGuideLineRect(): Rect {
        return Rect(width / 30, height / 40, width * 29 / 30, (centerY) * 3 / 2)
    }
}