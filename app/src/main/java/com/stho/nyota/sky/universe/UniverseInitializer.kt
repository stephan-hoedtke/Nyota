package com.stho.nyota.sky.universe

import android.util.Log
import com.stho.nyota.sky.utilities.Degree
import com.stho.nyota.sky.utilities.Hour
import java.lang.Exception


// TODO Make the UniverseInitialize a set of internal and private methods in Universe
/**
 * Created by shoedtke on 31.08.2016.
 */
class UniverseInitializer(universe: Universe) : AbstractUniverseInitializer(universe) {

    // https://en.wikipedia.org/wiki/88_modern_constellations
    // https://wissen.naanoo.de/esoterik/sternzeichen
    // https://www.sternfreunde-muenster.de/
    // https://en.wikipedia.org/wiki/IAU_designated_constellations_by_area
    // https://de.wikipedia.org/wiki/Liste_der_hellsten_Sterne
    // https://de.wikipedia.org/wiki/Bright-Star-Katalog
    // Translations:
    // https://starchild.gsfc.nasa.gov/docs/StarChild/questions/88constellations.html
    fun initialize() {
        try {
            registerVIP()
            registerAndromeda()
            registerAquarius()
            registerAquila()
            registerAra()
            registerAries()
            registerAuriga()
            registerChamaeleon()
            registerCancer()
            registerCapricornus()
            registerCassiopeia()
            registerCrux()
            registerCygnus()
            registerEridanus()
            registerGemini()
            registerHydra()
            registerLacerta()
            registerLeo()
            registerLibra()
            registerMusca()
            registerOrion()
            registerPavo()
            registerPegasus()
            registerPerseus()
            registerPisces()
            registerSagittarius()
            registerScorpius()
            registerTaurus()
            registerTriangulumAustrale()
            registerUrsaMajor()
            registerUrsaMinor()
            registerVirgo()
            registerBootes()
            registerCentaurus()
            registerAntlia()
            registerApus()
            registerSatellite()
            registerSpecialElements()
            registerGalaxies()
            registerAnything()
            rebuild()
        } catch (ex: Exception) {
            Log.d("ERROR", ex.toString())
            throw ex
        }
     }

