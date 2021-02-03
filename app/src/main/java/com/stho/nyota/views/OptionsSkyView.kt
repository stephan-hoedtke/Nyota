package com.stho.software.nyota.views

import android.content.Context
import android.util.AttributeSet
import com.stho.nyota.sky.universe.*
import com.stho.nyota.sky.utilities.Topocentric
import com.stho.nyota.ui.sky.ISkyViewSettings
import com.stho.nyota.ui.sky.SkyFragmentViewOptions
import com.stho.nyota.views.AbstractSkyView
import com.stho.nyota.views.Luminosity
import com.stho.nyota.views.ReferenceType

/**
 * Created by shoedtke on 07.09.2016.
 */
class OptionsSkyView(context: Context?, attrs: AttributeSet?) : AbstractSkyView(context, attrs) {

    override val referencePosition: Topocentric =
        Topocentric(0.0, 0.0)

    override fun onDrawElements() {
        drawElement(Topocentric(-10.0, 14.0), "-0.72 - Canopus", Luminosity.create(-0.72, options))
        drawElement(Topocentric(-10.0, 10.0), "+0.03 - Vega", Luminosity.create(0.03, options))
        drawElement(Topocentric(-10.0, 6.0), "+0.85 - Aldebaran", Luminosity.create(0.85, options))
        drawElement(Topocentric(-10.0, 2.0), "+0.98 - Spica", Luminosity.create(0.98, options))
        drawElement(Topocentric(-10.0, -2.0), "+2.02 - Polaris", Luminosity.create(2.02, options))
        drawElement(Topocentric(-10.0, -6.0), "+4.36 - Yildun", Luminosity.create(4.36, options))
        drawElement(Topocentric(-10.0, -10.0), "+5.55 ...", Luminosity.create(5.55, options))
        drawElement(Topocentric(-10.0, -14.0), "+7.77 ...", Luminosity.create(7.77, options))
    }
}



