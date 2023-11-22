package com.example.sonng266.chart

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path

class HighlightRenderer(
    private val viewPortHandler: ViewPortHandler,
    private val transformer: Transformer
) : Renderer(viewPortHandler = viewPortHandler) {

    var position: Pair<Float, Float>? = null
    private val paint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.RED
        strokeWidth = 10f
    }
    private val paintCircle = Paint().apply {
        style = Paint.Style.FILL
        color = Color.RED
    }

    private val paintCircleBorder = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.WHITE
        strokeWidth = 4f
    }

    override fun render(canvas: Canvas) {
        if (position == null) return

        val axis = floatArrayOf(position!!.first, position!!.second)
        transformer.pointValuesToPixel(axis)
        val path = Path()
        path.moveTo(axis[0], axis[1])
        path.lineTo(axis[0], viewPortHandler.contentBottom())
        canvas.drawPath(path, paint)
        canvas.drawCircle(axis[0], axis[1], 10f, paintCircle)
        canvas.drawCircle(axis[0], axis[1], 10f, paintCircleBorder)
    }
}