    // Right ascension "RA" is measured at the celestial equator (which is defined by earth equator) positive from vernal equinox (in March) into the east
    // - measured in hours (not in degree), 360° = 24 hours
    // - RA 12h is visible during night in March. RA 0h is visible during night in September
    // Declination "delta" is measured north or south of the celestial equator, along the hour circle passing through the point in question.
    // - north of equator = 0 to 90. 90 is North Pole
    // - south of equator = 0 to -90. -90 is the South Pole
    // The horizontal, or altitude-azimuth, system is based on the position of the observer on Earth, which revolves around its own axis once per sidereal day (23 hours, 56 minutes and 4.091 seconds)
    // See:
    // https://tools.wmflabs.org/geohack/skyhack.php?ra=2.5303010277778&de=89.264109444444&size=0.016666666666667&name=Polarstern+%2F+%CE%B1+Ursae+Minoris&object=Polaris
    // http://eco.mtk.nao.ac.jp/cgi-bin/koyomi/cande/horizontal_rhip_en.cgi
    // https://www.timeanddate.de/astronomie/nachthimmel/deutschland/berlin
    // Source: https://www.space.com/21640-star-luminosity-and-magnitude.html
    //      Sirius:
    //          R.A.: 6h 45m 9.25s / Dec.: -16° 42' 47.3"
    //          Lat.:52.0000° Lon.:13.5000° Hgt.: 0.0m LST:UT+1h
    //              2018-12-04 0:00:00 --> Alt: 16 26 31, Azi: 149 46 54, Hour angle: 21 58 53
    //          Lat.:52.6400° Lon.:13.4900° Hgt.: 55.0m LST:UT+1h ΔT=69s
    //              2018-12-04 0:00:00--> Alt: 15 53 15, Azi: 149 51 54, Hour angle: 21 58 50
    //      Polaris:
    //          R.A.: 2h 31m 47.07s / Dec.: 89° 15' 50.9"
    //          Lat.:52.6400° Lon.:13.4900° Hgt.: 55.0m LST:UT+1h ΔT=69s
    //              2018-12-04 0:00:00--> Alt: 53 14 10, Azi: 359 30 16, Hour angle: 21 58 50
    private fun registerVIP() {
        asVIP(setFriendlyNameTo(10144,"Achernar"))
        asVIP(setFriendlyNameTo(108248,"Acrux"))
        asVIP(setFriendlyNameTo(29139, "Aldebaran"))
        asVIP(setFriendlyNameTo(128620,"Alpha Centauri"))
        asVIP(setFriendlyNameTo(187642, "Altair"))
        asVIP(setFriendlyNameTo(148478, "Antares"))
        asVIP(setFriendlyNameTo(124897, "Arcturus"))
        asVIP(setFriendlyNameTo(111123, "Becrux"))
        asVIP(setFriendlyNameTo(39801,"Betelgeuse"))
        asVIP(setFriendlyNameTo(60179, "Castor"))
        asVIP(setFriendlyNameTo(45348, "Canopus"))
        asVIP(setFriendlyNameTo(34029, "Capella"))
        asVIP(setFriendlyNameTo(197345, "Deneb"))
        asVIP(setFriendlyNameTo(108903, "Gacrux"))
        asVIP(setFriendlyNameTo(122451, "Hadar"))
        asVIP(setFriendlyNameTo(216956, "Fomalhaut"))
        asVIP(setFriendlyNameTo(52089, "Adhara"))
        asVIP(setFriendlyNameTo(8890, "Polaris"))
        asVIP(setFriendlyNameTo(62509, "Pollux"))
        asVIP(setFriendlyNameTo(61421, "Procyon"))
        asVIP(setFriendlyNameTo(87901, "Regulus"))
        asVIP(setFriendlyNameTo(34085, "Rigel"))
        asVIP(setFriendlyNameTo(48915, "Sirius"))
        asVIP(setFriendlyNameTo(158926, "Shaula"))
        asVIP(setFriendlyNameTo(116658, "Spica"))
        asVIP(setFriendlyNameTo(172167, "Vega"))
        asVIP(setFriendlyNameTo(35468, "Bellatrix"))
    }

//    /**
//     * https://de.wikipedia.org/wiki/Liste_der_hellsten_Sterne
//     * https://de.wikipedia.org/wiki/Bright-Star-Katalog
//     */
//    private fun registerBrightestStars() {
//        register("Beta Centauri", Symbol.Beta, "14h 03m 49", "−60° 22′ 22", 0.61, 525.0)
//        register("Bellatrix", Symbol.Gamma, "05h 25m 08", "+6° 20′ 59", 1.64, 243.0)
//        register("Elnath", Symbol.Beta, "05h 26m 17", "+28° 36′ 27", 1.65, 131.0)
//        register("Miaplacidus", Symbol.Beta, "09h 13m 12", "−69° 43′ 02", 1.67, 113.0)
//        register("Alnilam", Symbol.Epsilon, "05h 36m 13", "−1° 12′ 07", 1.69, 1340.0)
//        register("Al Na'ir", Symbol.Alpha, "22h 08m 14", "−46° 57′ 40", 1.73, 101.0)
//        register("Alnitak", Symbol.Zeta, "05h 40m 46", "−1° 56′ 34", 1.74, 820.0)
//        register("Gamma Velorum", Symbol.Gamma, "08h 09m 32", "−47° 20′ 12", 1.75, 840.0)
//        register("Alioth", Symbol.Epsilon, "12h 54m 02", "+55° 57′ 35", 1.76, 81.0)
//        register("Mirfak", Symbol.Alpha, "03h 24m 19", "+49° 51′ 40", 1.79, 592.0)
//        register("Kaus Australis", Symbol.Epsilon, "18h 24m 10", "−34° 23′ 05", 1.79, 143.0)
//        register("Dubhe", Symbol.Alpha, "11h 03m 44", "+61° 45′ 03", 1.81, 124.0)
//        register("Wezen", Symbol.Delta, "07h 08m 23", "−26° 23′ 36", 1.83, 1800.0)
//        register("Alkaid", Symbol.Eta, "13h 47m 32", "+49° 18′ 48", 1.85, 100.0)
//        register("Avior", Symbol.Epsilon, "08h 22m 31", "−59° 30′ 34", 1.86, 630.0)
//        register("Sargas", Symbol.Theta, "17h 37m 19", "−42° 59′ 52", 1.86, 272.0)
//        register("Menkalinan", Symbol.Beta, "05h 59m 32", "+44° 56′ 51", 1.90, 82.0)
//        register("Atria", Symbol.Alpha, "16h 48m 40", "−69° 01′ 39", 1.91, 415.0)
//        register("Alhena", Symbol.Gamma, "06h 37m 43", "+16° 23′ 57", 1.93, 105.0)
//        register("Delta Velorum", Symbol.Delta, "08h 44m 42", "−54° 42′ 30", 1.93, 80.0)
//        register("Alpha Pavonis", Symbol.Alpha, "20h 25m 39", "−56° 44′ 06", 1.94, 185.0)
//        register("Polarstern", Symbol.Alpha, "02h 31m 50", "+89° 15′ 51", 1.97, 430.0)
//        register("Murzim", Symbol.Beta, "06h 22m 42", "−17° 57′ 22", 1.98, 500.0)
//        register("Alphard", Symbol.Alpha, "09h 27m 35", "−8° 39′ 31", 1.99, 177.0)
//        register("Hamal", Symbol.Alpha, "02h 07m 40", "+23° 27′ 07", 2.01, 66.0)
//        register("Algieba", Symbol.Gamma, "10h 19m 58", "+19° 50′ 30", 2.01, 125.0)
//        register("Deneb Kaitos", Symbol.Beta, "00h 43m 35", "−17° 59′ 12", 2.04, 95.0)
//        register("Nunki", Symbol.Sigma, "18h 55m 16", "−26° 17′ 48", 2.05, 225.0)
//        register("Menkent", Symbol.Theta, "14h 06m 40", "−36° 22′ 11", 2.06, 61.0)
//        register("Alpheratz", Symbol.Alpha, "00h 08m 23", "+29° 05′ 26", 2.07, 97.0)
//        register("Saiph", Symbol.Kappa, "05h 47m 45", "−9° 40′ 11", 2.07, 720.0)
//        register("Mirach", Symbol.Beta, "01h 09m 43", "+35° 37′ 14", 2.07, 200.0)
//        register("Kochab", Symbol.Beta, "14h 50m 08", "+74° 09′ 19", 2.07, 126.0)
//        register("Beta Gruis", Symbol.Beta, "22h 42m 40", "−46° 53′ 05", 2.07, 170.0)
//        register("Ras Alhague", Symbol.Alpha, "17h 34m 56", "+12° 33′ 36", 2.08, 47.0)
//        register("Algol", Symbol.Beta, "03h 08m 10", "+40° 57′ 20", 2.09, 93.0)
//        register("Alamach", Symbol.Gamma, "02h 03m 54", "+42° 19′ 47", 2.10, 355.0)
//        register("Denebola", Symbol.Beta, "11h 49m 03", "+14° 34′ 19", 2.14, 36.0)
//        register("Tsih", Symbol.Gamma, "00h 56m 43", "+60° 43′ 00", 2.15, 550.0)
//        register("Muhlifain", Symbol.Gamma, "12h 41m 31", "−48° 57′ 36", 2.20, 130.0)
//        register("Naos", Symbol.Zeta, "08h 03m 35", "−40° 00′ 11", 2.21, 1400.0)
//        register("Aspidiske", Symbol.Iota, "09h 17m 05", "−59° 16′ 31", 2.21, 690.0)
//        register("Gemma", Symbol.Alpha, "15h 34m 41", "+26° 42′ 53", 2.22, 75.0)
//        register("Suhail", Symbol.Lambda, "09h 06m 10", "−43° 14′ 00", 2.23, 575.0)
//        register("Mizar", Symbol.Zeta, "13h 23m 55", "+54° 55′ 31", 2.23, 78.0)
//        register("Sadr", Symbol.Gamma, "20h 22m 14", "+40° 15′ 24", 2.23, 1500.0)
//        register("Schedir", Symbol.Alpha, "00h 40m 30", "+56° 32′ 14", 2.24, 230.0)
//        register("Etamin", Symbol.Gamma, "17h 56m 36", "+51° 29′ 20", 2.24, 148.0)
//        register("Mintaka", Symbol.Delta, "05h 32m 00", "+0° 17′ 57", 2.25, 920.0)
//        register("Caph", Symbol.Beta, "00h 09m 11", "+59° 08′ 59", 2.28, 54.0)
//        register("Epsilon Centauri", Symbol.Epsilon, "13h 39m 53", "−53° 27′ 59", 2.29, 375.0)
//        register("Dschubba", Symbol.Delta, "15h 57m 24", "−22° 29′ 00", 2.29, 400.0)
//        register("Wei", Symbol.Epsilon, "16h 50m 10", "−34° 17′ 36", 2.29, 65.0)
//        register("Alpha Lupi", Symbol.Alpha, "14h 41m 56", "−47° 23′ 18", 2.30, 550.0)
//        register("Eta Centauri", Symbol.Eta, "14h 35m 30", "−42° 09′ 28", 2.33, 310.0)
//        register("Merak", Symbol.Beta, "11h 01m 50", "+56° 22′ 57", 2.34, 79.0)
//        register("Izar", Symbol.Epsilon, "14h 44m 59", "+27° 04′ 27", 2.35, 210.0)
//        register("Enif", Symbol.Epsilon, "21h 44m 11", "+9° 52′ 30", 2.38, 670.0)
//        register("Girtab", Symbol.Kappa, "17h 42m 29", "−39° 01′ 48", 2.39, 465.0)
//        register("Ankaa", Symbol.Alpha, "00h 26m 17", "−42° 18′ 22", 2.40, 77.0)
//        register("Phecda", Symbol.Gamma, "11h 53m 50", "+53° 41′ 41", 2.41, 83.0)
//        register("Sabik", Symbol.Eta, "17h 10m 22", "−15° 43′ 29", 2.43, 84.0)
//        register("Scheat", Symbol.Beta, "23h 03m 46", "+28° 04′ 58", 2.44, 200.0)
//        register("Alderamin", Symbol.Alpha, "21h 18m 35", "+62° 35′ 08", 2.45, 49.0)
//        register("Aludra", Symbol.Eta, "07h 24m 06", "−29° 18′ 11", 2.45, 3200.0)
//        register("Kappa Velorum", Symbol.Kappa, "09h 22m 07", "−55° 00′ 39", 2.47, 540.0)
//        register("Gienah", Symbol.Epsilon, "20h 46m 13", "+33° 58′ 13", 2.48, 72.0)
//        register("Markab", Symbol.Alpha, "23h 04m 46", "+15° 12′ 19", 2.49, 140.0)
//        register("Menkar", Symbol.Alpha, "03h 02m 17", "+4° 05′ 23", 2.54, 220.0)
//        register("Han", Symbol.Zeta, "16h 37m 10", "−10° 34′ 02", 2.54, 460.0)
//        register("Zeta Centauri", Symbol.Zeta, "13h 55m 32", "−47° 17′ 18", 2.55, 385.0)
//        register("Zosma", Symbol.Delta, "11h 14m 07", "+20° 31′ 25", 2.56, 58.0)
//        register("Akrab", Symbol.Beta, "16h 05m 26", "−19° 48′ 19", 2.56, 530.0)
//        register("Arneb", Symbol.Alpha, "05h 32m 44", "−17° 49′ 20", 2.58, 1300.0)
//        register("Delta Centauri", Symbol.Delta, "12h 08m 21", "−50° 43′ 21", 2.58, 400.0)
//    }


// https://en.wikipedia.org/wiki/IAU_designated_constellations

