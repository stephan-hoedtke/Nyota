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
class Constellation internal constructor(val id: Long, val rank: Int, override val name: String, val abbreviation: String, val author: String, val year: Int, val brightness: Double) : AbstractElement() {

    val stars: ArrayList<Star> = ArrayList()
    val lines: ArrayList<Collection<Star>> = ArrayList()

    private val symbols: EnumMap<Symbol, Star> = EnumMap(Symbol::class.java)
    private val translations: EnumMap<Language, String> = EnumMap(Language::class.java)

    override val imageId: Int = when (rank) {
        Andromeda -> R.drawable.constellation_andromeda
        Antlia -> R.drawable.constellation_antlia
        Apus -> R.drawable.constellation_apus
        Aquarius -> R.drawable.constellation_aquarius
        Aquila -> R.drawable.constellation_aquila
        Ara -> R.drawable.constellation_ara
        Aries -> R.drawable.constellation_aries
        Auriga -> R.drawable.constellation_auriga
        Bootes -> R.drawable.constellation_bootes
        Caelum -> R.drawable.constellation_caelum
        Camelopardalis -> R.drawable.constellation_camelopardalis
        Cancer -> R.drawable.constellation_cancer
        CanesVenatici -> R.drawable.constellation_canes_venatici
        CanisMajor -> R.drawable.constellation_canis_major
        CanisMinor -> R.drawable.constellation_canis_minor
        Capricornus -> R.drawable.constellation_capricornus
        Carina -> R.drawable.constellation_carina
        Cassiopeia -> R.drawable.constellation_cassiopeia
        Centaurus -> R.drawable.constellation_centaurus
        Cepheus -> R.drawable.constellation_cepheus
        Cetus -> R.drawable.constellation_cetus
        Chamaeleon -> R.drawable.constellation_chamaeleon
        Circinus -> R.drawable.constellation_circinus
        Columba -> R.drawable.constellation_columba
        ComaBerenices -> R.drawable.constellation_coma_berenices
        CoronaAustralis -> R.drawable.constellation_corona_australis
        CoronaBorealis -> R.drawable.constellation_corona_borealis
        Corvus -> R.drawable.constellation_corvus
        Crater -> R.drawable.constellation_crater
        Crux -> R.drawable.constellation_cross
        Cygnus -> R.drawable.constellation_cygnus
        Delphinus -> R.drawable.constellation_delphinus
        Dorado -> R.drawable.constellation_dorado
        Draco -> R.drawable.constellation_draco
        Equuleus -> R.drawable.constellation_equuleus
        Eridanus -> R.drawable.constellation_eridanus
        Fornax -> R.drawable.constellation_fornax
        Gemini -> R.drawable.constellation_gemini
        Grus -> R.drawable.constellation_grus
        Hercules -> R.drawable.constellation_hercules
        Horologium -> R.drawable.constellation_horologium
        Hydra -> R.drawable.constellation_hydra
        Hydrus -> R.drawable.constellation_hydrus
        Indus -> R.drawable.constellation_indus
        Lacerta -> R.drawable.constellation_lacerta
        Leo -> R.drawable.constellation_leo
        LeoMinor -> R.drawable.constellation_leo_minor
        Lepus -> R.drawable.constellation_lepus
        Libra -> R.drawable.constellation_libra
        Lupus -> R.drawable.constellation_lupus
        Lynx -> R.drawable.constellation_lynx
        Lyra -> R.drawable.constellation_lyra
        Mensa -> R.drawable.constellation_mensa
        Microscopium -> R.drawable.constellation_microscopium
        Monoceros -> R.drawable.constellation_monoceros
        Musca -> R.drawable.constellation_musca
        Norma -> R.drawable.constellation_norma
        Octans -> R.drawable.constellation_octans
        Ophiuchus -> R.drawable.constellation_ophiuchus
        Orion -> R.drawable.constellation_orion
        Pavo -> R.drawable.constellation_pavo
        Pegasus -> R.drawable.constellation_pegasus
        Perseus -> R.drawable.constellation_perseus
        Phoenix -> R.drawable.constellation_phoenix
        Pictor -> R.drawable.constellation_pictor
        Pisces -> R.drawable.constellation_pisces
        PiscisAustrinus -> R.drawable.constellation_piscis_austrinus
        Puppis -> R.drawable.constellation_puppis
        Pyxis -> R.drawable.constellation_pyxis
        Reticulum -> R.drawable.constellation_reticulum
        Sagitta -> R.drawable.constellation_sagitta
        Sagittarius -> R.drawable.constellation_sagittarius
        Scorpius -> R.drawable.constellation_scorpius
        Sculptor -> R.drawable.constellation_sculptor
        Scutum -> R.drawable.constellation_scutum
        Serpens -> R.drawable.constellation_serpens
        Sextans -> R.drawable.constellation_sextans
        Taurus -> R.drawable.constellation_taurus
        Telescopium -> R.drawable.constellation_telescopium
        Triangulum -> R.drawable.constellation_triangulum
        TriangulumAustrale -> R.drawable.constellation_triangulum_australe
        Tucana -> R.drawable.constellation_tucana
        UrsaMajor -> R.drawable.constellation_urs_major
        UrsaMinor -> R.drawable.constellation_urs_minor
        Vela -> R.drawable.constellation_vela
        Virgo -> R.drawable.constellation_virgo
        Volans -> R.drawable.constellation_volans
        Vulpecula -> R.drawable.constellation_vulpecula
        else -> throw Exception("Invalid constellation rank $rank")
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
        } catch (ex: Exception) {
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
            val existingStarForSymbol = symbols[star.symbol]
            if (existingStarForSymbol == null || star.isBrighterThan(existingStarForSymbol.magn)) {
                symbols[star.symbol] = star
            }
        }
    }

    /**
     * Register a star for a constellation, using another symbol. This is needed if a star belongs to multiple constellations
     * examples:
     *      HD 358 "Alpheratz" = Alpha Andromeda = Delta Pegasus
     *      HD 212593 = 1 Lacerta
     */
    fun register(symbol: Symbol, star: Star): Constellation {
        if (!stars.contains(star)) {
            stars.add(star)
            star.register(this)
        }
        symbols[symbol] = star
        return this
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
        symbols[symbol] ?: throw Exception("Star $symbol is not registered in constellation $name yet.")

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

    /**
     * Zodiac = Tierkreis = ζῳδιακός
     */
    data class Zodiac(val number: Int, val fromMonth: Int, val fromDay: Int, val toMonth: Int, val toDay: Int) {
        override fun toString(): String {
            return "$fromDay.${fromMonth + 1} - $toDay.${toMonth + 1}"
        }
    }

    private val zodiac: Zodiac? = when (rank) {
        Aries -> Zodiac(1, Calendar.MARCH, 21, Calendar.APRIL, 20)
        Taurus -> Zodiac(2, Calendar.APRIL, 21, Calendar.MAY, 20)
        Gemini -> Zodiac(3, Calendar.MAY, 21, Calendar.JUNE, 21)
        Cancer -> Zodiac(4, Calendar.JUNE, 22, Calendar.JULY, 22)
        Leo -> Zodiac(5, Calendar.JULY, 23, Calendar.AUGUST, 23)
        Virgo -> Zodiac(6, Calendar.AUGUST, 24, Calendar.SEPTEMBER, 23)
        Libra -> Zodiac(7, Calendar.SEPTEMBER, 24, Calendar.OCTOBER, 23)
        Scorpius -> Zodiac(8, Calendar.OCTOBER, 24, Calendar.NOVEMBER, 22)
        Sagittarius -> Zodiac(9, Calendar.NOVEMBER, 23, Calendar.DECEMBER, 21)
        Capricornus -> Zodiac(10, Calendar.DECEMBER, 22, Calendar.JANUARY, 20)
        Aquarius -> Zodiac(11, Calendar.JANUARY, 21, Calendar.FEBRUARY, 19)
        Pisces -> Zodiac(12, Calendar.FEBRUARY, 20, Calendar.MARCH, 20)
        else -> null
    }

    val isZodiac: Boolean =
        (zodiac != null)

    val isPtolemaeus: Boolean =
        (year < 200)

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
            translations.forEach {
                add(Language.languageImageId(it.key), it.key.toString(), it.value)
            }
            add(R.drawable.empty, author, year.toString())
            zodiac?.also {
                add(R.drawable.empty, "Zodiac sign ${it.number}", it.toString())
            }
            stars.filter { s -> s.hasSymbol || s.hasFriendlyName }.forEach {
                add(it)
            }
        }

    override fun getDetails(moment: Moment): PropertyList =
        super.getDetails(moment).apply {
            stars.filter { s -> !s.hasSymbol && !s.hasFriendlyName }.forEach {
                add(it)
            }
        }

    fun findNearestStarByPosition(position: Topocentric, magnitude: Double, sensitivityAngle: Double): Star? {
        var distance: Double = INVALID_DISTANCE
        var brightness: Double = INVALID_MAGNITUDE
        var star: Star? = null

        stars.forEach {
            if (it.isBrighterThan(magnitude) && it.isNear(position, sensitivityAngle)) {
                val b = it.magn
                val d = it.distanceTo(position)
                if (b < brightness || (b == brightness && d < distance)) {
                    brightness = b
                    distance = d
                    star = it
                }
            }
        }

        return star
    }

     companion object {

        fun createWithId(id: Long, rank: Int, name: String, abbreviation: String, english: String, german: String, french: String, greek: String, author: String, year: Int, brightness: Double, visibility: String, map: String, link: String) =
            Constellation(id, rank, name, abbreviation, author, year, brightness)
                .translate(Language.Latin, name)
                .translate(Language.German, german)
                .translate(Language.English, english)
                .translate(Language.French, french)
                .translate(Language.Greek, greek)
                .link(link)
        // TODO: Visibility
        // TODO: Map

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

        val zodiacSign: Array<Int> = arrayOf(

        )
    }
}
