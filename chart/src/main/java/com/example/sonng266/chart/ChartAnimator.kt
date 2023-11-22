package com.example.sonng266.chart

import android.animation.ValueAnimator

class ChartAnimator(private val listener: ValueAnimator.AnimatorUpdateListener) {
    /** The phase of drawn values on the y-axis. 0 - 1  */
    var phaseY = 1f

    /** The phase of drawn values on the x-axis. 0 - 1  */
    var phaseX = 1f
}