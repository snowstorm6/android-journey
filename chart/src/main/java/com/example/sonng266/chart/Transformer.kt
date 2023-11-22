package com.example.sonng266.chart

import android.graphics.Matrix
import android.graphics.Path

/**
 * Transformer class that contains all matrices and is responsible for
 * transforming values into pixels on the screen and backwards.
 */
class Transformer(private val viewPortHandler: ViewPortHandler) {

    /**
     * matrix to map the values to the screen pixels
     */
    private val matrixValueToPx = Matrix()

    /**
     * matrix for handling the different offsets of the chart
     */
    private val matrixOffset = Matrix()

    /**
     * transform a path with all the given matrices VERY IMPORTANT: keep order
     * to value-touch-offset
     *
     * @param path
     */
    fun pathValueToPixel(path: Path) {
        path.transform(matrixValueToPx)
        path.transform(matrixOffset)
    }

    /**
     * Transform an array of points with all matrices. VERY IMPORTANT: Keep
     * matrix order "value-touch-offset" when transforming.
     *
     * @param positions
     */
    fun pointValuesToPixel(positions: FloatArray) {
        matrixValueToPx.mapPoints(positions)
        matrixOffset.mapPoints(positions)
    }

    /**
     * Prepares the matrix that transforms values to pixels. Calculates the
     * scale factors from the charts size and offsets.
     *
     * @param xChartMin
     * @param deltaX
     * @param deltaY
     * @param yChartMin
     */
    fun prepareMatrixValuePx(xChartMin: Float, deltaX: Float, deltaY: Float, yChartMin: Float) {
        var scaleX = ((viewPortHandler.contentWidth() - viewPortHandler.extraWidth) / deltaX)
        var scaleY = ((viewPortHandler.contentHeight() - viewPortHandler.extraHeight) / deltaY)

        if (java.lang.Float.isInfinite(scaleX)) scaleX = 0f
        if (java.lang.Float.isInfinite(scaleY)) scaleY = 0f

        // setup all matrices
        matrixValueToPx.reset()
        matrixValueToPx.postTranslate(-xChartMin, -yChartMin)
        matrixValueToPx.postScale(scaleX, -scaleY)
    }

    /**
     * Prepares the matrix that contains all offsets.
     */
    fun prepareMatrixOffset() {
        matrixOffset.reset()
        matrixOffset.postTranslate(viewPortHandler.offsetLeft(), viewPortHandler.contentBottom())
    }

    /**
     * Returns a recyclable MPPointD instance.
     * returns the x and y values in the chart at the given touch point
     * (encapsulated in a MPPointD). This method transforms pixel coordinates to
     * coordinates / values in the chart. This is the opposite method to
     * getPixelForValues(...).
     *
     * @param x
     * @param y
     * @return
     */
    fun getValuesByTouchPoint(x: Float, y: Float): Pair<Float, Float> {
        val pts = FloatArray(2)
        pts[0] = x
        pts[1] = y

        pixelsToValue(pts)

        return (Pair(pts[0], pts[1]))
    }

    /**
     * Transforms the given array of touch positions (pixels) (x, y, x, y, ...)
     * into values on the chart.
     *
     * @param pixels
     */
    fun pixelsToValue(pixels: FloatArray) {
        val tmp = Matrix()
        tmp.reset()

        // invert all matrixes to convert back to the original value
        matrixOffset.invert(tmp)
        tmp.mapPoints(pixels)

//        tmp.mapPoints(pixels)
        matrixValueToPx.invert(tmp)
        tmp.mapPoints(pixels)
    }
}