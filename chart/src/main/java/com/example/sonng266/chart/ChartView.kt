package com.example.sonng266.chart

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.core.view.children
import kotlin.math.abs
import kotlin.math.hypot

class ChartView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {


    /**
     * object that manages the bounds and drawing constraints of the chart
     */
    private val viewPortHandler = ViewPortHandler()

    /**
     * the object representing the labels on the left y-axis
     */
    var yAxis: YAxis = YAxis(context)

    /**
     * the object representing the labels on the bottom x-axis
     */
    val xAxis: XAxis = XAxis(context).apply {
        entries = arrayOf(
            "T11" to 10f,
            "T12" to 20f,
            "T1" to 30f,
            "T2" to 40f,
            "T3" to 50f,
            "T4" to 60f,
            "T5" to 70f,
            "T6" to 80f,
            "T7" to 90f,
            "T8" to 100f,
            "T9" to 110f,
            "T10" to 120f,
        )
    }

    val data = arrayOf(
        Pair(10f, 82f),
        Pair(20f, 150f),
        Pair(30f, 200f),
        Pair(40f, 65f),
        Pair(50f, 60f),
        Pair(60f, 75f),
        Pair(70f, 84f),
        Pair(80f, 100f),
        Pair(90f, 83f),
        Pair(100f, 79f),
        Pair(110f, 240f),
        Pair(120f, 79f),
    )

    private var highlightIndex: Int = data.size - 1

    private val touchListener = ChartTouchListener(this)

    private val transformer = Transformer(viewPortHandler = viewPortHandler)


    private val xAxisRenderer = XAxisRenderer(
        viewPortHandler = viewPortHandler,
        axis = xAxis,
        transformer = transformer
    )
    private val yAxisRenderer = YAxisRenderer(
        viewPortHandler = viewPortHandler,
        axis = yAxis,
        transformer = transformer
    )
    private val highlightRenderer = HighlightRenderer(
        viewPortHandler = viewPortHandler,
        transformer = transformer
    )

    private val dataRenderer =
        DataRenderer(transformer = transformer, viewPortHandler = viewPortHandler)

    init {
        setWillNotDraw(false)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null) return
        xAxisRenderer.render(canvas = canvas)
        yAxisRenderer.render(canvas = canvas)
        dataRenderer.render(canvas = canvas)
        highlightRenderer.render(canvas = canvas)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        children.forEach {
            it.layout(left, top, right, bottom)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        if (w > 0 && h > 0) {
            viewPortHandler.setDimens(width = w.toFloat(), height = height.toFloat())
        }

        // This may cause the chart view to mutate properties affecting the view port --
        // lets do this before we try to run any pending jobs on the view port itself
        notifyDataSetChanged()

        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return touchListener.onTouch(this, event)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        dataRenderer.releaseBitmap()
    }

    fun updateXAxis(shouldRedraw: Boolean = false, update: (Axis) -> Unit) {
        xAxis.apply(update)
        if (shouldRedraw) invalidate()
    }

    fun updateYAxis(shouldRedraw: Boolean = false, update: (Axis) -> Unit) {
        yAxis.apply(update)
        if (shouldRedraw) invalidate()
    }

    fun redraw() {
        invalidate()
    }

    /**
     * Lets the chart know its underlying data has changed and performs all
     * necessary recalculations. It is crucial that this method is called
     * everytime data is changed dynamically. Not calling this method can lead
     * to crashes or unexpected behaviour.
     */
    private fun notifyDataSetChanged() {
        calcMinMax()
        yAxisRenderer.computeAxis(0f, 243f)
        calculateOffsets()
    }

    /**
     * Calculates the offsets of the chart to the border depending on the
     * position of an eventual legend or depending on the length of the y-axis
     * and x-axis labels and their position
     */
    private fun calculateOffsets() {
        // offsets for y-labels
        val offsetLeft =
            yAxis.computeMaximumValueTextSize(valueTextPaint = yAxisRenderer.axisLabelPaint).width +
                    yAxis.spaceBetweenAxisLineAndValue

        // offsets for x-labels
        val offsetBottom =
            xAxis.computeTextSize(valueTextPaint = xAxisRenderer.axisLabelPaint).height +
                    xAxis.spaceBetweenAxisLineAndValue

        viewPortHandler.restrainViewPort(
            offsetLeft = offsetLeft,
            offsetTop = 0f,
            offsetBottom = offsetBottom,
            offsetRight = 0f
        )

        transformer.prepareMatrixOffset()
        transformer.prepareMatrixValuePx(
            xChartMin = xAxis.axisMinimum,
            deltaX = xAxis.axisRange,
            deltaY = yAxis.axisRange,
            yChartMin = yAxis.axisMinimum
        )
    }

    /**
     * Calculates the y-min and y-max value and the y-delta and x-delta value
     */
    private fun calcMinMax() {
        xAxis.calculate(dataMin = 10f, dataMax = 120f)
        yAxis.calculate(dataMin = 0f, dataMax = 243f)
    }

    /**
     * Returns the Highlight object (contains x-index and DataSet index)
     *
     * @param x
     * @param y
     * @return
     */
    fun getHighlightByTouchPoint(x: Float, y: Float) {
        val xVal = transformer.getValuesByTouchPoint(x, y).first
        val result = data.minBy { abs(xVal - it.first) }
        if (result.first != highlightRenderer.position?.first) {
            highlightRenderer.position = result
            invalidate()
        }
    }


    /**
//     * Returns the corresponding Highlight for a given xVal and x- and y-touch position in pixels.
//     *
//     * @param xVal
//     * @param x
//     * @param y
//     * @return
//     */
//    protected fun getHighlightForX(
//        xVal: Float,
//        x: Float,
//        y: Float
//    ): Pair<Float, Float>? {
//        val closestValues: List<Pair<Float, Float>> = getHighlightsAtXValue(xVal, x, y)
//    }
//
//    /**
//     * Returns a list of Highlight objects representing the entries closest to the given xVal.
//     * The returned list contains two objects per DataSet (closest rounding up, closest rounding down).
//     *
//     * @param xVal the transformed x-value of the x-touch position
//     * @param x    touch position
//     * @param y    touch position
//     * @return
//     */
//    protected fun getHighlightsAtXValue(xVal: Float, x: Float, y: Float): List<Pair<Float, Float>>? {
//
//        return mHighlightBuffer
//    }
}