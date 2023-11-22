package com.example.sonng266.chart

import android.graphics.Canvas
import android.graphics.Path
import android.graphics.Typeface

class XAxisRenderer(
    axis: XAxis,
    viewPortHandler: ViewPortHandler,
    transformer: Transformer
) : AxisRenderer(axis = axis, viewPortHandler = viewPortHandler, transformer = transformer) {

    private val renderGridLinePath = Path()
    private var gridLineHeight = 12f
    private val xAxis get() = axis as XAxis

    override fun renderAxisValuesLabel(canvas: Canvas) {
        super.renderAxisValuesLabel(canvas)
        val positions = FloatArray(axis.entries.size * 2)
        for (i in positions.indices step 2) {
            positions[i] = axis.entries[i / 2].second
            positions[i + 1] = 0f
        }

        transformer.pointValuesToPixel(positions)

        for (i in positions.indices step 2) {
            val label = axis.entries[i / 2].first
            val labelSize = xAxis.computeTextSize(axisLabelPaint)
            val x = positions[i] - labelSize.width / 2
            val y = positions[i + 1] + labelSize.height + xAxis.spaceBetweenAxisLineAndValue
            if (i != positions.size - 2) {
                axisLabelPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                axisLabelPaint.color = axis.valueTextColor
                canvas.drawText(label, x, y, axisLabelPaint)
            } else {
                // is the last value
                axisLabelPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                axisLabelPaint.color = xAxis.highlightValueTextColor
                canvas.drawText(label, x, y, axisLabelPaint)
            }
        }
    }

    override fun renderGridLines(canvas: Canvas) {
        super.renderGridLines(canvas)

        val positions = FloatArray(axis.entries.size * 2)
        for (i in positions.indices step 2) {
            positions[i] = axis.entries[i / 2].second
            positions[i + 1] = 0f
        }

        transformer.pointValuesToPixel(positions)

        for (i in positions.indices step 2) {
            drawGridLine(canvas = canvas, x = positions[i], y = positions[i + 1])
        }
    }

    override fun renderAxisLine(canvas: Canvas) {
        super.renderAxisLine(canvas)
        canvas.drawLine(
            viewPortHandler.contentLeft(),
            viewPortHandler.contentBottom(),
            viewPortHandler.contentRight(),
            viewPortHandler.contentBottom(),
            axisLinePaint
        )
    }

    /**
     * Draws the grid line at the specified position using the provided path.
     *
     * @param canvas
     * @param x
     * @param gridLinePath
     */
    private fun drawGridLine(canvas: Canvas, x: Float, y: Float) {
        renderGridLinePath.reset()
        renderGridLinePath.moveTo(x, y)
        renderGridLinePath.lineTo(x, y - gridLineHeight)

        canvas.drawPath(renderGridLinePath, gridLinePaint)
    }
}