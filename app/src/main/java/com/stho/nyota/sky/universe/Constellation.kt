package com.stho.nyota.sky.universe

import android.util.Log
import com.stho.nyota.R
import com.stho.nyota.sky.utilities.*
import com.stho.nyota.sky.utilities.Topocentric.Companion.INVALID_DISTANCE
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by shoedtke on 08.09.2016.
 */
@Suppress("LocalVariableName")
class Constellation internal constructor(val id: Long, val rank: Int, override val name: String, val abbreviation: String) : AbstractElement() {

    // TODO: make those lists immutable for public, and mutable private
    val stars: ArrayList<Star> = ArrayList()
    val lines: ArrayList<Collection<Star>> = ArrayList()
    private val map: EnumMap<Symbol, Star> = EnumMap(Symbol::class.java)
    private val translations: EnumMap<Language, String> = EnumMap(Language::class.java)

    override val imageId: Int = when (rank) {
        Crux -> R.drawable.constellation_cross
        TriangulumAustrale -> R.drawable.constellation_triangulum_australe
        Orion -> R.drawable.constellation_orion
        UrsaMajor -> R.drawable.constellation_urs_major
        UrsaMinor -> R.drawable.constellation_urs_minor
        Cassiopeia -> R.drawable.constellation_cassiopeia
        //TODO Pleiades -> R.drawable.constellation_pleiades
        Pegasus -> R.drawable.constellation_pegasus
        Andromeda -> R.drawable.constellation_andromeda
        Aquila -> R.drawable.constellation_aquila
        Cygnus -> R.drawable.constellation_cygnus
        Lacerta -> R.drawable.constellation_lacerta
        Ara -> R.drawable.constellation_ara
        Hydra -> R.drawable.constellation_hydra
        Aries -> R.drawable.constellation_aries
        Taurus -> R.drawable.constellation_taurus
        Gemini -> R.drawable.constellation_gemini
        Cancer -> R.drawable.constellation_cancer
        Leo -> R.drawable.constellation_leo
        Virgo -> R.drawable.constellation_virgo
        Libra -> R.drawable.constellation_libra
        Scorpius -> R.drawable.constellation_scorpius
        Sagittarius -> R.drawable.constellation_sagittarius
        Capricornus -> R.drawable.constellation_capricornus
        Aquarius -> R.drawable.constellation_aquarius
        Pisces -> R.drawable.constellation_pisces
        Auriga -> R.drawable.constellation_auriga
        Perseus -> R.drawable.constellation_perseus
        Pavo -> R.drawable.constellation_pavo
        Bootes -> R.drawable.constellation_bootes
        Eridanus -> R.drawable.constellation_eridanus
        Centaurus -> R.drawable.constellation_centaurus
        Chamaeleon -> R.drawable.constellation_chamaeleon
        Musca -> R.drawable.constellation_musca
        Antlia -> R.drawable.constellation_antlia
        Apus -> R.drawable.constellation_apus
        else -> R.drawable.constellation
    }

    override val largeImageId: Int
        get() = imageId

    var links: ArrayList<String> = ArrayList()
        private set

    override val key: String =
        toKey(rank)

    fun line(vararg symbols: Symbol): Constellation {
        try {
            require(symbols.size > 1) { "A line must have at least 2 stars" }
            lines.add(getStarsFor(*symbols))
            return this
        }
        catch(ex: Exception) {
            Log.d("ERROR", ex.toString())
            throw ex
        }
    }

    private fun getStarsFor(vararg symbols: Symbol): Collection<Star> =
        symbols.map { s -> get(s) }

