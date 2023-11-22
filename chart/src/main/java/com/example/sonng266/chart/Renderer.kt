package com.example.sonng266.chart

import android.graphics.Canvas

/**
 * Abstract baseclass of all Renderers
 */
abstract class Renderer(private val viewPortHandler: ViewPortHandler) {
    abstract fun render(canvas: Canvas)
}