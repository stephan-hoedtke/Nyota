package com.stho.nyota.sky.universe

import android.util.Log
import com.stho.nyota.R
import com.stho.nyota.sky.utilities.CircularAverage
import com.stho.nyota.sky.utilities.Moment
import com.stho.nyota.sky.utilities.PropertyKey
import com.stho.nyota.sky.utilities.PropertyList
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * Created by shoedtke on 08.09.2016.
 */
class Constellation internal constructor(val id: Long, val number: Int, override val name: String, val abbreviation: String) : AbstractElement() {

    // TODO: make those lists immutable for public, and mutable private
    val stars: ArrayList<Star> = ArrayList()
    val lines: ArrayList<Array<out Star>> = ArrayList()
    private val map: EnumMap<Symbol, Star> = EnumMap(Symbol::class.java)
    private val translations: EnumMap<Language, String> = EnumMap(Language::class.java)

    override val imageId: Int = when (name) {
        "Crux" -> R.drawable.constellation_cross
        "Triangulum Australe" -> R.drawable.constellation_triangulum_australe
        "Orion" -> R.drawable.constellation_orion
        "Urs Major" -> R.drawable.constellation_urs_major
        "Urs Minor" -> R.drawable.constellation_urs_minor
        "Cassiopeia" -> R.drawable.constellation_cassiopeia
        "Pleiades" -> R.drawable.constellation_pleiades
        "Pegasus" -> R.drawable.constellation_pegasus
        "Andromeda" -> R.drawable.constellation_andromeda
        "Aquila" -> R.drawable.constellation_aquila
        "Cygnus" -> R.drawable.constellation_cygnus
        "Lacerta" -> R.drawable.constellation_lacerta
        "Ara" -> R.drawable.constellation_ara
        "Hydra" -> R.drawable.constellation_hydra
        "Aries" -> R.drawable.constellation_aries
        "Taurus" -> R.drawable.constellation_taurus
        "Gemini" -> R.drawable.constellation_gemini
        "Cancer" -> R.drawable.constellation_cancer
        "Leo" -> R.drawable.constellation_leo
        "Virgo" -> R.drawable.constellation_virgo
        "Libra" -> R.drawable.constellation_libra
        "Scorpius" -> R.drawable.constellation_scorpius
        "Sagittarius" -> R.drawable.constellation_sagittarius
        "Capricornus" -> R.drawable.constellation_capricornus
        "Aquarius" -> R.drawable.constellation_aquarius
        "Pisces" -> R.drawable.constellation_pisces
        "Auriga" -> R.drawable.constellation_auriga
        "Perseus" -> R.drawable.constellation_perseus
        "Pavo" -> R.drawable.constellation_pavo
        else -> R.drawable.constellation
    }

    override val largeImageId: Int
        get() = imageId

    var links: ArrayList<String> = ArrayList()
        private set

    fun line(vararg symbols: Symbol): Constellation {
        try {
            if (symbols.size < 2) throw Exception("A line must have at least 2 stars")
            lines.add(getStarsFor(*symbols))
            return this
        }
        catch(ex: Exception) {
            Log.d("ERROR", ex.toString())
            throw ex
        }
    }

    @Deprecated("Use registerLine(vararg symbols: Symbol)")
    fun line(vararg newStars: Star): Constellation {
        if (newStars.size < 2) throw Exception("A line must have at least 2 stars")
        lines.add(newStars)
        return this
    }

    private fun getStarsFor(vararg symbols: Symbol): Array<out Star> {
        // TODO: make this more Kotlin-Like:
        val array = ArrayList<Star>()
        symbols.forEach { x -> array.add(get(x)) }
        return array.toTypedArray<Star>()
    }

    /**
     * Register a star for a constellation, using its symbol
     */
    fun register(star: Star) {
        if (!stars.contains(star)) {
            stars.add(star)
            star.register(this)
        }
        if (star.hasSymbol) {
            map[star.symbol] = star
        }
    }

    /**
     * Register a star for a constellation, using another symbol. This is needed if a star belongs to multiple constellations
     * examples:
     *      HD 358 "Alpheratz" = Alpha Andromeda = Delta Pegasus
     *      HD 212593 = 1 Lacerta
     */
    fun register(symbol: Symbol, star: Star) {
        if (!stars.contains(star)) {
            stars.add(star)
            star.register(this)
        }
        map[symbol] = star
    }

    fun build(): Constellation {
        calculateAveragePosition()
        stars.sortBy { it.magn }
        return this
    }

    fun translate(language: Language, name: String): Constellation {
        translations[language] = name
        return this
    }

    fun link(link: String): Constellation {
        if (link.isNotBlank()) {
            links.add(link)
        }
        return this
    }

    /**
     * Retrieve a star by the henry draper catalog number; raising an exception if not found.
     */
    operator fun get(HD: Int): Star =
        stars.firstOrNull { star -> star.HD == HD } ?: throw Exception("Star $HD is not registered in constellation $name yet.")

    /**
     * Retrieve a star by the symbol; raising an exception if not found.
     */
    operator fun get(symbol: Symbol): Star =
        map[symbol] ?: throw Exception("Star $symbol is not registered in constellation $name yet.")

    /**
     * Retrieve a star by the name, if found, or null otherwise
     */
    fun findStarInConstellationByName(starName: String): Star? =
        stars.firstOrNull { star -> star.name == starName }

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
                add(PropertyKey.STAR, Symbol.greekSymbolImageId(star.symbol), star.toString(), star.position.toString())
            }
            for (x in translations) {
                add(PropertyKey.TRANSLATION, Language.languageImageId(x.key), x.key.toString(), x.value)
            }
        }

    companion object {

        fun createWithId(id: Long, number: Int, name: String, abbreviation: String, english: String, german: String, link: String) =
            Constellation(id, number, name, abbreviation)
                .translate(Language.Latin, name)
                .translate(Language.German, german)
                .translate(Language.English, english)
                .link(link)
                .build() // TODO: call after all stars had been added, once!
    }
}
