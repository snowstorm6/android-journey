package com.example.sonng266.chart

import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

/**
 * Baseclass of all axis renderers.
 */
abstract class AxisRenderer(
    val axis: Axis, // axis - this axis renderer works with
    open val viewPortHandler: ViewPortHandler,
    open val transformer: Transformer
) : Renderer(viewPortHandler) {

    /** paint object for the grid lines */
    protected val gridLinePaint: Paint = Paint()

    /** paint for the label values */
    val axisLabelPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    /** paint for the line surrounding the chart */
    protected val axisLinePaint: Paint = Paint()

    init {
        // initialize style for paint of grid lines
        gridLinePaint.apply {
            style = Paint.Style.STROKE
            color = axis.gridLineColor
            strokeWidth = axis.gridLineWidth
            pathEffect = DashPathEffect(floatArrayOf(25f, 25f), 0f)
        }

        // initialize style for paint of axis line
        axisLinePaint.apply {
            style = Paint.Style.STROKE
            color = axis.lineColor
            strokeWidth = axis.lineWidth
        }

        // initialize style for paint of axis label
        axisLabelPaint.apply {
            color = axis.valueTextColor
            textSize = axis.valueTextSize
        }
    }

    override fun render(canvas: Canvas) {
        renderAxisLine(canvas = canvas)
        renderGridLines(canvas = canvas)
        renderAxisValuesLabel(canvas = canvas)
    }

    /**
     * Draws the axis labels to the screen.
     *
     * @param canvas
     */
    protected open fun renderAxisValuesLabel(canvas: Canvas) {
        axisLabelPaint.apply {
            color = axis.valueTextColor
            textSize = axis.valueTextSize
        }
    }

    /**
     * Draws the grid lines belonging to the axis.
     *
     * @param canvas
     */
    protected open fun renderGridLines(canvas: Canvas) {
        gridLinePaint.apply {
            color = axis.gridLineColor
            strokeWidth = axis.gridLineWidth
        }
    }

    /**
     * Draws the line that goes alongside the axis.
     *
     * @param canvas
     */
    protected open fun renderAxisLine(canvas: Canvas) {
        axisLinePaint.apply {
            color = axis.lineColor
            strokeWidth = axis.lineWidth
        }
    }

    /**
     * Computes the axis values.
     *
     * @param min - the minimum value in the data object for this axis
     * @param max - the maximum value in the data object for this axis
     */
    fun computeAxis(min: Float, max: Float) {

        val labelCount: Int = 6
        val range = abs(max - min).toDouble()


        // Find out how much spacing (in y value space) between axis values
        val rawInterval = range / labelCount
        var interval: Double = ChartUtils.roundToNextSignificant(rawInterval).toDouble()

        // Normalize interval
        val intervalMagnitude: Double =
            ChartUtils.roundToNextSignificant(10.0.pow(log10(interval))).toDouble()
        val intervalSigDigit = (interval / intervalMagnitude).toInt()
        if (intervalSigDigit > 5) {
            // Use one order of magnitude higher, to avoid intervals like 0.9 or 90
            // if it's 0.0 after floor(), we use the old value
            interval = if (floor(10.0 * intervalMagnitude) == 0.0) interval
            else floor(10.0 * intervalMagnitude)
        }

        val first = if (interval == 0.0) 0.0 else ceil(min / interval) * interval
        val last = if (interval == 0.0) 0.0 else ChartUtils.nextUp(floor(max / interval) * interval)

        var n = 0
        if (interval != 0.0 && last != first) {
            var f = first
            while (f <= last) {
                ++n
                f += interval
            }
        } else if (last == first && n == 0) {
            n = 1
        }

//        axis.mEntryCount = n

        if (axis.entries.size < n) {
            // Ensure stops contains at least numStops elements.
            axis.entries = Array(n, init = { Pair("0", 0f) })
        }

        var f = first
        var i = 0
        while (i < n) {
            if (f == 0.0) f = 0.0
            axis.entries[i] = Pair("${f.toInt()}", f.toFloat())
            f += interval
            ++i
        }


    }
}