package com.example.sonng266.chart

import kotlin.math.ceil
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToInt

object ChartUtils {
    /**
     * rounds the given number to the next significant number
     *
     * @param number
     * @return
     */
    fun roundToNextSignificant(number: Double): Float {
        if (java.lang.Double.isInfinite(number) ||
            java.lang.Double.isNaN(number) || number == 0.0
        ) return 0f
        val d = ceil(log10(if (number < 0) -number else number).toFloat())
        val pw = 1 - d.toInt()
        val magnitude = 10.0.pow(pw.toDouble()).toFloat()
        val shifted = (number * magnitude).roundToInt()
        return shifted / magnitude
    }

    /**
     * Replacement for the Math.nextUp(...) method that is only available in
     * HONEYCOMB and higher. Dat's some seeeeek sheeet.
     *
     * @param d
     * @return
     */
    fun nextUp(d: Double): Double {
        var d = d
        return if (d == Double.POSITIVE_INFINITY) d else {
            d += 0.0
            java.lang.Double.longBitsToDouble(
                java.lang.Double.doubleToRawLongBits(d) +
                        if (d >= 0.0) +1L else -1L
            )
        }
    }
}