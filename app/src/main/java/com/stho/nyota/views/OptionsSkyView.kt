package com.stho.software.nyota.views

import android.content.Context
import android.util.AttributeSet
import com.stho.nyota.sky.utilities.Formatter
import com.stho.nyota.sky.utilities.Topocentric
import com.stho.nyota.views.AbstractSkyView
import com.stho.nyota.views.LuminosityCalculator

/**
 * Created by shoedtke on 07.09.2016.
 */
class OptionsSkyView(context: Context?, attrs: AttributeSet?) : AbstractSkyView(context, attrs) {

    override val referencePosition: Topocentric =
        Topocentric(0.0, 0.0)

    override fun onDrawElements() {
        draw(14.0, -0.72)
        draw(10.0, 0.03)
        draw(6.0, 0.85)
        draw(2.0, 0.98)
        draw(-2.0, 2.02)
        draw(-6.0, 4.36)
        draw(-10.0, 5.55)
        draw(-14.0, 7.77)
    }

    private fun draw(altitude: Double, magnitude: Double) {
        val calculator = LuminosityCalculator.create(zoomAngle, options)
        val luminosity = calculator.calculate(magnitude)
        val fullName = "${Formatter.df2.format(magnitude)}:  α=${Formatter.df3.format(luminosity.alpha)}  r=${Formatter.df3.format(luminosity.radius)}"
        drawElement(Topocentric(-20.0, altitude), fullName, luminosity)
    }
}



