package com.stho.nyota.sky.universe

import com.stho.nyota.R
import com.stho.nyota.settings.Settings
import com.stho.nyota.sky.utilities.Formatter
import com.stho.nyota.sky.utilities.Moment
import com.stho.nyota.sky.utilities.PropertyList

/**
 * Created by shoedtke on 31.08.2016.
 * @param HD Henry Draper Catalog Number
 */
class Star private constructor(val id: Long, val HD: Int, override val name: String, val symbol: Symbol, RA: Double, Decl: Double, magn: Double, val distance: Double, val color: StarColor) : AbstractElement(RA, Decl, magn) {

   private val constellations: ArrayList<Constellation> = ArrayList()

    private val hasHenryDraperCatalogNumber: Boolean =
        HD > 0

    val hasSymbol: Boolean =
        symbol.isSymbol()

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
            constellations.first().let { "${symbol.greekSymbol} ${it.abbreviation}" }
        } else if (hasName) {
            name
        } else if (hasHenryDraperCatalogNumber) {
            "HD: $HD"
        } else {
            "ID: $id"
        }

    override val imageId: Int
        get() = color.imageId

    override val largeImageId: Int
        get() = color.imageId

    override fun getBasics(moment: Moment): PropertyList =
        super.getBasics(moment).apply {
            add(R.drawable.empty, "Name", if (hasFriendlyName) friendlyName else name)
            add(symbol.imageId, "Symbol", symbol.greekSymbol)
            add(R.drawable.alpha_gray, "Magnitude", magnAsString)
            add(R.drawable.distance, "Distance", Formatter.toDistanceLyString(distance))
            constellations.forEach { add(it) }

        }

    override fun getDetails(moment: Moment): PropertyList =
        super.getDetails(moment).apply {
            add(R.drawable.empty, "Henry Draper", HD.toString())
            add(R.drawable.empty, "Name", name)
            add(color.imageId, "Color", color.toString())
        }

    override val key: String =
        toKey(HD)

    fun isBrighterThan(magnitude: Double): Boolean =
        magn <= magnitude || magnitude >= Settings.MAX_MAGNITUDE

    val magnAsString: String =
        Formatter.df2.format(magn)

    fun register(constellation: Constellation) {
        if (!constellations.contains(constellation)) {
            constellations.add(constellation)
        }
    }

    fun setFriendlyName(friendlyName: String): Star {
        this.friendlyName = friendlyName
        this.hasFriendlyName = true
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
        fun createWithId(id: Long, hd: Int, name: String, friendlyName: String, symbol: Symbol, rightAscension: Double, declination: Double, magnitude: Double, distance: Double, color: StarColor) =
            Star(id, HD = hd, name, symbol, RA = 15 * rightAscension, Decl = declination, magn = magnitude, distance, color).also {
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