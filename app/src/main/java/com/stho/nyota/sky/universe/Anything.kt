package com.stho.nyota.sky.universe

import com.stho.nyota.sky.utilities.Formatter
import com.stho.nyota.sky.utilities.Moment
import com.stho.nyota.sky.utilities.PropertyList

class Anything(override val name: String, RA: Double, Decl: Double, mag: Double, val distance: Double) : AbstractElement(RA, Decl, mag) {

    override val key: String =
        toKey(name)

    override val imageId: Int
        get() = com.stho.nyota.R.drawable.star

    override val largeImageId: Int
        get() = imageId

    private val magnAsString: String =
        Formatter.df2.format(magn)

    override fun getBasics(moment: Moment): PropertyList =
        super.getBasics(moment).apply {
            add(com.stho.nyota.R.drawable.empty, "Name", name)
            add(com.stho.nyota.R.drawable.alpha_gray, "Magnitude", magnAsString)
            add(com.stho.nyota.R.drawable.distance, "Distance", Formatter.toDistanceLyString(distance))
        }

    companion object {
        private fun toKey(name: String): String =
            "ANY:$name"

        fun isValidKey(key: String): Boolean =
            key.startsWith("ANY:")

        fun nameFromKey(key: String): String =
            key.substring(4)
    }
}