    /**
     * Register a star for a constellation, using its symbol
     */
    fun register(star: Star) {
        if (!stars.contains(star)) {
            stars.add(star)
            star.register(this)
        }
        if (star.hasSymbol) {
            val existingStarForSymbol = map[star.symbol]
            if (existingStarForSymbol == null || star.isBrighterThan(existingStarForSymbol.magn)) {
                map[star.symbol] = star
            }
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
        stars.find { star -> star.HD == HD } ?: throw Exception("Star $HD is not registered in constellation $name yet.")

    /**
     * Retrieve a star by the symbol; raising an exception if not found.
     */
    operator fun get(symbol: Symbol): Star =
        map[symbol] ?: throw Exception("Star $symbol is not registered in constellation $name yet.")

    /**
     * Retrieve a star by the key, if it can be found, or null otherwise
     */
    fun findStarInConstellationByKey(key: String): Star? =
        when (Star.isValidKey(key)) {
            true -> {
                val HD = Star.hdFromKey(key)
                get(HD)
            }
            false -> null
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

    override fun getBasics(moment: Moment): PropertyList =
        super.getBasics(moment).apply {
            translations.forEach { add(Language.languageImageId(it.key), it.key.toString(), it.value) }
            stars.filter { s -> s.hasSymbol || s.hasFriendlyName }.forEach{ add(it) }
        }

    override fun getDetails(moment: Moment): PropertyList =
        super.getDetails(moment).apply {
            stars.filter { s -> !s.hasSymbol && !s.hasFriendlyName }.forEach{ add(it) }
        }

    fun findNearestStarByPosition(position: Topocentric, magnitude: Double, sensitivityAngle: Double): Star? {
        var distance: Double = INVALID_DISTANCE
        var brightness: Double = INVALID_MAGNITUDE
        var star: Star? = null

        for (s in stars) {
            if (s.isBrighterThan(magnitude) && s.isNear(position, sensitivityAngle)) {
                val b = s.magn
                val d = s.distanceTo(position)
                if (b < brightness || (b == brightness && d < distance)) {
                    brightness = b
                    distance = d
                    star = s
                }
            }
        }
        return star
    }

    companion object {

        fun createWithId(id: Long, rank: Int, name: String, abbreviation: String, english: String, german: String, link: String) =
            Constellation(id, rank, name, abbreviation)
                .translate(Language.Latin, name)
                .translate(Language.German, german)
                .translate(Language.English, english)
                .link(link)

        private fun toKey(rank: Int) =
            "CONSTELLATION:${rank}"

        fun isValidKey(key: String): Boolean =
            key.startsWith("CONSTELLATION:")

        fun rankFromKey(key: String): Int =
            key.substring(14).toInt()

        const val Hydra: Int = 1
        const val Virgo: Int = 2
        const val UrsaMajor: Int = 3
        const val Cetus: Int = 4
        const val Hercules: Int = 5
        const val Eridanus: Int = 6
        const val Pegasus: Int = 7
        const val Draco: Int = 8
        const val Centaurus: Int = 9
        const val Aquarius: Int = 10
        const val Ophiuchus: Int = 11
        const val Leo: Int = 12
        const val Bootes: Int = 13
        const val Pisces: Int = 14
        const val Sagittarius: Int = 15
        const val Cygnus: Int = 16
        const val Taurus: Int = 17
        const val Camelopardalis: Int = 18
        const val Andromeda: Int = 19
        const val Puppis: Int = 20
        const val Auriga: Int = 21
        const val Aquila: Int = 22
        const val Serpens: Int = 23
        const val Perseus: Int = 24
        const val Cassiopeia: Int = 25
        const val Orion: Int = 26
        const val Cepheus: Int = 27
        const val Lynx: Int = 28
        const val Libra: Int = 29
        const val Gemini: Int = 30
        const val Cancer: Int = 31
        const val Vela: Int = 32
        const val Scorpius: Int = 33
        const val Carina: Int = 34
        const val Monoceros: Int = 35
        const val Sculptor: Int = 36
        const val Phoenix: Int = 37
        const val CanesVenatici: Int = 38
        const val Aries: Int = 39
        const val Capricornus: Int = 40
        const val Fornax: Int = 41
        const val ComaBerenices: Int = 42
        const val CanisMajor: Int = 43
        const val Pavo: Int = 44
        const val Grus: Int = 45
        const val Lupus: Int = 46
        const val Sextans: Int = 47
        const val Tucana: Int = 48
        const val Indus: Int = 49
        const val Octans: Int = 50
        const val Lepus: Int = 51
        const val Lyra: Int = 52
        const val Crater: Int = 53
        const val Columba: Int = 54
        const val Vulpecula: Int = 55
        const val UrsaMinor: Int = 56
        const val Telescopium: Int = 57
        const val Horologium: Int = 58
        const val Pictor: Int = 59
        const val PiscisAustrinus: Int = 60
        const val Hydrus: Int = 61
        const val Antlia: Int = 62
        const val Ara: Int = 63
        const val LeoMinor: Int = 64
        const val Pyxis: Int = 65
        const val Microscopium: Int = 66
        const val Apus: Int = 67
        const val Lacerta: Int = 68
        const val Delphinus: Int = 69
        const val Corvus: Int = 70
        const val CanisMinor: Int = 71
        const val Dorado: Int = 72
        const val CoronaBorealis: Int = 73
        const val Norma: Int = 74
        const val Mensa: Int = 75
        const val Volans: Int = 76
        const val Musca: Int = 77
        const val Triangulum: Int = 78
        const val Chamaeleon: Int = 79
        const val CoronaAustralis: Int = 80
        const val Caelum: Int = 81
        const val Reticulum: Int = 82
        const val TriangulumAustrale: Int = 83
        const val Scutum: Int = 84
        const val Circinus: Int = 85
        const val Sagitta: Int = 86
        const val Equuleus = 87
        const val Crux = 88

    }
}
