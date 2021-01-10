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

    override lateinit var options: ISkyViewOptions

    fun notifyDataSetChanged() {
        invalidate()
    }

    fun setOptions(settings: ISkyViewSettings) {
        options = SkyFragmentViewOptions(this@SkyView, settings)
    }

    fun setUniverse(universe: Universe) {
        this.universe = universe
        invalidate()
    }

    fun setElement(element: IElement?) {
        this.element = element
        setCenter(element?.position)
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
        for (planet in universe.solarSystem.planets) {
            super.drawPlanet(planet)
        }
        for (target in universe.targets.values) {
            super.drawTarget(target)
        }
        super.drawMoon(universe.solarSystem.moon)
        super.drawSun(universe.solarSystem.sun)
        super.drawName(universe.zenit, "Z")
    }

    private fun onDrawElement(element: IElement) {
        when (element) {
            is Star -> super.drawStarAsReference(element)
            is Constellation -> super.drawConstellationAsReference(element)
        }
    }
}



