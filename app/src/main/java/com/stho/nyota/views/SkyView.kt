package com.stho.software.nyota.views

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import com.stho.nyota.sky.universe.*
import com.stho.nyota.sky.utilities.Topocentric
import com.stho.nyota.views.AbstractSkyView
import java.text.FieldPosition

/**
 * Created by shoedtke on 07.09.2016.
 */
class SkyView(context: Context?, attrs: AttributeSet?) : AbstractSkyView(context, attrs) {

    private var universe: Universe? = null
    private var referenceElement: IElement? = null

    fun notifyDataSetChanged() {
        invalidate()
    }

    fun setUniverse(universe: Universe) {
        this.universe = universe
        invalidate()
    }

    fun setReferenceElement(element: IElement?) {
        this.referenceElement = element
        setCenter(element?.position)
        invalidate()
    }

    override val referencePosition: Topocentric?
        get() = referenceElement?.position

    // TODO: mode to draw the universe with some alpha, and highlight a selected element
    // TODO: mode to change colors, display text, display ...

    override fun onDrawElements(canvas: Canvas, zoom: Double) {
        if (universe != null) {
            drawUniverse(canvas, zoom, universe)
        } else {
            drawReferencedElement(canvas, zoom, referenceElement)
        }
    }

    private fun drawUniverse(canvas: Canvas, zoom: Double, universe: Universe?) {
        universe?.also {
            for (special in it.specials) {
                super.drawName(canvas, zoom, special)
            }
            for (constellation in it.constellations.values) {
                super.drawConstellation(canvas, zoom, constellation)
                if (super.displayNames) {
                    super.drawName(canvas, zoom, constellation)
                }
            }
            for (star in it.vip) {
                super.drawStar(canvas, zoom, star)
                if (super.displayNames) {
                    super.drawName(canvas, zoom, star)
                }
            }
            for (planet in it.solarSystem.planets) {
                super.drawPlanet(canvas, zoom, planet)
                if (super.displayNames) {
                    super.drawName(canvas, zoom, planet)
                }
            }
            for (target in it.targets.values) {
                super.drawTarget(canvas, zoom, target)
            }
            super.drawMoon(canvas, zoom, it.solarSystem.moon)
            super.drawSun(canvas, zoom, it.solarSystem.sun)
            super.drawName(canvas, zoom, it.zenit, "Z")
        }
    }

    private fun drawReferencedElement(canvas: Canvas, zoom: Double, element: IElement?) {
        element?.also {
            when (it) {
                is Constellation -> super.drawConstellation(canvas, zoom, it)
                is AbstractPlanet -> super.drawPlanet(canvas, zoom, it)
            }
        }
    }
}