    private fun registerCrux() {
        universe.constellations[Constellation.Crux]
            .line(Symbol.Alpha, Symbol.Gamma)
            .line(Symbol.Beta, Symbol.Delta)
            .build()
     }

    private fun registerTriangulumAustrale() {
        universe.constellations[Constellation.TriangulumAustrale]
            .line(Symbol.Alpha, Symbol.Gamma, Symbol.Epsilon, Symbol.Beta, Symbol.Alpha)

    }

    // http://www.sternenhimmel-aktuell.de/Orion.htm
    private fun registerOrion() {
        universe.constellations[Constellation.Orion]
            .line(Symbol.Kappa, Symbol.Zeta, Symbol.Alpha, Symbol.Gamma, Symbol.Delta, Symbol.Beta)
            .line(Symbol.Zeta, Symbol.Epsilon, Symbol.Delta)
    }

    // https://en.wikipedia.org/wiki/Ursa_Major
    private fun registerUrsaMajor() {
        universe.constellations[Constellation.UrsaMajor]
            .line(Symbol.Eta, Symbol.Zeta, Symbol.Epsilon, Symbol.Delta, Symbol.Gamma, Symbol.Beta, Symbol.Alpha, Symbol.Delta)
    }

    // https://en.wikipedia.org/wiki/Ursa_Minor
    private fun registerUrsaMinor() {
        universe.constellations[Constellation.UrsaMinor]
            .line(Symbol.Alpha, Symbol.Delta, Symbol.Epsilon, Symbol.Zeta, Symbol.Beta, Symbol.Gamma, Symbol.Eta, Symbol.Zeta)
    }

