package com.stho.software.nyota.views

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import com.stho.nyota.sky.universe.Constellation
import com.stho.nyota.sky.utilities.Topocentric
import com.stho.nyota.views.AbstractSkyView

/**
 * Created by shoedtke on 23.09.2016.
 * see: https://developer.android.com/training/gestures/scale
 */
class ConstellationView(context: Context?, attrs: AttributeSet?) : AbstractSkyView(context, attrs) {

    private var constellation: Constellation? = null

    fun setConstellation(constellation: Constellation) {
        this.constellation = constellation
        setCenter(constellation.position!!)
        invalidate()
    }

    override val referencePosition: Topocentric?
        get() = constellation?.position

    override fun onDrawElements(canvas: Canvas, zoom: Double) {
        if (constellation != null) {
            drawConstellation(canvas, zoom, constellation!!)
        }
    }
}