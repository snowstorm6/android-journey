package com.example.sonng266.chart

import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

class ChartTouchListener(
    private val chart: ChartView
) : GestureDetector.SimpleOnGestureListener(), View.OnTouchListener {

    private val gestureDetector = GestureDetector(chart.context, this)

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when (event?.action?.and(MotionEvent.ACTION_MASK)) {
            MotionEvent.ACTION_MOVE -> {
                chart.getHighlightByTouchPoint(event.x, event.y)
            }

            else -> Unit // do nothing
        }
        return true
    }
}