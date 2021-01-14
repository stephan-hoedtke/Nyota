package com.stho.nyota.sky.universe

import com.stho.nyota.sky.utilities.Formatter
import com.stho.nyota.sky.utilities.Moment
import com.stho.nyota.sky.utilities.PropertyKey
import com.stho.nyota.sky.utilities.PropertyList
import java.lang.Exception

/**
 * Created by shoedtke on 31.08.2016.
 */
class Star private constructor(override val name: String, val nameIsUnique: Boolean, val symbol: Symbol, RA: Double, Decl: Double, magn: Double) : AbstractElement(RA, Decl, magn) {

    private val constellations: ArrayList<Constellation> = ArrayList()

    override val uniqueName: String by lazy {
        when (nameIsUnique) {
            true -> name
            false -> constellations.firstOrNull()?.joinConstellationStarName(this) ?: throw Exception("Star $name doesn't have a unique name")
        }
    }

    override fun toString(): String =
        when (nameIsUnique) {
            true -> name
            false -> constellations.firstOrNull()?.let { it.name + ": " + this.name } ?: name
        }

    override val imageId: Int
        get() = com.stho.nyota.R.mipmap.star

    override val largeImageId: Int
        get() = com.stho.nyota.R.drawable.star

    override fun getBasics(moment: Moment): PropertyList =
        super.getBasics(moment).apply {
            add(com.stho.nyota.R.drawable.alpha_gray, "Magnitude", Formatter.df2.format(magn))
            add(Symbol.greekSymbolImageId(symbol), "Symbol", Symbol.greekSymbolToString(symbol))
        }

    override fun getDetails(moment: Moment): PropertyList =
        super.getDetails(moment).apply {
            for(constellation: Constellation in constellations) {
                add(PropertyKey.CONSTELLATION, constellation.imageId, constellation.name, constellation.position.toString())
            }
        }

    fun isBrighterThan(magnitude: Double): Boolean =
        magn <= magnitude

    fun register(constellation: Constellation) {
        if (!constellations.contains(constellation)) {
            constellations.add(constellation)
        }
    }

    val referenceConstellation: Constellation?
        get() = constellations.firstOrNull()

    companion object {
        fun create(name: String, symbol: Symbol, ra: Double, decl: Double, magnitude: Double): Star =
            Star(name, true, symbol, ra, decl, magnitude)

        fun create(symbol: Symbol, ra: Double, decl: Double, magnitude: Double): Star =
            Star(symbol.toString(), false, symbol, ra, decl, magnitude)
    }
}