    // https://en.wikipedia.org/wiki/Cassiopeia_(constellation)
    private fun registerCassiopeia() {
        universe.constellations[Constellation.Cassiopeia]
            .line(Symbol.Beta, Symbol.Alpha, Symbol.Gamma, Symbol.Delta, Symbol.Epsilon)
    }

    // https://en.wikipedia.org/wiki/Pegasus_(constellation)
    private fun registerPegasus() {

        // register the star HD 358 (Alpheratz) = Alpha Andromeda in constellation Pegasus as Delta
        val alpheratz = getStar(358)
        val pegasus = universe.constellations[Constellation.Pegasus]

        pegasus.register(Symbol.Delta, alpheratz)
        pegasus
            .line(Symbol.Epsilon, Symbol.Theta, Symbol.Zeta, Symbol.Alpha, Symbol.Gamma, Symbol.Delta, Symbol.Beta, Symbol.Eta, Symbol.Pi)
            .line(Symbol.Beta, Symbol.Mu, Symbol.Lambda, Symbol.Iota, Symbol.Kappa)
            .line(Symbol.Alpha, Symbol.Beta)
    }

    // https://en.wikipedia.org/wiki/Andromeda_(constellation)
    private fun registerAndromeda() {
        universe.constellations[Constellation.Andromeda]
            .line(Symbol.Alpha, Symbol.Delta, Symbol.Beta, Symbol.Gamma)
            .line(Symbol.Eta, Symbol.Zeta, Symbol.Epsilon, Symbol.Delta, Symbol.Pi, Symbol.Iota, Symbol.Kappa, Symbol.Lambda)
    }

