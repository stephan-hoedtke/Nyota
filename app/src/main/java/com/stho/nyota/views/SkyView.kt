package com.stho.software.nyota.views

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import com.stho.nyota.sky.universe.IElement
import com.stho.nyota.sky.universe.Universe
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

    protected override fun onDrawElements(canvas: Canvas, zoom: Double) {
        if (universe != null) {
            drawUniverse(canvas, zoom, universe!!)
        }
    }

    private fun drawUniverse(canvas: Canvas, zoom: Double, universe: Universe) {
        for (special in universe.specials) {
            super.drawName(canvas, zoom, special)
        }
        for (constellation in universe.constellations.values) {
            super.drawConstellation(canvas, zoom, constellation)
            if (super.displayNames) {
                super.drawName(canvas, zoom, constellation)
            }
        }
        for (star in universe.vip) {
            super.drawStar(canvas, zoom, star)
            if (super.displayNames) {
                super.drawName(canvas, zoom, star)
            }
        }
        for (planet in universe.solarSystem.planets) {
            super.drawPlanet(canvas, zoom, planet)
            if (super.displayNames) {
                super.drawName(canvas, zoom, planet)
            }
        }
        for (target in universe.targets.values) {
            super.drawTarget(canvas, zoom, target)
        }
        super.drawMoon(canvas, zoom, universe.solarSystem.moon)
        super.drawSun(canvas, zoom, universe.solarSystem.sun)
        super.drawName(canvas, zoom, universe.zenit, "Z")
    }
}