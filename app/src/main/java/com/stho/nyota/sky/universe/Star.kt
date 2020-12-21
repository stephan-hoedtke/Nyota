package com.stho.nyota.sky.universe

import com.stho.nyota.sky.utilities.Formatter
import com.stho.nyota.sky.utilities.Moment
import com.stho.nyota.sky.utilities.PropertyList

/**
 * Created by shoedtke on 31.08.2016.
 */
class Star internal constructor(override val name: String, val symbol: UniverseInitializer.Symbol, ra: Double, decl: Double, val brightness: Double) : AbstractElement() {

    override val imageId: Int
        get() = com.stho.nyota.R.mipmap.star

    override val largeImageId: Int
        get() = com.stho.nyota.R.drawable.star

    override fun getBasics(moment: Moment): PropertyList =
        super.getBasics(moment).apply {
            add(com.stho.nyota.R.drawable.sunset, "Brightness", Formatter.df2.format(brightness))
            add(UniverseInitializer.greekSymbolImageId(symbol), "Symbol", UniverseInitializer.greekSymbolToString(symbol))
        }

    init {
        RA = ra
        Decl = decl
    }
}