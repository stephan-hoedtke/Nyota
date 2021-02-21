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

    fun setUniverse(universe: Universe) {
        this.universe = universe
        invalidate()
    }

    fun setTippedElement(element: IElement?) {
        this.tippedElement = element
        invalidate()
    }

    fun removeTippedElement(element: IElement?) {
        if (this.tippedElement == element) {
            this.tippedElement = null
            this.tippedConstellation = null
            invalidate()
        }
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

        drawLight()

        universe?.let {
            onDrawUniverse(it)
        }
        referenceElement?.let {
            onDrawElement(it, ReferenceType.Reference)
        }
        tippedConstellation?.let {
            onDrawElement(it, ReferenceType.TippedConstellation)
        }
        tippedElement?.let {
            onDrawElement(it, ReferenceType.TippedStar)
        }
        tippedPosition?.let {
            drawSensitivityArea(it)
        }
    }

    private fun onDrawUniverse(universe: Universe) {
        if (options.displayEcliptic) {
            super.drawEcliptic(Sun.eclipticFor(universe.moment))
        }
        universe.specials.forEach {
            super.drawNameOf(it)
        }
        universe.any.forEach {
            super.drawNameOf(it)
        }
        universe.constellations.values.forEach {
            super.drawConstellation(it, referenceType = ReferenceType.Default)
        }
        universe.vip.forEach {
            super.drawStar(it, referenceType = ReferenceType.Default)
        }
        universe.galaxies.values.forEach {
            super.drawGalaxy(it, referenceType = ReferenceType.Default)
        }
        universe.solarSystem.planets.forEach {
            super.drawPlanet(it)
        }
        if (options.displayTargets) {
            universe.targets.values.forEach {
                super.drawTarget(it)
            }
        }
        if (options.displaySatellites) {
            universe.satellites.values.forEach {
                super.drawSatellite(it)
            }
        }
        if (options.displayHints) {
            universe.hints.forEach {
                super.drawHint(it)
            }
        }
        super.drawMoon(universe.solarSystem.moon)
        super.drawSun(universe.solarSystem.sun)
        super.drawZenit(universe.zenit)
        super.drawNadir(universe.nadir)
    }

    private fun onDrawElement(element: IElement, referenceType: ReferenceType) {
        when (element) {
            is Star -> super.drawStar(element, referenceType)
            is Constellation -> super.drawConstellation(element, referenceType)
            is Galaxy -> super.drawGalaxy(element, referenceType)
        }
    }
}



