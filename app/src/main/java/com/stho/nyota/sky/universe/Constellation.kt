package com.stho.nyota.sky.universe

import com.stho.nyota.sky.utilities.CircularAverage
import com.stho.nyota.sky.utilities.Moment
import com.stho.nyota.sky.utilities.PropertyList
import java.util.*
import kotlin.collections.HashMap

enum class Language {
    LATIN,
    ENGLISH,
    GERMAN,
}

/**
 * Created by shoedtke on 08.09.2016.
 */
class Constellation internal constructor(override val name: String, override val imageId: Int) : AbstractElement() {

    val stars = ArrayList<Star>()
    val lines = ArrayList<Array<out Star>>()
    val translations: HashMap<Language, String> = HashMap()

    override val largeImageId: Int
        get() = imageId

    fun register(vararg newStars: Star): Constellation {
        for (star in newStars) {
            if (!stars.contains(star)) {
                stars.add(star)
                star.register(this)
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

    fun translate(language: Language, name: String): Constellation {
        translations[language] = name
        return this
    }

    override fun toString(): String =
        name

    private fun calculateAveragePosition() {
        val length = stars.size
        val decl = DoubleArray(length)
        val ra = DoubleArray(length)
        for (i in 0 until length) {
            val star = stars[i]
            decl[i] = star.Decl
            ra[i] = star.RA
        }
        Decl = CircularAverage.getCircularAverage(decl, length)
        RA = CircularAverage.getCircularAverage(ra, length)
    }

    override fun getDetails(moment: Moment): PropertyList =
        super.getDetails(moment).apply {
            for (star in stars) {
                add(UniverseInitializer.greekSymbolImageId(star.symbol), star.name, star.position.toString())
            }
            for (x in translations) {
                add(UniverseInitializer.languageImageId(x.key), x.key.toString(), x.value)
            }
        }

}