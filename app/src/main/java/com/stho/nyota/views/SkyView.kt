package com.stho.software.nyota.views

import android.content.Context
import android.util.AttributeSet
import com.stho.nyota.sky.universe.*
import com.stho.nyota.sky.utilities.Topocentric
import com.stho.nyota.ui.sky.ISkyViewSettings
import com.stho.nyota.ui.sky.SkyFragmentViewOptions
import com.stho.nyota.views.AbstractSkyView
import com.stho.nyota.views.ReferenceType

/**
 * Created by shoedtke on 07.09.2016.
 */
class SkyView(context: Context?, attrs: AttributeSet?) : AbstractSkyView(context, attrs) {

    private var universe: Universe? = null
    private var tippedElement: IElement? = null
    private var referenceElement: IElement? = null
    private var tippedConstellation: IElement? = null

    fun notifyDataSetChanged() {
        invalidate()
    }

    fun setOptions(settings: ISkyViewSettings) =
        super.setOptions(SkyFragmentViewOptions(this@SkyView, settings))

    fun setUniverse(universe: Universe) {
        this.universe = universe
        invalidate()
    }

    fun setTippedElement(element: IElement?) {
        this.tippedElement = element
        invalidate()
    }

    fun setTippedConstellation(element: IElement?) {
        this.tippedConstellation = element
        invalidate()
    }

    fun setReferenceElement(element: IElement?, updateCenter: Boolean = true) {
        this.referenceElement = element
        if (updateCenter) {
            setCenter(element?.position)
        }
        invalidate()
    }

    override val referencePosition: Topocentric?
        get() = referenceElement?.position

    override fun onDrawElements() {
        universe?.let {
            onDrawUniverse(it)
        }
        tippedConstellation?.let {
            onDrawTippedElement(it)
        }
        referenceElement?.let {
            onDrawReferenceElement(it)
        }
        tippedElement?.let {
            onDrawTippedElement(it)
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

    private fun onDrawTippedElement(element: IElement) {
        when (element) {
            is Star -> super.drawStar(element, ReferenceType.Reference)
            is Constellation -> super.drawConstellation(element, ReferenceType.Tipped)
        }
    }

    private fun onDrawReferenceElement(element: IElement) {
        when (element) {
            is Star -> super.drawStar(element, ReferenceType.Reference)
            is Constellation -> super.drawConstellation(element, ReferenceType.Reference)
        }
    }

}



