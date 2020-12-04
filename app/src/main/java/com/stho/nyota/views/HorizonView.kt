package com.stho.software.nyota.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.stho.nyota.sky.utilities.Angle
import com.stho.nyota.sky.utilities.AverageOrientation
import com.stho.nyota.sky.utilities.Orientation
import com.stho.nyota.sky.utilities.Topocentric
import kotlin.math.cos
import kotlin.math.sin

/**
 * Created by shoedtke on 04.10.2016.
 */
class HorizonView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    init {
        onCreate()
    }

    private val rect = RectF()

    var targetAltitude = -17.0
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

    var flat: Boolean = true
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }

    private fun onCreate() {
        pen.color = Color.WHITE
        pen.style = Paint.Style.STROKE
        pen.strokeWidth = 5f
        blue.color = Color.rgb(40, 60, 170)
        blue.style = Paint.Style.FILL
        green.color = Color.GREEN
        green.alpha = 200
        green.style = Paint.Style.FILL_AND_STROKE
        green.isAntiAlias = true
        scale.color = Color.GREEN
        scale.alpha = 200
        scale.style = Paint.Style.STROKE
        scale.isAntiAlias = true
        scale.strokeWidth = 6f
        circle.color = Color.GREEN
        circle.style = Paint.Style.STROKE
        circle.strokeWidth = 2f
    }

    @Deprecated(message = "set both targetAzimuth and currentDeviceOrientation directly")
    fun setDirection(position: Topocentric, orientation: Orientation) {
        this.targetAltitude = position.altitude
        this.currentDeviceOrientation = orientation
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width
        val h = height
        val t = Math.min(w, h).toFloat()
        val r = t / 2.9f
        val a = t / 3.5f
        val b = t / 9.5f
        val middle = t / 2.6f
        val outer = t / 2.2f
        val alpha: Float = 0 - currentDeviceOrientation.roll.toFloat()
        val beta: Float = if (flat) currentDeviceOrientation.direction.toFloat() else currentDeviceOrientation.pitch.toFloat()
        var phi: Float = Angle.getAngleDifference(targetAltitude.toFloat(), beta)
        if (phi > 90) phi = 90f
        if (phi < -90) phi = -90f
        canvas.translate(w / 2.toFloat(), h / 2.toFloat())
        canvas.rotate(alpha)
        rect[-r, -r, r] = r
        path.reset()
        path.addArc(rect, 180 + phi, 180 - 2 * phi)
        path.close()
        canvas.drawPath(path, blue)
        canvas.drawCircle(0f, 0f, r, circle)
        for (p in dots) {
            val x =
                (middle * cos(Math.toRadians(p.toDouble()))).toFloat()
            val y =
                (middle * sin(Math.toRadians(p.toDouble()))).toFloat()
            canvas.drawCircle(x, -y, 4f, green)
        }
        path.reset()
        path.moveTo(-middle, 0f)
        path.lineTo(-outer, 0f)
        path.moveTo(middle, 0f)
        path.lineTo(outer, 0f)
        path.moveTo(0f, -middle)
        path.lineTo(0f, -outer)
        canvas.drawPath(path, scale)
        canvas.rotate(0 - alpha)
        rect[-b, -b, b] = b
        path.reset()
        path.moveTo(-a, 0f)
        path.lineTo(-b, 0f)
        path.addArc(rect, 180f, -180f)
        path.lineTo(a, 0f)
        canvas.drawPath(path, pen)
        canvas.drawCircle(0f, 0f, 5f, pen)
    }

    companion object {
        private val pen = Paint()
        private val green = Paint()
        private val scale = Paint()
        private val blue = Paint()
        private val circle = Paint()
        private val path = Path()
        private val dots = intArrayOf(15, 30, 45, 60, 75, 105, 120, 135, 150, 165)
    }
}

