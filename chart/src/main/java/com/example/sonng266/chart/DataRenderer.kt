package com.example.sonng266.chart

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.GradientDrawable
import java.lang.ref.WeakReference
import kotlin.math.roundToInt

open class DataRenderer(
    val viewPortHandler: ViewPortHandler,
    val transformer: Transformer,
) : Renderer(viewPortHandler = viewPortHandler) {
    /**
     * Bitmap object used for drawing the paths (otherwise they are too long if
     * rendered directly on the canvas)
     */
    private var drawBitmap: WeakReference<Bitmap>? = null

    /**
     * the bitmap configuration to be used
     */
    private var bitmapConfig = Bitmap.Config.ARGB_8888

    /**
     * on this canvas, the paths are rendered, it is initialized with the
     * pathBitmap
     */
    private var bitmapCanvas: Canvas? = null

    /**
     * main paint object used for rendering
     */
    private var renderPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    protected var cubicPath = Path()
    protected var cubicFillPath = Path()

    init {
        renderPaint.apply {
            style = Paint.Style.STROKE
            color = Color.WHITE
            strokeWidth = 5f
        }
    }

    override fun render(canvas: Canvas) {

        val width = viewPortHandler.chartWidth.roundToInt()
        val height = viewPortHandler.chartHeight.roundToInt()

        var drawBitmap: Bitmap? = this.drawBitmap?.get()
        if (width > 0 && height > 0) {
            drawBitmap = Bitmap.createBitmap(width, height, bitmapConfig)
            this.drawBitmap = WeakReference(drawBitmap)
            bitmapCanvas = Canvas(drawBitmap)
        }

        drawBitmap!!.eraseColor(Color.TRANSPARENT)

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

        val intensity = 0.2f
        cubicPath.reset()
        data.forEachIndexed { index, entry ->
            when (index) {
                0 -> {
                    cubicPath.moveTo(entry.first, entry.second)
                }
                else -> {
                    val prevprev = data[(index - 2).takeIf { it >= 0 } ?: 0]
                    val prev = data[index - 1]
                    val cur = entry
                    val next = data[(index + 1).takeIf { it < data.size } ?: (data.size - 1)]

                    val prevDx = (cur.first - prevprev.first) * intensity
                    val prevDy = (cur.second - prevprev.second) * intensity
                    val curDx = (next.first - prev.first) * intensity
                    val curDy = (next.second - prev.second) * intensity

                    cubicPath.cubicTo(
                        prev.first + prevDx, prev.second + prevDy,
                        cur.first - curDx,
                        cur.second - curDy, cur.first, cur.second
                    )
                }
            }
        }

        // Filled
        val drawable = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(Color.parseColor("#802275D3"), Color.parseColor("#00FFFFFF"))
        )
        cubicFillPath.rewind()
        cubicFillPath.addPath(cubicPath)
        cubicFillPath.lineTo(data[data.size - 1].first, 0f)
        cubicFillPath.lineTo(data[0].first, 0f)
        cubicFillPath.close()
        transformer.pathValueToPixel(cubicFillPath)
        val save = canvas.save()
        canvas.clipPath(cubicFillPath)
        drawable.setBounds(
            viewPortHandler.contentLeft().roundToInt(),
            viewPortHandler.contentTop().roundToInt(),
            viewPortHandler.contentRight().roundToInt(),
            viewPortHandler.contentBottom().roundToInt(),
        )
        drawable.draw(canvas)
        canvas.restoreToCount(save)

        // draw everything
        transformer.pathValueToPixel(cubicPath)
        bitmapCanvas!!.drawPath(cubicPath, renderPaint)
        canvas.drawBitmap(drawBitmap, 0f, 0f, renderPaint)
    }

    /**
     * Releases the drawing bitmap. This should be called when {@link ChartView#onDetachedFromWindow()}.
     */
    fun releaseBitmap() {
        bitmapCanvas?.setBitmap(null)
        bitmapCanvas = null
        drawBitmap?.get()?.recycle()
        drawBitmap?.clear()
        drawBitmap = null
    }
}

