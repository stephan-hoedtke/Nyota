package com.stho.nyota.sky.universe

import com.stho.nyota.sky.utilities.Average
import com.stho.nyota.sky.utilities.Moment
import com.stho.nyota.sky.utilities.PropertyList
import java.util.*

/**
 * Created by shoedtke on 08.09.2016.
 */
class Constellation internal constructor(override val name: String, override val imageId: Int) : AbstractElement() {
    val stars = ArrayList<Star>()
    val lines = ArrayList<Array<out Star>>()

    override val largeImageId: Int
        get() = imageId

    fun register(vararg newStars: Star): Constellation {
        for (star in newStars) {
            if (!stars.contains(star)) {
                stars.add(star)
            }
        }
        if (newStars.size > 1) {
            lines.add(newStars)
        }
        return this
    }

    fun build(): Constellation {
        calculateAveragePosition()
        return this
    }

    override fun toString(): String {
        return name
    }

    private fun calculateAveragePosition() {
        val length = stars.size
        val decl = DoubleArray(length)
        val ra = DoubleArray(length)
        for (i in 0 until length) {
            val star = stars[i]
            decl[i] = star.Decl
            ra[i] = star.RA
        }
        Decl = Average.getCircularAverage(decl, length)
        RA = Average.getCircularAverage(ra, length)
    }

    override fun getDetails(moment: Moment): PropertyList {
        val list = super.getDetails(moment)
        for (star in stars) {
            list.add(com.stho.nyota.R.drawable.star, star.name, star.position.toString())
        }
        return list
    }

}