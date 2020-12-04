package com.stho.software.nyota.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import com.stho.nyota.sky.utilities.Orientation
import com.stho.nyota.sky.utilities.Topocentric
import kotlin.math.cos
import kotlin.math.sin

/**
 * Created by shoedtke on 04.10.2016.
 */
class DirectionPointerView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    init {
        onCreate()
    }

    var targetAzimuth: Double = 17.0
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }

    var currentDeviceOrientation: Orientation = Orientation.defaultOrientation
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }

    private fun onCreate() {
        pen1.color = Color.WHITE
        pen1.style = Paint.Style.FILL_AND_STROKE
        pen2.color = Color.LTGRAY
        pen2.style = Paint.Style.FILL_AND_STROKE
        black.color = Color.BLACK
        black.style = Paint.Style.FILL_AND_STROKE
        green.color = Color.GREEN
        green.alpha = 200
        green.style = Paint.Style.FILL_AND_STROKE
        green.isAntiAlias = true
        green.textSize = 50f
    }

    @Deprecated(message = "set both targetAzimuth and currentDeviceOrientation directly")
    fun setDirection(position: Topocentric, orientation: Orientation) {
        this.targetAzimuth = position.azimuth
        this.currentDeviceOrientation = orientation
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width
        val h = height
        val t = Math.min(w, h).toFloat()
        val a = t / 2.7f
        val b = t / 27f
        val c = t / 23f
        val middle = t / 2.6f
        val letter = t / 2.5f
        canvas.translate(w / 2.toFloat(), h / 2.toFloat())
        canvas.rotate(0 - currentDeviceOrientation.azimuth.toFloat())
        for (p in dots) {
            val x =
                (middle * cos(Math.toRadians(p.toDouble()))).toFloat()
            val y =
                (middle * sin(Math.toRadians(p.toDouble()))).toFloat()
            canvas.drawCircle(x, y, 4f, green)
        }
        drawTextCentered(canvas, "N", 0f, -letter, green)
        drawTextCentered(canvas, "E", letter, 0f, green)
        drawTextCentered(canvas, "S", 0f, letter, green)
        drawTextCentered(canvas, "W", -letter, 0f, green)
        canvas.rotate(targetAzimuth.toFloat())
        path.reset()
        path.moveTo(0f, -a)
        path.lineTo(-b, c)
        path.lineTo(0f, c)
        path.close()
        canvas.drawPath(path, pen1)
        path.reset()
        path.moveTo(0f, -a)
        path.lineTo(b, c)
        path.lineTo(0f, c)
        path.close()
        canvas.drawPath(path, pen2)
        canvas.drawCircle(0f, 0f, 7f, black)
    }

    private fun drawTextCentered(canvas: Canvas, text: String, x: Float, y: Float, pen: Paint) {
        val w = pen.measureText(text)
        val h = pen.descent() + pen.ascent()
        canvas.drawText(text, x - w / 2f, y - h / 2f, pen)
    }

    companion object {
        private val pen1 = Paint()
        private val pen2 = Paint()
        private val black = Paint()
        private val green = Paint()
        private val path = Path()
        private val dots = intArrayOf(
            15,
            30,
            45,
            60,
            75,
            105,
            120,
            135,
            150,
            165,
            195,
            210,
            225,
            240,
            255,
            285,
            300,
            315,
            330,
            345
        )
    }
}