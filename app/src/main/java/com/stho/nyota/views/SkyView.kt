package com.stho.software.nyota.views

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import com.stho.nyota.sky.universe.*
import com.stho.nyota.sky.utilities.Topocentric
import com.stho.nyota.ui.sky.ISkyViewOptions
import com.stho.nyota.ui.sky.ISkyViewSettings
import com.stho.nyota.ui.sky.SkyFragmentViewOptions
import com.stho.nyota.views.AbstractSkyView

/**
 * Created by shoedtke on 07.09.2016.
 */
class SkyView(context: Context?, attrs: AttributeSet?) : AbstractSkyView(context, attrs) {

    private var universe: Universe? = null
    private var element: IElement? = null

    fun notifyDataSetChanged() {
        invalidate()
    }

    fun setOptions(settings: ISkyViewSettings) =
        super.setOptions(SkyFragmentViewOptions(this@SkyView, settings))

    fun setUniverse(universe: Universe) {
        this.universe = universe
        invalidate()
    }

    fun setElement(element: IElement?, updateCenter: Boolean = true) {
        this.element = element
        if (updateCenter) {
            setCenter(element?.position)
        }
        invalidate()
    }

    override val referencePosition: Topocentric?
        get() = element?.position

    // TODO: mode to draw the universe with some alpha, and highlight a selected element
    // TODO: mode to change colors, display text, display ...

    override fun onDrawElements() {
        universe?.let {
            onDrawUniverse(it)
        }
        element?.let {
            onDrawElement(it)
        }
    }

    private fun onDrawUniverse(universe: Universe) {
        for (special in universe.specials) {
            super.drawSpecial(special)
        }
        for (constellation in universe.constellations.values) {
            super.drawConstellation(constellation)
        }
        for (star in universe.vip) {
            super.drawStar(star)
        }
        for (star in universe.extra) {
            super.drawStar(star)
        }
        for (planet in universe.solarSystem.planets) {
            super.drawPlanet(planet)
        }
        if (options.displayTargets) {
            for (target in universe.targets.values) {
                super.drawTarget(target)
            }
        }
        if (options.displaySatellites) {
            for (satellite in universe.satellites.values) {
                super.drawSatellite(satellite)
            }
        }
        super.drawMoon(universe.solarSystem.moon)
        super.drawSun(universe.solarSystem.sun)
        super.drawZenit(universe.zenit)
    }

    private fun onDrawElement(element: IElement) {
        when (element) {
            is Star -> super.drawStarAsReference(element)
            is Constellation -> super.drawConstellationAsReference(element)
        }
    }
}