    // Adler
    // https://en.wikipedia.org/wiki/List_of_stars_in_Aquila
    private fun registerAquila() {
        universe.constellations[Constellation.Aquila]
            .line(Symbol.Alpha, Symbol.Zeta, Symbol.Delta, Symbol.Alpha, Symbol.Theta, Symbol.Eta, Symbol.Delta, Symbol.Lambda)
    }

    // Schwan
    // https://en.wikipedia.org/wiki/Cygnus_(constellation)
    private fun registerCygnus() {
        universe.constellations[Constellation.Cygnus]
            .line(Symbol.Alpha, Symbol.Gamma, Symbol.Eta, Symbol.Beta)
            .line(Symbol.Zeta, Symbol.Epsilon, Symbol.Gamma, Symbol.Delta, Symbol.Iota, Symbol.Kappa)
    }

    // Eidechse
    // https://en.wikipedia.org/wiki/Lacerta
    private fun registerLacerta() {
        val lacerta = universe.constellations[Constellation.Lacerta]

        getStar(212593).also { lacerta.register(Symbol.Four, it) }
        getStar(213310).also { lacerta.register(Symbol.Five, it) }
        getStar(212120).also { lacerta.register(Symbol.Two, it) }
        getStar(211388).also { lacerta.register(Symbol.One, it) }
        getStar(213420).also { lacerta.register(Symbol.Six, it) }
        getStar(214868).also { lacerta.register(Symbol.Eleven, it) }
        getStar(211073).also { lacerta.register(Symbol.Xi, it) }

        lacerta
            .line(Symbol.Beta, Symbol.Alpha,  Symbol.Four,  Symbol.Five,  Symbol.Two,  Symbol.Six,  Symbol.Xi,  Symbol.One)
            .line( Symbol.Six,  Symbol.Eleven,  Symbol.Five)
    }

    // Altar
    // https://en.wikipedia.org/wiki/Ara_(constellation)
    private fun registerAra() {
        universe.constellations[Constellation.Ara]
            .line(Symbol.Alpha, Symbol.Beta, Symbol.Gamma, Symbol.Delta, Symbol.Eta, Symbol.Zeta, Symbol.Epsilon, Symbol.Alpha)
    }

    // Wasserschlange
    // https://en.wikipedia.org/wiki/Hydra_(constellation)
    private fun registerHydra() {
        universe.constellations[Constellation.Hydra]
            .line(Symbol.Rho, Symbol.Eta, Symbol.Sigma, Symbol.Delta, Symbol.Epsilon, Symbol.Zeta, Symbol.Theta, Symbol.Iota, Symbol.Alpha, Symbol.Upsilon, Symbol.Lambda, Symbol.Mu, Symbol.Phi, Symbol.Nu, Symbol.Chi, Symbol.Beta, Symbol.Gamma, Symbol.Pi)
    }

    // Widder
    // https://en.wikipedia.org/wiki/Aries_(constellation)
    // https://wissen.naanoo.de/esoterik/sternzeichen-widder
    // (1) 21. März – 20. April
    private fun registerAries() {
        val aries = universe.constellations[Constellation.Aries]

        getStar(17573).also { aries.register(Symbol.FortyOne, it) }

        aries
            .line(Symbol.Epsilon, Symbol.FortyOne, Symbol.Alpha, Symbol.Beta, Symbol.Gamma)
    }

    // Stier
    // https://en.wikipedia.org/wiki/Taurus_(constellation)
    // https://deepsky.astronomie.info/Tau/index.de.php !
    // https://wissen.naanoo.de/esoterik/sternzeichen-stier
    // (2) 21. April – 20. Mai
    private fun registerTaurus() {
        universe.constellations[Constellation.Taurus]
            .line(Symbol.Zeta, Symbol.Alpha, Symbol.Theta, Symbol.Gamma, Symbol.Delta, Symbol.Epsilon, Symbol.Beta)
            .line(Symbol.Gamma, Symbol.Lambda, Symbol.Xi, Symbol.Omicron)
    }

