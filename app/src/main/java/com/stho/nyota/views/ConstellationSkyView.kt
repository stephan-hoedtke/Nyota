package com.stho.software.nyota.views

import android.content.Context
import android.util.AttributeSet
import com.stho.nyota.sky.universe.Constellation
import com.stho.nyota.sky.universe.Star
import com.stho.nyota.sky.utilities.Topocentric
import com.stho.nyota.views.AbstractSkyView
import com.stho.nyota.views.ReferenceType

/**
 * Created by shoedtke on 23.09.2016.
 * see: https://developer.android.com/training/gestures/scale
 */
class ConstellationSkyView(context: Context?, attrs: AttributeSet?) : AbstractSkyView(context, attrs) {

    private var constellation: Constellation? = null
    private var tippedStar: Star? = null

    fun setConstellation(constellation: Constellation) {
        this.constellation = constellation
        setCenter(constellation.position!!)
        invalidate()
    }

    fun setTippedStar(star: Star?) {
        this.tippedStar = star
        invalidate()
    }

    fun notifyDataSetChanged() {
        invalidate()
    }

    override val referencePosition: Topocentric?
        get() = constellation?.position

    override fun onDrawElements() {
        constellation?.let {
            drawConstellation(it, ReferenceType.Default)
        }
        tippedStar?.let {
            drawStar(it, ReferenceType.TippedStar)
        }
        tippedPosition?.let {
            drawSensitivityArea(it)
        }
    }
}
