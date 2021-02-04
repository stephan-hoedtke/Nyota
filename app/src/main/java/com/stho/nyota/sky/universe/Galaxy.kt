package com.stho.nyota.sky.universe

import com.stho.nyota.R
import com.stho.nyota.sky.utilities.Formatter

class Galaxy(override val name: String, override val imageId: Int, RA: Double, Decl: Double, mag: Double, distance: Double) : AbstractElement(RA, Decl, mag) {

    override val key: String =
        toKey(name)

    override val largeImageId: Int
        get() = imageId

    val magnAsString: String =
        Formatter.df2.format(magn)

    companion object {
        private fun toKey(name: String): String =
            "GALAXY:$name"

        fun isValidKey(key: String): Boolean =
            key.startsWith("GALAXY:")

        fun nameFromKey(key: String): String =
            key.substring(7)
    }

}