    // Zwillinge
    // https://en.wikipedia.org/wiki/Gemini_(constellation)
    // https://deepsky.astronomie.info/Gem/index.de.php
    // https://wissen.naanoo.de/esoterik/sternzeichen-zwilling
    // https://www.space.com/16816-gemini-constellation.html
    // (3) 21. Mai – 21. Juni
    private fun registerGemini() {
        universe.constellations[Constellation.Gemini]
            .line(Symbol.Theta, Symbol.Tau, Symbol.Iota, Symbol.Upsilon, Symbol.Kappa)
            .line(Symbol.Beta, Symbol.Upsilon, Symbol.Delta, Symbol.Zeta, Symbol.Gamma)
            .line(Symbol.Delta, Symbol.Lambda, Symbol.Xi)
            .line(Symbol.Alpha, Symbol.Tau, Symbol.Epsilon, Symbol.Nu)
            .line(Symbol.Epsilon, Symbol.Mu, Symbol.Eta)
    }

    // Krebs
    // https://en.wikipedia.org/wiki/Cancer_(constellation)
    // https://wissen.naanoo.de/esoterik/sternzeichen-krebs
    // (4) 22. Juni – 22. Juli
    private fun registerCancer() {
        universe.constellations[Constellation.Cancer]
            .line(Symbol.Iota, Symbol.Gamma, Symbol.Delta, Symbol.Beta)
            .line(Symbol.Delta, Symbol.Alpha)
    }

    // Löwe
    // https://en.wikipedia.org/wiki/Leo_(constellation)
    // https://wissen.naanoo.de/esoterik/sternzeichen-loewe
    // (5) 23. Juli – 23. August
    private fun registerLeo() {
        universe.constellations[Constellation.Leo]
            .line(Symbol.Alpha, Symbol.Eta, Symbol.Gamma, Symbol.Delta, Symbol.Beta)
            .line(Symbol.Gamma, Symbol.Zeta, Symbol.Mu, Symbol.Epsilon)
    }

    // Jungfrau
    // https://en.wikipedia.org/wiki/List_of_stars_in_Virgo
    // https://wissen.naanoo.de/esoterik/sternzeichen-jungfrau
    // (6) 24. August – 23. September
    private fun registerVirgo() {
        universe.constellations[Constellation.Virgo]
            .line(Symbol.Alpha, Symbol.Theta, Symbol.Gamma, Symbol.Eta, Symbol.Beta, Symbol.Nu)
            .line(Symbol.Epsilon, Symbol.Delta, Symbol.Gamma)
            .line(Symbol.Zeta, Symbol.Theta)
    }

    // Waage (Libra)
    // https://en.wikipedia.org/wiki/Libra_(constellation)
    // https://wissen.naanoo.de/esoterik/sternzeichen-waage
    // (7) 24. September – 23. October
    private fun registerLibra() {
        universe.constellations[Constellation.Libra]
            .line(Symbol.Sigma, Symbol.Alpha, Symbol.Beta, Symbol.Gamma, Symbol.Tau, Symbol.Upsilon)
            .line(Symbol.Alpha, Symbol.Gamma)
    }

    // Skorpion
    // https://en.wikipedia.org/wiki/Scorpius
    // https://wissen.naanoo.de/esoterik/sternzeichen-skorpion
    // (8) 24. October – 22. November
    private fun registerScorpius() {
        universe.constellations[Constellation.Scorpius]
            .line(Symbol.Sigma, Symbol.Nu)
            .line(Symbol.Sigma, Symbol.Beta)
            .line(Symbol.Sigma, Symbol.Delta)
            .line(Symbol.Sigma, Symbol.Pi)
            .line(Symbol.Sigma, Symbol.Rho)
            .line(Symbol.Sigma, Symbol.Alpha, Symbol.Tau, Symbol.Epsilon, Symbol.Mu, Symbol.Zeta, Symbol.Eta, Symbol.Theta, Symbol.Iota, Symbol.Kappa, Symbol.Lambda)
            .line(Symbol.Kappa, Symbol.Upsilon)
    }

    // Schütze (german)
    // Archer (english)
    // see also "Teapot"
    // https://en.wikipedia.org/wiki/Sagittarius_(constellation)
    // https://wissen.naanoo.de/esoterik/sternzeichen-schuetze
    // (9) 23. November – 21. December
    private fun registerSagittarius() {
        universe.constellations[Constellation.Sagittarius]
            .line(Symbol.Eta, Symbol.Epsilon, Symbol.Delta, Symbol.Lambda, Symbol.Mu)
            .line(Symbol.Lambda, Symbol.Phi, Symbol.Sigma)
            .line(Symbol.Xi, Symbol.Pi, Symbol.Sigma, Symbol.Tau, Symbol.Zeta)
        // Although it's quite funny, I favour the clear appearance of the sagittarius
        // com.stho.nyota.R.drawable.constellation_teapot)
    }

