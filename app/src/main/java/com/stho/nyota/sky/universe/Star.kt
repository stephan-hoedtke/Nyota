package com.stho.nyota.sky.universe

import com.stho.nyota.sky.utilities.Formatter
import com.stho.nyota.sky.utilities.Moment
import com.stho.nyota.sky.utilities.PropertyList

/**
 * Created by shoedtke on 31.08.2016.
 */
class Star private constructor(override val name: String, val hasUniqueName: Boolean, val symbol: UniverseInitializer.Symbol, RA: Double, Decl: Double, magn: Double) : AbstractElement(RA, Decl, magn) {

    private val constellations: ArrayList<Constellation> = ArrayList()

    override val imageId: Int
        get() = com.stho.nyota.R.mipmap.star

    override val largeImageId: Int
        get() = com.stho.nyota.R.drawable.star

    override fun getBasics(moment: Moment): PropertyList =
        super.getBasics(moment).apply {
            add(com.stho.nyota.R.drawable.alpha_gray, "Magnitude", Formatter.df2.format(magn))
            add(UniverseInitializer.greekSymbolImageId(symbol), "Symbol", UniverseInitializer.greekSymbolToString(symbol))
        }

    override fun getDetails(moment: Moment): PropertyList =
        super.getDetails(moment).apply {
            for(constellation: Constellation in constellations) {
                add(constellation.imageId, "Constellation", constellation.name)
            }
        }

    fun isBrighterThan(magnitude: Double): Boolean =
        magn <= magnitude

    fun register(constellation: Constellation) {
        if (!constellations.contains(constellation)) {
            constellations.add(constellation)
        }
    }

    companion object {
        fun create(name: String, symbol: UniverseInitializer.Symbol, ra: Double, decl: Double, magnitude: Double): Star =
            Star(name, true, symbol, ra, decl, magnitude)

        fun create(symbol: UniverseInitializer.Symbol, ra: Double, decl: Double, magnitude: Double): Star =
            Star(symbol.toString(), false, symbol, ra, decl, magnitude)
    }
}