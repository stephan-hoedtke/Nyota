package com.stho.software.nyota.views

import android.content.Context
import android.util.AttributeSet
import com.stho.nyota.settings.Settings.Companion.DEFAULT_MAGNITUDE
import com.stho.nyota.sky.universe.Constellation
import com.stho.nyota.sky.universe.Star
import com.stho.nyota.sky.utilities.Topocentric
import com.stho.nyota.sky.utilities.projections.Projection
import com.stho.nyota.ui.sky.*
import com.stho.nyota.views.AbstractSkyView
import com.stho.nyota.views.ReferenceType

/**
 * Created by shoedtke on 23.09.2016.
 * see: https://developer.android.com/training/gestures/scale
 */
class ConstellationSkyView(context: Context?, attrs: AttributeSet?) : AbstractSkyView(context, attrs) {

    private var constellation: Constellation? = null
    private var referenceStar: Star? = null
    private var tippedStar: Star? = null

    init {
        setOptions(object: SkyViewOptions() {
            override var displaySymbols: Boolean = true
            override var displayConstellations: Boolean = true
            override var displayConstellationNames: Boolean = false
            override var displayPlanetNames: Boolean = false
            override var displayStarNames: Boolean = false
            override var displayTargets: Boolean = false
            override var displaySatellites: Boolean = false
            override var displayGrid: Boolean = false
            override var sphereProjection: Projection = Projection.STEREOGRAPHIC
            override var magnitude: Double = DEFAULT_MAGNITUDE
            override fun touch(): Unit = Unit
        })
    }

    fun setConstellation(constellation: Constellation) {
        this.constellation = constellation
        setCenter(constellation.position!!)
        invalidate()
    }

    fun setReferenceStar(star: Star?) {
        this.referenceStar = star
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
            drawConstellation(it)
        }
        referenceStar?.let {
            drawStar(it, ReferenceType.Reference)
        }
        tippedStar?.let {
            drawStar(it, ReferenceType.TippedStar)
        }
    }
}