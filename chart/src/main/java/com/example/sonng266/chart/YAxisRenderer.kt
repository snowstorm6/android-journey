package com.example.sonng266.chart

import android.graphics.Canvas
import android.graphics.Path

class YAxisRenderer(
    axis: YAxis,
    viewPortHandler: ViewPortHandler,
    transformer: Transformer
) : AxisRenderer(axis = axis, viewPortHandler = viewPortHandler, transformer = transformer) {

    private val renderGridLinePath = Path()
    private val yAxis get() = axis as YAxis

    override fun renderAxisValuesLabel(canvas: Canvas) {
        super.renderAxisValuesLabel(canvas)
        val positions = FloatArray(axis.entries.size * 2)
        for (i in positions.indices step 2) {
            positions[i] = 0f
            positions[i + 1] = axis.entries[i / 2].second
        }

        transformer.pointValuesToPixel(positions)

        for (i in positions.indices step 2) {
            val label = axis.entries[i / 2].first
            val labelSize = yAxis.computeTextSize(axisLabelPaint, label)
            val x = viewPortHandler.contentLeft() -
                    labelSize.width - yAxis.spaceBetweenAxisLineAndValue

            val y = if (i == 0) {
                positions[i + 1]
            } else {
                positions[i + 1] + labelSize.height / 2
            }

            canvas.drawText(label, x, y, axisLabelPaint)
        }
    }

    override fun renderGridLines(canvas: Canvas) {
        super.renderGridLines(canvas)
        val positions = FloatArray(axis.entries.size * 2)
        for (i in positions.indices step 2) {
            positions[i] = 0f
            positions[i + 1] = axis.entries[i / 2].second
        }

        transformer.pointValuesToPixel(positions)

        for (i in positions.indices step 2) {
            renderGridLinePath.reset()
            renderGridLinePath.moveTo(viewPortHandler.contentLeft(), positions[i + 1])
            renderGridLinePath.lineTo(viewPortHandler.contentRight() - viewPortHandler.extraWidth, positions[i + 1])

            if (i != 0) {
                canvas.drawPath(renderGridLinePath, gridLinePaint)
            }
        }
    }

    override fun renderAxisLine(canvas: Canvas) {
        super.renderAxisLine(canvas)
        canvas.drawLine(
            viewPortHandler.contentLeft(),
            viewPortHandler.contentTop(),
            viewPortHandler.contentLeft(),
            viewPortHandler.contentBottom(),
            axisLinePaint
        )
    }
}