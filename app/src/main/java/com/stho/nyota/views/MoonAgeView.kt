package com.stho.software.nyota.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.widget.ImageView
import com.stho.nyota.sky.universe.Moon
import com.stho.nyota.sky.utilities.UTC
import kotlinx.android.synthetic.main.fragment_moon.view.*


/**
 * Created by shoedtke on 23.09.2016.
 */
class MoonAgeView : androidx.appcompat.widget.AppCompatImageView {
    private val white = Paint()
    private val red = Paint()
    private val path = Path()
    private var age = 0.0
    private var before = 0.5
    private var after = 0.5

    constructor(context: Context) : super(context) {
        onCreate()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs,0) {
        onCreate()
    }

    private fun onCreate() {
        white.color = Color.WHITE
        white.alpha = 200
        white.style = Paint.Style.STROKE
        red.color = Color.RED
        red.alpha = 200
        red.strokeWidth = 3f
        red.style = Paint.Style.STROKE
        setImageResource(com.stho.nyota.R.drawable.moonly) // altitude := wrap_content, width = match_parent
    }

    fun setAge(moon: Moon) {
        val before = UTC.gapInHours(moon.prevNewMoon!!, moon.fullMoon!!)
        val after = UTC.gapInHours(moon.fullMoon!!, moon.nextNewMoon!!)
        val total = UTC.gapInHours(moon.prevNewMoon!!, moon.nextNewMoon!!)
        setAge(moon.age, before / total, after / total);
    }

    fun setAge(age: Double, before: Double, after: Double) {
        this.age = age
        this.before = before
        this.after = after
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width
        val h = height
        val cy = h / 2
        val l = w - h - h
        val w1 = (l * before).toInt()
        val w2 = (l * after).toInt()
        val c1 = w / 2 - w1
        val c2 = w / 2 + w2
        val r = h / 2.toFloat()
        val p = c1 + age.toFloat() * (w1 + w2)
        canvas.drawCircle(c1.toFloat(), cy.toFloat(), r, white)
        canvas.drawCircle(c2.toFloat(), cy.toFloat(), r, white)
        canvas.drawLine(p, 0f, p, h.toFloat(), red)
    }
}