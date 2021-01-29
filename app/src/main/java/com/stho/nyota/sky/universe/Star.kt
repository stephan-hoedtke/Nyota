package com.stho.nyota.sky.universe

import com.stho.nyota.sky.utilities.Formatter
import com.stho.nyota.sky.utilities.Moment
import com.stho.nyota.sky.utilities.PropertyKeyType
import com.stho.nyota.sky.utilities.PropertyList
import com.stho.nyota.ui.sky.SkyViewOptions.Companion.MAX_MAGNITUDE

/**
 * Created by shoedtke on 31.08.2016.
 * @param HD Henry Draper Catalog Number
 */
class Star private constructor(val id: Long, val HD: Int, override val name: String, val symbol: Symbol, RA: Double, Decl: Double, magn: Double, val distance: Double) : AbstractElement(RA, Decl, magn) {

    private val constellations: ArrayList<Constellation> = ArrayList()

    private val hasHenryDraperCatalogNumber: Boolean =
        HD > 0

    val hasSymbol: Boolean =
        symbol.isNotEmpty()

    val hasName: Boolean =
        name.isNotBlank()

    private val hasConstellation: Boolean // mind, it may change when constellations are registered later
        get() = constellations.size > 0

    var hasFriendlyName: Boolean = false
        private set

    var friendlyName: String = name
        private set

    override fun toString(): String =
        if (hasFriendlyName) {
            friendlyName
        } else if (hasSymbol && hasConstellation) {
            constellations.first().let { "${Symbol.greekSymbolToString(symbol)} ${it.abbreviation}" }
        } else if (hasName) {
            name
        } else if (hasHenryDraperCatalogNumber) {
            "HD: $HD"
        } else {
            "ID: $id"
        }

    override val imageId: Int
        get() = com.stho.nyota.R.mipmap.star

    override val largeImageId: Int
        get() = com.stho.nyota.R.drawable.star

    override fun getBasics(moment: Moment): PropertyList =
        super.getBasics(moment).apply {
            add(com.stho.nyota.R.drawable.empty, "Friendly Name", friendlyName)
            add(com.stho.nyota.R.drawable.empty, "Name", name)
            add(com.stho.nyota.R.drawable.alpha_gray, "Magnitude", Formatter.df2.format(magn))
            add(com.stho.nyota.R.drawable.distance, "Distance (ly)", Formatter.df0.format(distance))
            add(Symbol.greekSymbolImageId(symbol), "Symbol", Symbol.greekSymbolToString(symbol))
            add(com.stho.nyota.R.drawable.empty, "Henry Draper", HD.toString())
        }

    override fun getDetails(moment: Moment): PropertyList =
        super.getDetails(moment).apply {
            constellations.forEach { add(it) }
        }

    override val key: String =
        toKey(HD)

    fun isBrighterThan(magnitude: Double): Boolean =
        magn <= magnitude || magnitude >= MAX_MAGNITUDE

    fun register(constellation: Constellation) {
        if (!constellations.contains(constellation)) {
            constellations.add(constellation)
        }
    }

    fun setFriendlyName(name: String): Star {
        friendlyName = name
        hasFriendlyName = true
        return this
    }

    val referenceConstellation: Constellation?
        get() = constellations.firstOrNull()

    companion object {
        /**
         * create a new star as given by the star catalog
         * @id is the primary key in the database
         * @hd henry draper catalog number
         */
        fun createWithId(id: Long, hd: Int, name: String, friendlyName: String, symbol: Symbol, rightAscension: Double, declination: Double, magnitude: Double, distance: Double) =
            Star(id, HD = hd, name, symbol, RA = 15 * rightAscension, Decl = declination, magn = magnitude, distance).also {
                if (friendlyName.isNotBlank()) {
                    it.setFriendlyName(friendlyName)
                }
            }

        private fun toKey(HD: Int) =
            "STAR:HD${HD}"

        fun isValidKey(key: String): Boolean =
            key.startsWith("STAR:HD")

        fun hdFromKey(key: String): Int =
            key.substring(7).toInt()

    }
}