    // Steinbock (german)
    // The Sea-Goat (englisch)
    // https://en.wikipedia.org/wiki/Capricornus
    // https://wissen.naanoo.de/esoterik/sternzeichen-steinbock
    // (10) 22. December – 20. January
    private fun registerCapricornus() {
        universe.constellations[Constellation.Capricornus]
            .line(Symbol.Alpha, Symbol.Beta, Symbol.Psi, Symbol.Omega, Symbol.Zeta, Symbol.Epsilon, Symbol.Delta, Symbol.Gamma, Symbol.Theta, Symbol.Alpha)
    }

    // Wassermann (german)
    // The Water-Bearer (englisch)
    // https://en.wikipedia.org/wiki/Aquarius_(constellation)
    // https://wissen.naanoo.de/esoterik/sternzeichen-wassermann
    // (11) 21. January – 19. February
    private fun registerAquarius() {
        universe.constellations[Constellation.Aquarius]
            .line(Symbol.Epsilon, Symbol.Mu, Symbol.Beta, Symbol.Alpha, Symbol.Pi, Symbol.Zeta, Symbol.Eta)
            .line(Symbol.Zeta, Symbol.Gamma, Symbol.Alpha)
            .line(Symbol.Beta, Symbol.Iota)
            .line(Symbol.Alpha, Symbol.Theta, Symbol.Lambda, Symbol.Tau, Symbol.Delta, Symbol.Psi, Symbol.Chi, Symbol.Phi, Symbol.Lambda)
    }

    // Fische (german)
    // Fishes (english)
    // https://en.wikipedia.org/wiki/Pisces_(constellation)
    // https://wissen.naanoo.de/esoterik/sternzeichen
    // (12) 20. February – 20. March
    private fun registerPisces() {
        universe.constellations[Constellation.Pisces]
            .line(Symbol.Iota, Symbol.Lambda, Symbol.Kappa, Symbol.Gamma, Symbol.Theta, Symbol.Iota, Symbol.Omega, Symbol.Delta, Symbol.Epsilon, Symbol.Zeta, Symbol.Mu, Symbol.Nu, Symbol.Alpha, Symbol.Xi, Symbol.Omicron, Symbol.Pi, Symbol.Eta, Symbol.Rho, Symbol.Phi, Symbol.Tau, Symbol.Upsilon, Symbol.Phi)
    }

    // (Fuhrmann)
    // https://en.wikipedia.org/wiki/Auriga_(constellation)
    private fun registerAuriga() {
        universe.constellations[Constellation.Auriga]
            .line(Symbol.Iota, Symbol.Epsilon, Symbol.Alpha, Symbol.Beta, Symbol.Theta, Symbol.Iota)
    }

    // Perseus
    // https://en.wikipedia.org/wiki/Perseus_(constellation)
    private fun registerPerseus() {
        universe.constellations[Constellation.Perseus]
            .line(Symbol.Rho, Symbol.Beta, Symbol.Kappa, Symbol.Iota, Symbol.Tau, Symbol.Eta, Symbol.Gamma, Symbol.Alpha, Symbol.Delta, Symbol.Epsilon, Symbol.Xi, Symbol.Zeta, Symbol.Omicron)
            .line(Symbol.Lambda, Symbol.Mu, Symbol.Delta)
            .line(Symbol.Alpha, Symbol.Iota, Symbol.Theta)
            .line(Symbol.Epsilon, Symbol.Beta)
    }

    // Musca = Fly
    // https://en.wikipedia.org/wiki/Musca#/media/File:Musca_IAU.svg
    private fun registerMusca() {
        universe.constellations[Constellation.Musca]
            .line(Symbol.Lambda, Symbol.Mu, Symbol.Epsilon, Symbol.Alpha, Symbol.Beta, Symbol.Delta, Symbol.Gamma, Symbol.Alpha)
    }

    // https://de.wikipedia.org/wiki/Cham%C3%A4leon_(Sternbild)
    private fun registerChamaeleon() {
        universe.constellations[Constellation.Chamaeleon]
            .line(Symbol.Alpha, Symbol.Theta, Symbol.Gamma, Symbol.Delta, Symbol.Beta, Symbol.Epsilon, Symbol.Gamma)
    }

    // https://de.wikipedia.org/wiki/Zentaur_(Sternbild)
    private fun registerCentaurus() {
        universe.constellations[Constellation.Centaurus]
            .line(Symbol.Pi, Symbol.Delta, Symbol.Sigma, Symbol.Rho, Symbol.Omicron)
            .line(Symbol.Sigma, Symbol.Gamma, Symbol.Epsilon, Symbol.Beta, Symbol.Alpha)
            .line(Symbol.Tau, Symbol.Zeta, Symbol.Mu, Symbol.Nu, Symbol.Theta, Symbol.Psi, Symbol.Chi, Symbol.Phi, Symbol.Upsilon, Symbol.Zeta, Symbol.Epsilon)
            .line(Symbol.Iota, Symbol.Nu)
            .line(Symbol.Phi, Symbol.Eta)
    }

