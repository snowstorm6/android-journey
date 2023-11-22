package com.example.sonng266.chart

import android.graphics.RectF

class ViewPortHandler {

    private val _contentRect = RectF()
    val contentRect get() = _contentRect

    var chartWidth: Float = 0f
        private set

    var chartHeight: Float = 0f
        private set

    var extraWidth = 40f
    var extraHeight = 80f

    fun setDimens(width: Float, height: Float) {
        chartWidth = width
        chartHeight = height

        val offsetLeft: Float = this.offsetLeft()
        val offsetTop: Float = this.offsetTop()
        val offsetRight: Float = this.offsetRight()
        val offsetBottom: Float = this.offsetBottom()

        restrainViewPort(offsetLeft, offsetTop, offsetRight, offsetBottom)
    }

    fun restrainViewPort(
        offsetLeft: Float, offsetTop: Float, offsetRight: Float, offsetBottom: Float
    ) {
        contentRect.set(offsetLeft, offsetTop, chartWidth - offsetRight, chartHeight - offsetBottom)
    }

    fun offsetLeft(): Float {
        return contentRect.left
    }

    fun offsetRight(): Float {
        return chartWidth - contentRect.right
    }

    fun offsetTop(): Float {
        return contentRect.top
    }

    fun offsetBottom(): Float {
        return chartHeight - contentRect.bottom
    }

    fun contentTop(): Float {
        return contentRect.top
    }

    fun contentLeft(): Float {
        return contentRect.left
    }

    fun contentRight(): Float {
        return contentRect.right
    }

    fun contentBottom(): Float {
        return contentRect.bottom
    }

    fun contentWidth(): Float {
        return contentRect.width()
    }

    fun contentHeight(): Float {
        return contentRect.height()
    }
}