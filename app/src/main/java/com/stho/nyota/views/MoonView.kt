package com.stho.software.nyota.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import com.stho.nyota.sky.universe.Moon
import kotlin.math.abs
import kotlin.math.min

/**
 * Created by shoedtke on 23.09.2016.
 */
class MoonView(context: Context, attrs: AttributeSet) : androidx.appcompat.widget.AppCompatImageView(context, attrs, 0) {
    private var phase = 0.2 // Percentage of illumination
    private var phaseAngle = 30.0 // Angle of the illuminated limb, from north to east
    private var rotationAngle = 40.0 // Local sidereal time (to rotate the moons image)
    private val pen = Paint()
    private val path = Path()
    var circle = RectF()
    var ellipse = RectF()

    init {
        onCreate()
    }

    private fun onCreate() {
        pen.color = Color.BLACK
        pen.alpha = 200
        pen.style = Paint.Style.FILL
    }

    fun setPhase(moon: Moon) {
        setPhase(moon.phase, moon.zenithAngle, moon.parallacticAngle)
    }

    fun setPhase(phase: Double, phaseAngle: Double, rotationAngle: Double) {
        this.phase = phase
        this.phaseAngle = phaseAngle
        this.rotationAngle = rotationAngle
        rotation = rotationAngle.toFloat()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()
        val cx = w / 2
        val cy = h / 2
        val r = min(cx, cy)
        val d = r * abs(2 * phase - 1).toFloat()
        val angle = (phaseAngle + rotation).toFloat()
        onDrawPath(canvas, cx, cy, r, d, angle)
    }

    private fun onDrawPath(canvas: Canvas, cx: Float, cy: Float, r: Float, d: Float, angle: Float) {
        canvas.translate(cx, cy)
        canvas.rotate(-angle)
        circle.set(-r, -r, r, r)
        ellipse.set(-r, -d, r, d)
        if (phase > 0.5) {
            path.reset()
            path.addArc(ellipse, 0f, 180f)
            path.addArc(circle, 180f, -180f)
            path.close()
        } else {
            path.reset()
            path.addArc(ellipse, 0f, -180f)
            path.addArc(circle, 180f, -180f)
            path.close()
        }
        canvas.drawPath(path, pen)
    }
}