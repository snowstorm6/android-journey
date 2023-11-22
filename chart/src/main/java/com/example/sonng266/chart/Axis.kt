package com.example.sonng266.chart

import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorInt
import kotlin.math.abs

/**
 * Base class of all axes
 */
open class Axis(protected val context: Context) {
    companion object {
        private const val DEFAULT_VALUE_TEXT_SIZE = 12f
        private const val DEFAULT_LINE_WIDTH = 1f
        private const val DEFAULT_GRID_LINE_WIDTH = 1f
    }

    var entries: Array<Pair<String, Float>> = emptyArray()

    @ColorInt
    var lineColor: Int = Color.parseColor("#FFFFFF")
    var lineWidth: Float = convertDpToPixel(DEFAULT_LINE_WIDTH, context)

    @ColorInt
    var gridLineColor: Int = Color.parseColor("#FFFFFF")
    var gridLineWidth: Float = convertDpToPixel(DEFAULT_GRID_LINE_WIDTH, context)

    @ColorInt
    var valueTextColor: Int = Color.parseColor("#FFFFFF")
    var valueTextSize: Float = convertDpToPixel(DEFAULT_VALUE_TEXT_SIZE, context)

    var axisMaximum: Float = 0f
        private set

    var axisMinimum: Float = 0f
        private set

    /**
     * the total range of values this axis covers
     */
    var axisRange: Float = 0f
        private set

    /**
     * Calculates the minimum / maximum  and range values of the axis with the given
     * minimum and maximum values from the chart data.
     *
     * @param dataMin the min value according to chart data
     * @param dataMax the max value according to chart data
     */
    fun calculate(dataMin: Float, dataMax: Float) {

        // if custom, use value as is, else use data value
        var min: Float = dataMin
        var max: Float = dataMax

        // temporary range (before calculations)
        val range = abs(max - min)

        // in case all values are equal
        if (range == 0f) {
            max += 1f
            min -= 1f
        }
        axisMinimum = min
        axisMaximum = max

        // actual range
        axisRange = abs(max - min)
    }
}