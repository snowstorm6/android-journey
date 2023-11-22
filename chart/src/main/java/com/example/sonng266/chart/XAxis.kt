package com.example.sonng266.chart

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.SizeF

/**
 * Class representing the x-axis labels settings. Only use the setter methods to
 * modify it. Do not access public variables directly. Be aware that not all
 * features the XLabels class provides are suitable for the RadarChart.
 */
class XAxis(context: Context) : Axis(context) {

    var spaceBetweenAxisLineAndValue = convertDpToPixel(8f, context)
    val highlightValueTextColor = Color.parseColor("#EFF6FF")

    fun computeTextSize(valueTextPaint: Paint): SizeF {
        val label = entries.first().first
        valueTextPaint.textSize = valueTextSize
        val rect = Rect()
        rect.set(0, 0, 0, 0)
        valueTextPaint.getTextBounds(label, 0, label.length, rect)
        return SizeF(rect.width().toFloat(), rect.height().toFloat())
    }
}