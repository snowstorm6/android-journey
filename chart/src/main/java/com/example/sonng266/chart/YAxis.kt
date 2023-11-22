package com.example.sonng266.chart

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.SizeF

class YAxis(context: Context) : Axis(context) {

    var spaceBetweenAxisLineAndValue = convertDpToPixel(6f, context)

    init {
        gridLineColor = Color.parseColor("#CACACA")
    }

    fun computeMaximumValueTextSize(valueTextPaint: Paint): SizeF {
        valueTextPaint.textSize = valueTextSize
        val maxLengthLabel = entries.maxBy { it.first.length }.first
        val rect = Rect()
        rect.set(0, 0, 0, 0)
        valueTextPaint.getTextBounds(maxLengthLabel, 0, maxLengthLabel.length, rect)
        return SizeF(rect.width().toFloat(), rect.height().toFloat())
    }

    fun computeTextSize(valueTextPaint: Paint, label: String): SizeF {
        valueTextPaint.textSize = valueTextSize
        val rect = Rect()
        rect.set(0, 0, 0, 0)
        valueTextPaint.getTextBounds(label, 0, label.length, rect)
        return SizeF(rect.width().toFloat(), rect.height().toFloat())
    }
}