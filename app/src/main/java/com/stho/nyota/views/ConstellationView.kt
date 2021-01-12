package com.stho.software.nyota.views

import android.content.Context
import android.util.AttributeSet
import com.stho.nyota.sky.universe.Constellation
import com.stho.nyota.sky.universe.Star
import com.stho.nyota.sky.utilities.Topocentric
import com.stho.nyota.sky.utilities.projections.Projection
import com.stho.nyota.ui.sky.*
import com.stho.nyota.views.AbstractSkyView

/**
 * Created by shoedtke on 23.09.2016.
 * see: https://developer.android.com/training/gestures/scale
 */
class ConstellationView(context: Context?, attrs: AttributeSet?) : AbstractSkyView(context, attrs) {

    private var constellation: Constellation? = null
    private var star: Star? = null

    init {
        setOptions(object: SkyViewOptions(this@ConstellationView) {
            override var displaySymbols: Boolean = true
            override var displayMagnitude: Boolean = false
            override var displayConstellations: Boolean = true
            override var displayConstellationNames: Boolean = false
            override var displayPlanetNames: Boolean = false
            override var displayStarNames: Boolean = false
            override var displayTargets: Boolean = false
            override var displaySatellites: Boolean = false
            override var sphereProjection: Projection = Projection.SPHERE
        })
    }

    fun setConstellation(constellation: Constellation) {
        this.constellation = constellation
        setCenter(constellation.position!!)
        invalidate()
    }

    fun setStar(star: Star?) {
        this.star = star
    }

    fun notifyDataSetChanged() {
        invalidate()
    }

    override val referencePosition: Topocentric?
        get() = constellation?.position

    override fun onDrawElements() {
        constellation?.let {
            drawConstellation(it)
        }
        star?.let {
            drawStarAsReference(it)
        }
    }
}