    // https://de.wikipedia.org/wiki/Pfau_(Sternbild)
    // https://en.wikipedia.org/wiki/List_of_stars_in_Pavo
    private fun registerPavo() {
        universe.constellations[Constellation.Pavo]
            .line(Symbol.Alpha, Symbol.Beta, Symbol.Delta, Symbol.Lambda, Symbol.Eta, Symbol.Zeta, Symbol.Epsilon, Symbol.Beta)
    }

    // https://de.wikipedia.org/wiki/Eridanus_(Sternbild)
    // https://de.wikipedia.org/wiki/Achernar
    private fun registerEridanus() {
        universe.constellations[Constellation.Eridanus]
            .line(Symbol.Alpha, Symbol.Chi, Symbol.Phi, Symbol.Kappa, Symbol.Iota, Symbol.Upsilon, Symbol.Tau)
            .line(Symbol.Tau, Symbol.Eta, Symbol.Epsilon, Symbol.Delta, Symbol.Gamma, Symbol.Nu, Symbol.Mu, Symbol.Beta)
    }

    // https://de.wikipedia.org/wiki/B%C3%A4renh%C3%BCter#/media/Datei:Bootes_constellation_map.png
    private fun registerBootes() {
        universe.constellations[Constellation.Bootes]
            .line(Symbol.Alpha, Symbol.Rho, Symbol.Gamma, Symbol.Beta, Symbol.Delta, Symbol.Epsilon, Symbol.Alpha)
            .line(Symbol.Tau, Symbol.Eta, Symbol.Alpha, Symbol.Zeta)
    }

    // https://www.iau.org/public/themes/constellations/
    private fun registerAntlia() {
        universe.constellations[Constellation.Antlia]
            .line(Symbol.Epsilon, Symbol.Alpha, Symbol.Iota)
    }

    private fun registerApus() {
        universe.constellations[Constellation.Apus]
            .line(Symbol.Alpha, Symbol.Delta, Symbol.Beta, Symbol.Gamma, Symbol.Delta)
    }

    // https://www.space.com/32054-satellite-tracker.html
    // https://www.space.com/6870-spot-satellites.html
    // ISS
    // Hubble
    // Cheops
    // Terra (earth observing)
    // KMS-4 (North Korea)
    // NOAA-15 (weather observing)
    // Aqua
    // X-37B (Air Force)
    // NOAA-18
    // ALOS (Japan)
    // Landsat 8
    // NOAA-19
    private fun registerSatellite() {

        // TLE from NORAD, Jan 18 2017, 19:27
        newSatellite("ISS", "International Space Station", 25544, "1 25544U 98067A   17018.16759178  .00002139  00000-0  39647-4 0  9999\r\n2 25544  51.6445  66.1799 0007746  95.7219  10.7210 15.54083144 38425")

        // TLE from AMSAT
        newSatellite("HST", "Hubble Space Telescope", 20580,"1 20580U 90037B   20157.78380640 +.00000391 +00000-0 +12799-4 0  9990\r\n2 20580 028.4706 036.8479 0002552 328.6431 091.6588 15.09393144454203")

        // TLE from https://www.celestrak.com/NORAD/elements/active.txt
        newSatellite("CHEOPS", "Cheops Telescope", 44874, "1 44874U 19092B   20300.77885576  .00000042  00000-0  19668-4 0  9996\r\n2 44874  98.2344 123.7172 0011612  35.8637 324.3338 14.56820200 45626")
    }

    private fun registerGalaxies() {
        newGalaxy("Andromeda Galaxy", com.stho.nyota.R.drawable.galaxy_andromeda, "00h 42m 44.31", "+41° 16′ 09.4", 3.44, 2540000.0)

        // TODO add other galaxies
        // TODO draw galaxies
    }

    private fun registerAnything() {

        // seven sisters,
        // https://en.wikipedia.org/wiki/Pleiades
        newAnything("Pleiades", ascension = "03h 47m 24", "+24° 07′ 00", 1.6, 444.0)

        // https://en.wikipedia.org/wiki/Orion_Nebula
        newAnything("Orion Nebula", ascension = "05h 35m 17.3", "−05° 23′ 28", 4.0, 1344.0)

    }

    private fun registerSpecialElements() {
        newSpecialElement("S", Hour.fromDegree(0.0), Degree.fromNegative(90, 0, 0.0))
        newSpecialElement("N", Hour.fromDegree(0.0), Degree.fromPositive(90, 0, 0.0))
    }

    private fun rebuild() {
        universe.constellations.values.forEach { it.build() }
    }
}