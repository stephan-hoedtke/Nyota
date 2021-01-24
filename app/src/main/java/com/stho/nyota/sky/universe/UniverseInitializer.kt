package com.stho.nyota.sky.universe

import com.stho.nyota.sky.utilities.Degree
import com.stho.nyota.sky.utilities.Hour


// TODO Make the UniverseInitialize a set of internal and private methods in Universe
/**
 * Created by shoedtke on 31.08.2016.
 */
class UniverseInitializer(universe: Universe) : AbstractUniverseInitializer(universe) {

    // https://en.wikipedia.org/wiki/88_modern_constellations
    // https://wissen.naanoo.de/esoterik/sternzeichen
    // https://www.sternfreunde-muenster.de/
    fun initialize() {
        registerVIP()
        registerAndromeda()
        registerAquarius()
        registerAquila()
        registerAra()
        registerAries()
        registerAuriga()
        registerCamaeleon()
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
        registerPleiades()
        registerSagittarius()
        registerScorpius()
        registerTaurus()
        registerTriangulumAustrale()
        registerUrsMajor()
        registerUrsMinor()
        registerVirgo()
        registerSatellite()
        registerSpecialElements()
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
        asVIP(newStar("Achernar", Symbol.Alpha, "01h 37m 42.85", "-57° 14′ 12.3", 0.50)) // Eridanus
        asVIP(newStar("Acrux", Symbol.Alpha, "12h 26m 35.99", "-63° 5' 56.7", 0.76)) // Crux
        asVIP(newStar("Aldebaran", Symbol.Alpha, "4h 35m 55.24", "+16° 30' 33.5", 0.87)) // Taurus
        asVIP(newStar("Alpha Centauri", Symbol.Three, "14h 39m 36.49400", "–60° 50′ 02.3737", -0.27)) // Centaurus
        asVIP(newStar("Altair", Symbol.Alpha, "19h 50m 46.99855", "+08° 52′ 05.9563", 0.77)) // Aquila
        asVIP(newStar("Antares", Symbol.Alpha, "16h 29m 24.45970", "−26° 25′ 55.2094", 0.96)) // Scorpius
        asVIP(newStar("Arcturus", Symbol.Four, "14h 15m 39.7", "+19° 10′ 56.0", -0.05)) // Boötus
        asVIP(newStar("Becrux", Symbol.Beta, "12h 47m 43.3", "-59° 41' 20", 1.25)) // Crux
        asVIP(newStar("Betelgeuse", Symbol.Alpha, "05h 55m 10.30536", "+07° 24′ 25.4304", 0.50)) // Orion
        asVIP(newStar("Castor", Symbol.Alpha, "07h 34m 35.863", "+31° 53′ 17.79", 1.57)) // Gemini
        asVIP(newStar("Canopus", Symbol.Two, "06h 23m 57.10988", "−52° 41′ 44.3810", -0.74)) // Carina
        asVIP(newStar("Capella", Symbol.Alpha, "05h 16m 41.30", "+45° 59′ 56.5", 0.08)) // Auriga
        asVIP(newStar("Deneb", Symbol.Alpha, "20h 41m 25.91", "+45° 16′ 49.2", 1.25)) // Cygnus
        // "Procyon", ..., brightness: 0.38) // Canis Minor
        asVIP(newStar("Gacrux", Symbol.Gamma, "12h 31m 10.0", "-57° 6' 48", 1.63)) // Crux
        asVIP(newStar("Hadar", Symbol.Beta , "14h 03m 49.4", "-60° 22' 23", 0.61)) // Centaurus
        // Formalhaut, 1.16 // Piscis Austrinus
        // Adhara, 1.50 // Canis Major
        // Shaula, 1.63 // Scorpius
        asVIP(newStar("Polaris", Symbol.Alpha, "2h 31m 49.09", "+89° 15′ 50.8", 1.98))
        asVIP(newStar("Pollux", Symbol.Beta, "07h 45m 18.94987", "+28° 01′ 34.3160", 1.14)) // Gemini
        // Source: https://www.space.com/21640-star-luminosity-and-magnitude.html
        asVIP(newStar("Regulus", Symbol.Alpha, "10h 08m 22.311", "+11° 58′ 01.95", 1.35)) // Leo
        asVIP(newStar("Rigel", Symbol.Beta, "05h 14m 32.27210", "−08° 12′ 05.8981", 0.12)) // Orion
        asVIP(newStar("Sirius", Symbol.One, "6h 45m 8.82", "-16° 42' 56.9", -1.46)) // Canis Major
        asVIP(newStar("Spica", Symbol.Alpha, "13h 25m 11.60", "−11° 09′ 40.5", 0.98)) // Virgo
        asVIP(newStar("Vega", Symbol.Five, "18h 36m 56.33635", "+38° 47′ 01.2802", +0.026)) // Lyra
    }

    private fun registerCrux() {
        val alpha = getStar("Acrux")
        val beta = getStar("Becrux")
        val gamma = getStar("Gacrux")
        val delta = newStar("Decrux", Symbol.Delta, "12h 15m 8.7", "-58° 44' 56", 2.79)
        val epsilon = newStar(Symbol.Epsilon, "12h 21m 21.61", "-60° 24' 4.1", 3.59)
        newConstellation("Crux", com.stho.nyota.R.drawable.constellation_cross)
            .register(alpha, gamma)
            .register(beta, delta)
            .register(epsilon)
            .translate(Language.Latin, "Crux")
            .translate(Language.German, "Kreuz des Südens")
            .translate(Language.English, "Southern Cross")
            .build()
     }

    // https://en.wikipedia.org/wiki/Triangulum_Australe
    private fun registerTriangulumAustrale() {
        val alpha = newStar(Symbol.Alpha, "16h 48m 39.89508", "–69° 01′ 39.7626", 1.91)
        val beta = newStar(Symbol.Beta, "15h 55m 08.56206", "−63° 25′ 50.6155", 2.85)
        val gamma = newStar(Symbol.Gamma, "15h 18m 54.58198", "–68° 40′ 46.3654", 2.87)
        val epsilon = newStar(Symbol.Epsilon, "15h 36m 43.22223", "−66° 19′ 01.3334", 4.11)
        newConstellation("Triangulum Australe", com.stho.nyota.R.drawable.constellation_triangulum_australe)
            .register(alpha, gamma, epsilon, beta, alpha)
            .translate(Language.Latin, "Triangulum Australe")
            .translate(Language.German, "Südliches Dreieck")
            .translate(Language.English, "Southern triangle")
            .build()
    }

    // https://en.wikipedia.org/wiki/Saiph
    // http://www.sternenhimmel-aktuell.de/Orion.htm
    private fun registerOrion() {
        val alpha = getStar("Betelgeuse")
        val gamma = newStar("Bellatrix", Symbol.Gamma, "05h 25m 07.86325", "+06° 20′ 58.9318", 1.64)
        val beta = getStar("Rigel")
        val kappa = newStar("Saiph", Symbol.Kappa, "05h 47m 45.38884", "−09° 40′ 10.5777", 2.09)
        val zeta = newStar("Alnitak", Symbol.Zeta, "05h 40m 45.52666", "−01° 56′ 34.2649", 1.77)
        val epsilon = newStar("Alnilam", Symbol.Epsilon, "05h 36m 12.8", "−01° 12′ 06.9", 1.69)
        val delta = newStar("Mintaka", Symbol.Delta, "05h 32m 00.40009", "−00° 17′ 56.7424", 2.23)
        val lambda = newStar("Meissa", Symbol.Lambda, "05h 35m 08.27761", "+09° 56′ 02.9611", 3.33)
        newConstellation("Orion", com.stho.nyota.R.drawable.constellation_orion)
            .register(kappa, zeta, alpha, gamma, delta, beta)
            .register(zeta, epsilon, delta)
            .register(lambda)
            .translate(Language.Latin, "Orion")
            .translate(Language.German, "Orion")
            .translate(Language.English, "Orion, the hunter")
            .build()
    }

    // https://en.wikipedia.org/wiki/Ursa_Major
    private fun registerUrsMajor() {
        val eta = newStar("Alkaid", Symbol.Eta, "13h 47m 32.43776", "+49° 18′ 47.7602", 1.86)
        val zeta = newStar("Mizar", Symbol.Zeta, "13h 23m 55.54048", "+54° 55′ 31.2671", 2.04)
        val epsilon = newStar("Alioth", Symbol.Epsilon, "12h 54m 01.74959", "+55° 57′ 35.3627", 1.77)
        val delta = newStar("Megrez", Symbol.Delta, "12h 15m 25.56063", "+57° 01′ 57.4156", 3.31)
        val gamma = newStar("Phecda", Symbol.Gamma, "11h 53m 49.84732", "+53° 41′ 41.1350", 2.44)
        val beta = newStar("Merak", Symbol.Beta, "11h 01m 50.47654", "+56° 22′ 56.7339", 2.37)
        val alpha = newStar("Dubhe", Symbol.Alpha, "11h 03m 43.67152", "+61° 45′ 03.7249", 1.79)
        newConstellation("Urs Major", com.stho.nyota.R.drawable.constellation_urs_major)
            .register(eta, zeta, epsilon, delta, gamma, beta, alpha, delta)
            .translate(Language.Latin, "Urs Major")
            .translate(Language.German, "Grosser Bär")
            .translate(Language.English, "Big bear")
            .build()
    }

    // https://en.wikipedia.org/wiki/Ursa_Minor
    private fun registerUrsMinor() {
        val polaris = getStar("Polaris")
        val delta = newStar("Yildun", Symbol.Delta, "17h 32m 12.99671", "+86° 35′ 11.2584", 4.36)
        val epsilon = newStar(Symbol.Epsilon, "16h 45m 58.24168", "+82° 02′ 14.1233", 4.19)
        val zeta = newStar("Ahfa al Farkadain", Symbol.Zeta, "15h 44m 03.5193", "+77° 47′ 40.175", 4.32)
        val beta = newStar("Kochab", Symbol.Beta, "14h 50m 42.32580", "+74° 09′ 19.8142", 2.08)
        val gamma = newStar("Pherkad", Symbol.Gamma, "15h 20m 43.71604", "+71° 50′ 02.4596", 3.05)
        val eta = newStar("Anwar Al Farkadain", Symbol.Eta, "16h 17m 30.28696", "+75° 45′ 19.1885", 4.95)
        newConstellation("Urs Minor", com.stho.nyota.R.drawable.constellation_urs_minor)
            .register(polaris, delta, epsilon, zeta, beta, gamma, eta, zeta)
            .translate(Language.Latin, "Urs Minor")
            .translate(Language.German, "Kleiner Bär")
            .translate(Language.English, "Little bear")
            .build()
    }

    private fun registerCassiopeia() {
        val beta = newStar("Caph", Symbol.Beta, "00h 09m 10.68518", "+59° 08′ 59.2120", 2.28)
        val alpha = newStar("Schedar", Symbol.Alpha, "00h 40m 30.4405", "+56° 32′ 14.392", 2.24)
        val gamma = newStar(Symbol.Gamma, "00h 56m 42.50108", "+60° 43′ 00.2984", 2.47)
        val delta = newStar("Ruchbah", Symbol.Delta, "01h 25m 48.95147", "+60° 14′ 07.0225", 2.68)
        val epsilon = newStar("Segin", Symbol.Epsilon, "01h 54m 23.72567", "+63° 40′ 12.3628", 3.37)
        newConstellation("Cassiopeia", com.stho.nyota.R.drawable.constellation_cassiopeia)
            .register(beta, alpha, gamma, delta, epsilon)
            .translate(Language.Latin, "Cassiopeia")
            .translate(Language.German, "Kassiopeia")
            .translate(Language.English, "Queen of Ethiopia")
            .build()
    }

    // seven sisters,
    // https://en.wikipedia.org/wiki/Pleiades
    private fun registerPleiades() {
        val alcyone = newStar("Alcyone", Symbol.One, "03h 47m 29.077", "+24° 06′ 18.49", 2.87)
        val electra = newStar("Electra", Symbol.Two, "03h 44m 52.53688", "+24° 06′ 48.0112", 3.70)
        val atlas = newStar("Atlas", Symbol.Three, "03h 49m 09.74258", "+24° 03′ 12.3003", 3.63)
        val maia = newStar("Maia", Symbol.Four, "03h 45m 49.6067", "+24° 22′ 03.895", 3.871)
        val merope = newStar("Merope", Symbol.Five, "03h 46m 19.57384", "+23° 56′ 54.0812", 4.18)
        val taygeta = newStar("Taygeta", Symbol.Six, "03h 45m 12.49578", "+24° 28′ 02.2097", 4.30)
        val celaeno = newStar("Celaeno", Symbol.Seven, "03h 44m 48.2154", "+24° 17′ 22.093", 5.448)
        val pleione = newStar("Pleione", Symbol.Eight, "03h 49m 11.2161", "+24° 08′ 12.163", 5.048)
        val sterope = newStar("Sterope", Symbol.Nine, "03h 46m 02.90030", "+24° 31′ 40.4313", 6.43)
        newConstellation("Pleiades", com.stho.nyota.R.drawable.constellation_pleiades)
            .register(alcyone)
            .register(electra)
            .register(atlas)
            .register(maia)
            .register(merope)
            .register(taygeta)
            .register(celaeno)
            .register(pleione)
            .register(sterope)
            .translate(Language.Latin, "Pleiades") // TODO in German in English?
            .build()
    }

    // https://en.wikipedia.org/wiki/Pegasus_(constellation)
    private fun registerPegasus() {
        val alpha = newStar(Symbol.Alpha, "23h 04m 45.65345", "+15° 12′ 18.9617", 2.48)
        val beta = newStar(Symbol.Beta, "23h 03m 46.45746", "+28° 04′ 58.0336", 2.42)
        val gamma = newStar(Symbol.Gamma, "00h 13m 14.15123", "+15° 11′ 00.9368", 2.84)
        // ALPHA Andromedae = DELTA Pegasi
        val andromeda = newStar(Symbol.Delta, "00h 08m 23.25988", "+29° 05′ 25.5520", 2.06)
        val epsilon = newStar(Symbol.Epsilon, "21h 44m 11.15614", "+09° 52′ 30.0311", 2.399)
        val zeta = newStar(Symbol.Zeta, "22h 41m 27.72072", "+10° 49′ 52.9079", 3.414)
        val eta = newStar(Symbol.Eta, "22h 43m 00.13743", "+30° 13′ 16.4822", 2.95)
        val pi = newStar(Symbol.Pi, "22h 09m 13.633", "+33° 10′ 20.41", 5.595)
        val iota = newStar(Symbol.Iota, "22h 07m 00.666", "+25° 20′ 42.40", 3.84)
        val kappa = newStar( Symbol.Kappa, "21h 44m 38.7344", "+25° 38′ 42.128", 4.159)
        val theta = newStar(Symbol.Theta, "22h 10m 11.98528", "+06° 11′ 52.3078", 3.53)
        val mu = newStar(Symbol.Mu, "22h 50m 00.19315", "+24° 36′ 05.6984", 3.514)
        val tau = newStar(Symbol.Tau, "23h 20m 38.24188", "+23° 44′ 25.2098", 4.58)
        val lambda = newStar(Symbol.Lambda, "22h 46m 31.87786", "+23° 33′ 56.3561", 3.65)
        val upsilon = newStar(Symbol.Upsilon, "23h 25m 22.78350", "+23° 24′ 14.7606", 4.40)
        newConstellation("Pegasus", com.stho.nyota.R.drawable.constellation_pegasus)
            .register(epsilon, theta, zeta, alpha, gamma, andromeda, beta, eta, pi)
            .register(beta, mu, lambda, iota, kappa)
            .register(alpha, beta)
            .register(tau)
            .register(upsilon)
            .translate(Language.Latin, "Pegasus")
            .translate(Language.German, "Pegasus")
            .translate(Language.English, "Pegasus, the winged horse")
            .build()
    }

    // https://en.wikipedia.org/wiki/Andromeda_(constellation)
    private fun registerAndromeda() {
        // ALPHA Andromedae = DELTA Pegasi
        val andromeda = newStar(Symbol.Alpha, "00h 08m 23.25988", "+29° 05′ 25.5520", 2.06)
        val delta = newStar(Symbol.Delta, "00h 39m 19.67518", "+30° 51′ 39.6783", 3.28)
        val beta = newStar(Symbol.Beta, "01h 09m 43.92388", "+35° 37′ 14.0075", 2.05)
        val gamma = newStar(Symbol.Gamma, "02h 03m 53.9531", "+42° 19′ 47.009", 2.10)
        val eta = newStar(Symbol.Eta, "00h 57m 12.4000", "+23° 25′ 03.533", 4.403)
        val zeta = newStar(Symbol.Zeta, "00h 47m 20.32547", "+24° 16′ 01.8408", 3.92)
        val epsilon = newStar(Symbol.Epsilon, "00h 38m 33.34610", "+29° 18′ 42.3135", 4.357)
        val pi = newStar(Symbol.Pi, "00h 36m 52.84926", "+33° 43′ 09.6384", 4.36)
        val iota = newStar(Symbol.Iota, "23h 38m 08.20130", "+43° 16′ 05.0649", 4.29)
        val kappa = newStar(Symbol.Kappa, "23h 40m 24.50763", "+44° 20′ 02.1566", 4.139)
        val lambda = newStar(Symbol.Lambda, "23h 37m 33.84261", "+46° 27′ 29.3380", 3.65)
        newConstellation("Andromeda", com.stho.nyota.R.drawable.constellation_andromeda)
            .register(andromeda, delta, beta, gamma)
            .register(eta, zeta, epsilon, delta, pi, iota, kappa, lambda)
            .translate(Language.Latin, "Andromeda")
            .translate(Language.German, "Andromeda")
            .translate(Language.English, "Princess of Ethiopia")
            .build()
    }

    // https://en.wikipedia.org/wiki/List_of_stars_in_Aquila
    private fun registerAquila() {
        val altair = getStar("Altair")
        val beta = newStar(Symbol.Beta, "19h 55m 18.79256", "+06° 24′ 24.3425", 3.87)
        val gamma = newStar(Symbol.Gamma, "19h 46m 15.58029", "+10° 36′ 47.7408", 2.712)
        val zeta = newStar(Symbol.Zeta, "19h 05m 24.60802", "+13° 51′ 48.5182", 2.983)
        val theta = newStar(Symbol.Theta, "20h 11m 18.28528", "–00° 49′ 17.2626", 3.26)
        val eta = newStar(Symbol.Eta, "19h 52m 28.36775", "+01° 00′ 20.3696", 3.87)
        val delta = newStar(Symbol.Delta, "19h 25m 29.90139", "+03° 06′ 53.2061", 3.365)
        val lambda = newStar(Symbol.Lambda, "19h 06m 14.93898", "−04° 52′ 57.2007", 3.43)
        newConstellation("Aquila", com.stho.nyota.R.drawable.constellation_aquila)
            .register(altair, zeta, delta, altair, theta, eta, delta, lambda)
            .register(beta)
            .register(gamma)
            .translate(Language.Latin, "Aquila")
            .translate(Language.German, "Adler")
            .translate(Language.English, "Eagle")
            .build()
    }

    // Schwan (Cygnus)
    // https://en.wikipedia.org/wiki/Cygnus_(constellation)
    private fun registerCygnus() {
        val alpha = getStar("Deneb")
        val gamma = newStar(Symbol.Gamma, "20h 22m 13.70184", "+40° 15′ 24", 2.23)
        val eta = newStar(Symbol.Eta, "19h 56m 18.37222", "+35° 05′ 00.3228", 3.89)
        val beta = newStar("Albireo", Symbol.Beta, "19h 30m 43.286", "+27° 57′ 34.84", 3.18)
        val zeta = newStar(Symbol.Zeta, "21h 12m 56.18594", "+30° 13′ 36.8957", 3.26)
        val epsilon = newStar(Symbol.Epsilon, "20h 46m 12.68236", "+33° 58′ 12.9250", 2.48)
        val delta = newStar(Symbol.Delta, "19h 44m 58.47854", "+45° 07′ 50.9161", 2.87)
        val iota = newStar(Symbol.Iota, "19h 29m 42.36", "+51° 43′ 47.2", 3.77)
        val kappa = newStar(Symbol.Kappa, "19h 17m 06.16865", "+53° 22′ 06.4534", 3.814)
        newConstellation("Cygnus", com.stho.nyota.R.drawable.constellation_cygnus)
            .register(alpha, gamma, eta, beta)
            .register(zeta, epsilon, gamma, delta, iota, kappa)
            .register(beta)
            .translate(Language.Latin, "Cygnus")
            .translate(Language.German, "Schwan")
            .translate(Language.English, "Swan")
            .build()
    }

    // Eidechse, Lacerta
    // https://en.wikipedia.org/wiki/Lacerta
    private fun registerLacerta() {
        val alpha = newStar(Symbol.Alpha, "22h 31m 17.5010", "+50° 16′ 56.969", 3.78)
        val beta = newStar(Symbol.Beta, "22h 23m 33.624", "+52° 13′ 44.56", 4.43)
        val x4 = newStar(Symbol.Four, "22h 24m 30.99068", "+49° 28′ 35.0176", 4.60)
        val x5 = newStar(Symbol.Five, "22h 29m 31.823", "+47° 42′ 24.79", 4.36)
        val x2 = newStar(Symbol.Two, "22h 21m 01.54727", "+46° 32′ 11.6461", 4.540)
        val x1 = newStar( Symbol.One, "22h 15m 58.17673", "+37° 44′ 55.4385", 4.15)
        val x6 = newStar(Symbol.Six, "22h 30m 29.26005", "+43° 07′ 24.1565", 4.52)
        val x11 = newStar(Symbol.Eleven, "22h 40m 30.85881", "+44° 16′ 34.7042", 4.46)
        // useless: Star _9 = newStar("9 Lacertae", "9", "22h 50m 21.77464s", "+41° 57′ 12.2181″", 5.928);
        // useless: Star _10
        // useless: Star _14 = newStar("14 Lacertae", "14", "22h 50m 21.77464s", "+41° 57′ 12.2181″", 5.928);
        // useless: Star _15 = newStar("15 Lacertae", "15", "22h 52m 02.03426s", "+43° 18′ 44.6962″", 4.96);
        // useless: V424
        // useless: Star _16 = newStar("16 Lacertae", "16", "22h 56m 23.62966s", "+41° 36′ 13.9438″", 5.58);
        // Alpha Lacertae
        val xi = newStar(Symbol.Xi, "22h 13m 52.72948", "+39° 42′ 53.7340", 4.49)
        // useless: Star yy = newStar("NGC 7243", "Y", "22h 15m 08.5s", "+49° 53′ 51″", 6.40);
        newConstellation("Lacerta", com.stho.nyota.R.drawable.constellation_lacerta)
            .register(beta, alpha, x4, x5, x2, x6, xi, x1)
            .register(x6, x11, x5)
            .register()
            .translate(Language.Latin, "Lacerta")
            .translate(Language.German, "Eidechse")
            .translate(Language.English, "Lizard")
            .build()
    }

    // Ara, Altar, southern
    // https://en.wikipedia.org/wiki/Ara_(constellation)
    // https://www.constellation-guide.com/constellation-list/lacerta-constellation/
    private fun registerAra() {
        val alpha = newStar(Symbol.Alpha, "17h 31m 50.49153", "−49° 52′ 34.1220", 2.76)
        val beta = newStar(Symbol.Beta, "17h 25m 17.98835", "−55° 31′ 47.5868", 2.84)
        val gamma = newStar(Symbol.Gamma, "17h 25m 23.65931", "–56° 22′ 39.8148", 3.34)
        val delta = newStar(Symbol.Delta, "17h 31m 05.91272", "–60° 41′ 01.8522", 3.62)
        val epsilon = newStar(Symbol.Epsilon, "16h 59m 35.04880", "–53° 09′ 37.5713", 4.068)
        val zeta = newStar(Symbol.Zeta, "16h 58m 37.21217", "–55° 59′ 24.5203", 3.13)
        val eta = newStar(Symbol.Eta, "16h 49m 47.15653", "–59° 02′ 28.9575", 3.76)
        newConstellation("Ara", com.stho.nyota.R.drawable.constellation_ara)
            .register(alpha, beta, gamma, delta, eta, zeta, epsilon, alpha)
            .translate(Language.Latin, "Ara")
            .translate(Language.German, "Altar")
            .translate(Language.English, "Altar")
            .build()
    }

    // Wasserschlange (hydra)
    // https://en.wikipedia.org/wiki/Hydra_(constellation)
    private fun registerHydra() {
        val alpha = newStar("Alphard", Symbol.Alpha, "09h 27m 35.2433", "−08° 39′ 30.969", 2.00)
        val beta = newStar(Symbol.Beta, "11h 52m 54.521", "−33° 54′ 29.25", 4.276)
        val gamma = newStar(Symbol.Gamma, "13h 18m 55.29719", "–23° 10′ 17.4514", 2.993)
        val delta = newStar(Symbol.Delta, "08h 37m 39.36627", "+05° 42′ 13.6057", 4.146)
        val epsilon = newStar("Ashlesha", Symbol.Epsilon, "08h 46m 46.51223", "+06° 25′ 07.6855", 3.38)
        val zeta = newStar(Symbol.Zeta, "08h 55m 23.62614", "+05° 56′ 44.0354", 3.10)
        val eta = newStar(Symbol.Eta, "08h 43m 13.47499", "+03° 23′ 55.1867", 4.294)
        val theta = newStar( Symbol.Theta, "09h 14m 21.79", "+02° 18′ 54.1", 3.888)
        val iota = newStar("Ukdah", Symbol.Iota, "09h 39m 51.36145", "−01° 08′ 34.1135", 3.91)
        val kappa = newStar(Symbol.Kappa, "09h 40m 18.36496", "−14° 19′ 56.2675", 5.06)
        val lambda = newStar(Symbol.Lambda, "10h 10m 35.27667", "−12° 21′ 14.6938", 3.61)
        val mu = newStar(Symbol.Mu, "10h 26m 05.42630", "−14° 19′ 56.2675", 3.83)
        val nu = newStar(Symbol.Nu, "10h 49m 37.48875", "–16° 11′ 37.1360", 3.115)
        val rho = newStar(Symbol.RHO, "08h 48m 25.97057", "+05° 50′ 16.1283", 4.34)
        val upsilon = newStar("Zhang", Symbol.Upsilon, "09h 51m 28.69384", "−14° 50′ 47.7710", 4.12)
        val tau = newStar(Symbol.Tau, "09h 29m 08.89655", "−02° 46′ 08.2649", 4.59)
        val xi = newStar(Symbol.Xi, "11h 33m 00.11505", "−31° 51′ 27.4435", 3.54)
        val omicron = newStar( Symbol.Omichron, "11h 40m 12.78970", "−34° 44′ 40.7733", 4.70)
        val pi = newStar(Symbol.Pi, "14h 06m 22.29749", "–26° 40′ 56.5024", 3.25)
        val phi = newStar(Symbol.Phi, "10h 38m 34.95281", "−16° 52′ 35.6665", 4.90)
        val chi = newStar(Symbol.Chi, "11h 05m 19.90766", "−27° 17′ 36.9957", 4.94)
        val sigma = newStar("Minchir", Symbol.Sigma, "08h 38m 45.43747", "+03° 20′ 29.1701", 4.48)
        val psi = newStar(Symbol.Psi, "13h 09m 03.27026", "−23° 07′ 05.0501", 4.97)
        val omega = newStar(Symbol.Omega, "09h 05m 58.36642", "+05° 05′ 32.3360", 5.00)
        newConstellation("Hydra", com.stho.nyota.R.drawable.constellation_hydra)
            .register(rho, eta, sigma, delta, epsilon, zeta, theta, iota, alpha, upsilon, lambda, mu, phi, nu, chi, xi, beta, gamma, pi)
            .register(omega)
            .register(tau)
            .register(kappa)
            .register(omicron)
            .register(psi)
            .translate(Language.Latin, "Hydra")
            .translate(Language.German, "Wasserschlange")
            .translate(Language.English, "Sea serpent")
            .build()
    }

    // Widder, Aries
    // https://en.wikipedia.org/wiki/Aries_(constellation)
    // https://wissen.naanoo.de/esoterik/sternzeichen-widder
    // The Ram (englisch)
    // Aries (latein)
    // Krios (griechisch)
    // Mesa (sanskrit)
    // (1) 21. März – 20. April
    private fun registerAries() {
        val alpha = newStar("Hamal", Symbol.Alpha, "02h 07m 10.40570", "+23° 27′ 44.7032", 2.00)
        val beta = newStar(Symbol.Beta, "01h 54m 38.41099", "+20° 48′ 28.9133", 2.655)
        val gamma = newStar(Symbol.Gamma, "01h 53m 31.81479", "+19° 17′ 37.8790", 3.86)
        val delta = newStar(Symbol.Delta, "03h 11m 37.76465", "+19° 43′ 36.0397", 4.349)
        val epsilon = newStar(Symbol.Epsilon, "02h 59m 12.72536", "+21° 20′ 25.5575", 4.63)
        val zeta = newStar(Symbol.Zeta, "03h 14m 54.09731", "+21° 02′ 40.0103", 4.89)
        val x41 = newStar(Symbol.FortyOne, "02h 49m 59.03324", "+27° 15′ 37.8260", 3.63)
        newConstellation("Aries", com.stho.nyota.R.drawable.constellation_aries)
            .register(epsilon, x41, alpha, beta, gamma)
            .register(delta)
            .register(zeta)
            .translate(Language.Latin, "Aries")
            .translate(Language.German, "Widder")
            .translate(Language.English, "Ram")
            .build()
    }

    // Stier, Taurus
    // https://en.wikipedia.org/wiki/Taurus_(constellation)
    // https://deepsky.astronomie.info/Tau/index.de.php !
    // https://wissen.naanoo.de/esoterik/sternzeichen-stier
    // (2) 21. April – 20. Mai
    // The Bull (englisch)
    // Taurus (latein)
    // Tavros (griechisch)
    // Vrsabha (sanskrit)
    private fun registerTaurus() {
        val alpha = getStar("Aldebaran")!!
        val beta = newStar(Symbol.Beta, "05h 26m 17.51312", "+28° 36′ 26.8262", 1.65)
        val zeta = newStar(Symbol.Zeta, "05h 37m 38.68542", "+21° 08′ 33.1588", 3.01)
        val theta = newStar(Symbol.Theta, "04h 28m 34.49603", "+15° 57′ 43.8494", 3.84)
        val lambda = newStar(Symbol.Lambda, "04h 00m 40.81572", "+12° 29′ 25.2259", 3.47)
        val delta = newStar(Symbol.Delta, "04h 22m 56.09253", "+17° 32′ 33.0487", 3.772)
        val epsilon = newStar(Symbol.Epsilon, "04h 28m 37.00", "+19° 10′ 50", +3.53)
        val omicron = newStar(Symbol.Omichron, "03h 24m 48.79796", "+09° 01′ 43.9489", 3.61)
        val gamma = newStar(Symbol.Gamma, "04h 19m 47.6037", "+15° 37′ 39.512", 3.654)
        val xi = newStar(Symbol.Xi, "03h 27m 10.151", "+09° 43′ 57.63", 3.73)
        val tau = newStar(Symbol.Tau, "04h 42m 14.70161", "+22° 57′ 24.9214", 4.27)
        newConstellation("Taurus", com.stho.nyota.R.drawable.constellation_taurus)
            .register(zeta, alpha, theta, gamma, delta, epsilon, beta)
            .register(gamma, lambda, xi, omicron)
            .register(tau)
            .translate(Language.Latin, "Taurus")
            .translate(Language.German, "Stier")
            .translate(Language.English, "Bull")
            .build()
    }

    // Zwilling, Gemini
    // https://en.wikipedia.org/wiki/Gemini_(constellation)
    // https://deepsky.astronomie.info/Gem/index.de.php
    // https://wissen.naanoo.de/esoterik/sternzeichen-zwilling
    // https://www.space.com/16816-gemini-constellation.html
    // (3) 21. Mai – 21. Juni
    // The Twins (englisch)
    // Gemini (latein)
    // Didymoi (griechisch)
    // Mithuna (sanskrit)
    private fun registerGemini() {
        val alpha = getStar("Castor")
        val beta = getStar("Pollux")
        val theta = newStar(Symbol.Theta, "06h 52m 47.33887", "+33° 57′ 40.5175", 3.59)
        val tau = newStar(Symbol.Tau, "07h 11m 08.37042", "+30° 14′ 42.5831", 4.42)
        val iota = newStar(Symbol.Iota, "07h 25m 43.59532", "+27° 47′ 53.0929", 3.791)
        val upsilon = newStar(Symbol.Upsilon, "07h 35m 55.34970", "+26° 53′ 44.6751", 4.04)
        val kappa = newStar(Symbol.Kappa, "07h 44m 26.85357", "+24° 23′ 52.7872", 3.568)
        val delta = newStar(Symbol.Delta, "07h 20m 07.37978", "+21° 58′ 56.3377", 3.53)
        val zeta = newStar(Symbol.Zeta, "07h 04m 06.53079", "+20° 34′ 13.0739", 3.93)
        val gamma = newStar(Symbol.Gamma, "06h 37m 42.71050", "+16° 23′ 57.4095", 1.915)
        val lambda = newStar(Symbol.Lambda, "07h 18m 05.57977", "+16° 32′ 25.3905", 3.571)
        val xi = newStar(Symbol.Xi, "06h 45m 17.36432", "+12° 53′ 44.1311", 3.35)
        val epsilon = newStar(Symbol.Epsilon, "06h 43m 55.92626", "+25° 07′ 52.0515", 3.06)
        val nu = newStar(Symbol.Nu, "06h 28m 57.78613", "+20° 12′ 43.6856", 4.16)
        val mu = newStar(Symbol.Mu, "06h 22m 57.62686", "+22° 30′ 48.8979", 2.857)
        val eta = newStar(Symbol.Eta, "06h 14m 52.657", "+22° 30′ 24.48", 3.15)
        newConstellation("Gemini", com.stho.nyota.R.drawable.constellation_gemini)
            .register(theta, tau, iota, upsilon, kappa)
            .register(beta, upsilon, delta, zeta, gamma)
            .register(delta, lambda, xi)
            .register(alpha, tau, epsilon, nu)
            .register(epsilon, mu, eta)
            .translate(Language.Latin, "Gemini")
            .translate(Language.German, "Zwillinge")
            .translate(Language.English, "Twins")
            .build()
    }

    // Krebs, Cancer
    // https://en.wikipedia.org/wiki/Cancer_(constellation)
    // https://wissen.naanoo.de/esoterik/sternzeichen-krebs
    // (4) 22. Juni – 22. Juli
    // The Crab (englisch)
    // Cancer (latein)
    // Karkinos (griechisch)
    // Karkaṭa (sanskrit)
    private fun registerCancer() {
        val alpha = newStar(Symbol.Alpha, "08h 58m 29.2217", "+11° 51′ 27.723", 4.20)
        val beta = newStar(Symbol.Beta, "08h 16m 30.9206", "+09° 11′ 07.961", 3.50)
        val delta = newStar(Symbol.Delta, "08h 44m 41.09921", "+18° 09′ 15.5034", 3.94)
        val gamma = newStar(Symbol.Gamma, "08h 43m 17.14820", "+21° 28′ 06.6008", 4.652)
        val iota = newStar(Symbol.Iota, "08h 46m 41.81988", "+28° 45′ 35.6190", 4.02)
        val epsilon = newStar(Symbol.Epsilon, "08h 40m 27.01052", "+19° 32′ 41.3133", 6.29)
        newConstellation("Cancer", com.stho.nyota.R.drawable.constellation_cancer)
            .register(iota, gamma, delta, beta)
            .register(delta, alpha)
            .register(epsilon)
            .translate(Language.Latin, "Cancer")
            .translate(Language.German, "Krebs")
            .translate(Language.English, "Crab")
            .build()
    }

    // Löwe, Leo
    // https://en.wikipedia.org/wiki/Leo_(constellation)
    // https://wissen.naanoo.de/esoterik/sternzeichen-loewe
    // (5) 23. Juli – 23. August
    // The Lion (englisch)
    // Leo (latein)
    // Leōn (griechisch)
    // Siṃha (sanskrit)
    private fun registerLeo() {
        val alpha = getStar("Regulus")
        val eta = newStar(Symbol.Eta, "10h 07m 19.95186", "+16° 45′ 45.592", 3.486)
        val gamma = newStar(Symbol.Gamma, "10h 19m 58.35056", "+19° 50′ 29.3468", 2.08)
        val delta = newStar(Symbol.Delta, "11h 14m 06.50142", "+20° 31′ 25.3853", 2.56)
        val beta = newStar("Denebola", Symbol.Beta, "11h 49m 03.57834", "+14° 34′ 19.4090", 2.113)
        val zeta = newStar(Symbol.Zeta, "10h 16m 41.41597", "+23° 25′ 02.3221", 3.33)
        val mu = newStar(Symbol.Mu, "09h 52m 45.81654", "+26° 00′ 25.0319", 3.88)
        val epsilon = newStar(Symbol.Epsilon, "09h 45m 51.07330", "+23° 46′ 27.3208", 2.98)
        val theta = newStar(Symbol.Theta, "11h 14m 14.40446", "+15° 25′ 46.4541", 3.324)
        val iota = newStar(Symbol.Iota, "11h 23m 55.45273", "+10° 31′ 46.2195", 4.00)
        val sigma = newStar(Symbol.Sigma, "11h 21m 08.1943", "+06° 01′ 45.558", 4.046)
        val lambda = newStar( Symbol.Lambda, "09h 31m 43.22754", "+22° 58′ 04.6904", 4.32)
        val kappa = newStar(Symbol.Kappa, "09h 24m 39.25874", "+26° 10′ 56.3650", 4.460)
        val omicron = newStar(Symbol.Omichron, "09h 41m 09.03", "+09° 53′ 32.30", 3.52)
        val rho = newStar(Symbol.RHO, "10h 32m 48.67168", "+09° 18′ 23.7094", 3.83)
        newConstellation("Leo", com.stho.nyota.R.drawable.constellation_leo)
            .register(alpha, eta, gamma, delta, beta)
            .register(gamma, zeta, mu, epsilon)
            .register(theta)
            .register(iota)
            .register(sigma)
            .register(lambda)
            .register(kappa)
            .register(omicron)
            .register(rho)
            .translate(Language.Latin, "Leo")
            .translate(Language.German, "Löwe")
            .translate(Language.English, "Lion")
            .build()
    }

    // Jungfrau, Virgo
    // https://en.wikipedia.org/wiki/List_of_stars_in_Virgo
    // https://wissen.naanoo.de/esoterik/sternzeichen-jungfrau
    // (6) 24. August – 23. September
    // The Maiden (englisch)
    // Virgo (latein)
    // Parthenos (griechisch)
    // Kanyā (sanskrit)
    private fun registerVirgo() {
        val spica = getStar("Spica")
        val gamma = newStar(Symbol.Gamma, "12h 41m 39.64344", "–01° 26′ 57.7421", 2.74)
        val epsilon = newStar(Symbol.Epsilon, "13h 02m 10.59785", "+10° 57′ 32.9415", 2.826)
        val zeta = newStar(Symbol.Zeta, "13h 34m 41.591", "–00° 35′ 44.95", 3.376)
        val delta = newStar(Symbol.Delta, "12h 55m 36.20861", "+3° 23′ 50.8932", 3.32)
        val beta = newStar(Symbol.Beta, "11h 50m 41.71824", "+1° 45′ 52.9910", 3.604)
        val eta = newStar(Symbol.Eta, "12h 19m 54.35783", "–00° 40′ 00.5095", 3.890)
        val nu = newStar(Symbol.Nu, "11h 45m 51.55957", "+06° 31′ 45.7413", 4.04)
        val theta = newStar(Symbol.Theta, "13h 09m 56.99067", "−05° 32′ 20.4185", 4.37)
        newConstellation("Virgo", com.stho.nyota.R.drawable.constellation_virgo)
            .register(spica, theta, gamma, eta, beta, nu)
            .register(epsilon, delta, gamma)
            .register(zeta, theta)
            .translate(Language.Latin, "Virgo")
            .translate(Language.German, "Jungfrau")
            .translate(Language.English, "Virgin")
            .build()
    }

    // Waage (Libra)
    // https://en.wikipedia.org/wiki/Libra_(constellation)
    // https://wissen.naanoo.de/esoterik/sternzeichen-waage
    // (7) 24. September – 23. Oktober
    // The Scales (englisch)
    // Libra (latein)
    // Zygos (griechisch)
    // Tulā (sanskrit)
    private fun registerLibra() {
        val alpha = newStar(Symbol.Alpha, "14h 50m 41.18097", "–15° 59′ 50.0482", 2.741)
        val beta = newStar(Symbol.Beta, "15h 17m 00.41382", "−09° 22′ 58.4919", 2.61)
        val gamma = newStar(Symbol.Gamma, "15h 35m 31.57881", "−14° 47′ 22.3278", 3.91)
        val delta = newStar(Symbol.Delta, "15h 00m 58.35013", "−08° 31′ 08.2063", 4.93)
        val sigma = newStar(Symbol.Sigma, "15h 04m 04.21608", "–25° 16′ 55.0606", 3.20)
        val tau = newStar(Symbol.Tau, "15h 38m 39.36950", "−29° 46′ 39.8956", 3.68)
        val upsilon = newStar(Symbol.Upsilon, "15h 37m 01.45020", "−28° 08′ 06.2926", 3.628)
        newConstellation("Libra", com.stho.nyota.R.drawable.constellation_libra)
            .register(sigma, alpha, beta, gamma, tau, upsilon)
            .register(alpha, gamma)
            .register(delta)
            .translate(Language.Latin, "Libra")
            .translate(Language.German, "Waage")
            .translate(Language.English, "Balance")
            .build()
    }

    // Skorpion (Scorpius)
    // https://en.wikipedia.org/wiki/Scorpius
    // https://wissen.naanoo.de/esoterik/sternzeichen-skorpion
    // (8) 24. Oktober – 22. November
    // The Scorpion (englisch)
    // Scorpio (latein)
    // Skorpios (griechisch)
    // Vṛścika (sanskrit)
    private fun registerScorpius() {
        val nu = newStar(Symbol.Nu, "16h 11m 59.740", "−19° 27′ 38.33", 4.349)
        val beta = newStar(Symbol.Beta, "16h 05m 26.23198", "–19° 48′ 19.6300", 2.62)
        val delta = newStar(Symbol.Delta, "16h 00m 20.00528", "–22° 37′ 18.1431", 2.307)
        val pi = newStar(Symbol.Pi, "15h 58m 51.11324", "−26° 06′ 50.7886", 2.89)
        val rho = newStar(Symbol.RHO, "15h 56m 53.07624", "−29° 12′ 50.6612", 3.86)
        val sigma = newStar( Symbol.Sigma, "16h 21m 11.31571", "–25° 35′ 34.0515", 2.88)
        val antares = newStar(Symbol.Alpha, "16h 29m 24.45970", "−26° 25′ 55.2094", 0.6)
        val tau = newStar(Symbol.Tau, "16h 35m 52.95285", "−28° 12′ 57.6615", 2.82)
        val epsilon = newStar(Symbol.Epsilon, "16h 50m 09.8", "–34° 17′ 36", 2.310)
        val mu = newStar(Symbol.Mu, "16h 51m 52.23111", "−38° 02′ 50.5694", 3.04)
        val zeta = newStar(Symbol.Zeta, "16h 53m 59.72650", "−42° 21′ 43.3063", 3.59)
        val eta = newStar(Symbol.Eta, "17h 12m 09.19565", "–43° 14′ 21.0905", 3.33)
        val iota = newStar(Symbol.Iota, "17h 47m 35.08113", "−40° 07′ 37.1893", 3.03)
        val kappa = newStar(Symbol.Kappa, "17h 42m 29.27520", "–39° 01′ 47.9391", 2.39)
        val theta = newStar(Symbol.Theta, "17h 37m 19.12985", "–42° 59′ 52.1808", 1.84)
        val lambda = newStar(Symbol.Lambda, "17h 33m 36.520", "−37° 06′ 13.76", 1.62)
        val upsilon = newStar(Symbol.Upsilon, "17h 30m 45.83712", "–37° 17′ 44.9285", 2.70)
        newConstellation("Scorpius", com.stho.nyota.R.drawable.constellation_scorpius)
            .register(sigma, nu)
            .register(sigma, beta)
            .register(sigma, delta)
            .register(sigma, pi)
            .register(sigma, rho)
            .register(sigma, antares, tau, epsilon, mu, zeta, eta, theta, iota, kappa, lambda)
            .register(kappa, upsilon)
            .translate(Language.Latin, "Scorpius")
            .translate(Language.German, "Skorpion")
            .translate(Language.English, "Scorpion")
            .build()
    }

    // Schütze / Archer / "Teapot" (Sagittarius)
    // 23. November – 21. Dezember
    // https://en.wikipedia.org/wiki/Sagittarius_(constellation)
    // https://wissen.naanoo.de/esoterik/sternzeichen-schuetze
    // (9) 23. November – 21. Dezember
    // The Archer (englisch)
    // Sagittarius (latein)
    // Toxotēs (griechisch)
    // Dhanuṣha (sanskrit)
    private fun registerSagittarius() {
        val delta = newStar(Symbol.Delta, "18h 20m 59.64354", "−29° 49′ 41.1659", 2.70)
        val epsilon = newStar(Symbol.Epsilon, "18h 24m 10.31840", "–34° 23′ 04.6193", 1.85)
        val zeta = newStar(Symbol.Zeta, "19h 02m 36.73024", "–29° 52′ 48.2279", 2.59)
        val phi = newStar(Symbol.Phi, "18h 45m 39.38610", "–26° 59′ 26.7944", 3.17)
        val lambda = newStar(Symbol.Lambda, "18h 27m 58.24072", "−25° 25′ 18.1146", 2.82)
        val gamma = newStar(Symbol.Gamma, "18h 05m 48.48810", "–30° 25′ 26.7235", 2.98)
        val sigma = newStar(Symbol.Sigma, "18h 55m 15.92650", "–26° 17′ 48.2068", 2.05)
        val tau = newStar(Symbol.Tau, "19h 06m 56.40897", "–27° 40′ 13.5189", 3.326)
        val eta = newStar( Symbol.Eta, "18h 17m 37.63505", "−36° 45′ 42.0667", 3.11)
        val pi = newStar(Symbol.Pi, "19h 09m 45.83293", "–21° 01′ 25.0103", 2.89)
        val xi = newStar(Symbol.Xi, "18h 57m 20.47670", "−20° 39′ 22.8539", 5.06)
        val mu = newStar(Symbol.Mu, "18h 13m 45.8", "−21° 03′ 32", 3.85)
        newConstellation("Sagittarius", com.stho.nyota.R.drawable.constellation_sagittarius)
            .register(eta, epsilon, delta, lambda, mu)
            .register(lambda, phi, sigma)
            .register(xi, pi, sigma, tau, zeta)
            .translate(Language.Latin, "Sagittarius")
            .translate(Language.German, "Schütze")
            .translate(Language.English, "Archer")
            .build()
// Although it's quite funny, I favour the clear appearance of the sagittarius
//        newConstellation("Teapot", com.stho.nyota.R.drawable.constellation_teapot)
//            .register(epsilon, delta, gamma, epsilon, zeta, phi, delta, lambda, phi, sigma, tau, zeta)
//            .translate(Language.English, "Teapot")
//            .build()
    }

    // Steinbock, The Sea-Goat (englisch)
    //Capricorn (latein)
    //Aigokerōs (griechisch)
    //Makara (sanskrit)
    // https://en.wikipedia.org/wiki/Capricornus
    // https://wissen.naanoo.de/esoterik/sternzeichen-steinbock
    // (10) 22. Dezember – 20. Januar
    // The Sea-Goat (englisch)
    // Capricorn (latein)
    // Aigokerōs (griechisch)
    // Makara (sanskrit)
    private fun registerCapricornus() {
        val alpha = newStar(Symbol.Alpha, "20h 18m 03.25595", "−12° 32′ 41.4684", 3.57)
        val beta = newStar( Symbol.Beta, "20h 21m 00.7", "−14° 46′ 53", 3.05)
        val psi = newStar(Symbol.Psi, "20h 46m 05.73263", "−25° 16′ 15.2312", 4.13)
        val omicron = newStar(Symbol.Omichron, "20h 29m 53.91117", "−18° 34′ 59.4803", 5.94)
        val omega = newStar(Symbol.Omega, "20h 51m 49.29084", "−26° 55′ 08.8574", 4.11)
        val zeta = newStar(Symbol.Zeta, "21h 26m 40.02634", "−22° 24′ 40.8042", 3.74)
        val epsilon = newStar( Symbol.Epsilon, "21h 37m 04.83068", "−19° 27′ 57.6464", 4.62)
        val delta = newStar(Symbol.Delta, "21h 47m 02.44424", "−16° 07′ 38.2335", 2.81)
        val gamma = newStar(Symbol.Gamma, "21h 40m 05.4563", "−16° 39′ 44.308", 3.67)
        val theta = newStar(Symbol.Theta, "21h 05m 56.82783", "−17° 13′ 58.3021", 4.07)
        newConstellation("Capricornus", com.stho.nyota.R.drawable.constellation_capricornus)
            .register(alpha, beta, psi, omega, zeta, epsilon, delta, gamma, theta, alpha)
            .register(omicron)
            .translate(Language.Latin, "Capricornus")
            .translate(Language.German, "Steinbock")
            .translate(Language.English, "Sea goat")
            .build()
    }


    //Translations:
    // https://starchild.gsfc.nasa.gov/docs/StarChild/questions/88constellations.html

    // Wassermann
    // https://en.wikipedia.org/wiki/Aquarius_(constellation)
    // https://wissen.naanoo.de/esoterik/sternzeichen-wassermann
    // (11) 21. Januar – 19. Februar
    // The Water-Bearer (englisch)
    // Aquarius (latein)
    // Hydrokhoos (griechisch)
    // Kumbha (sanskrit)
    private fun registerAquarius() {
        val epsilon = newStar(Symbol.Epsilon, "20h 47m 40.55260", "−09° 29′ 44.7877", 3.77)
        val mu = newStar(Symbol.Mu, "20h 52m 39.23277", "−08° 58′ 59.9499", 4.731)
        val beta = newStar(Symbol.Beta, "21h 31m 33.53171", "–05° 34′ 16.2320", 2.87)
        val alpha = newStar(Symbol.Alpha, "22h 05m 47.03593", "−00° 19′ 11.4568", 2.942)
        val pi = newStar(Symbol.Pi, "22h 25m 16.62285", "+01° 22′ 38.6346", 4.42)
        val zeta = newStar(Symbol.Zeta, "22h 28m 49.90685", "–00° 01′ 11.7942", 3.65)
        val eta = newStar(Symbol.Eta, "22h 35m 21.38126", "–00° 07′ 02.9888", 4.04)
        val gamma = newStar(Symbol.Gamma, "22h 21m 39.37542", "–01° 23′ 14.4031", 3.849)
        val iota = newStar(Symbol.Iota, "22h 06m 26.22742", "–13° 52′ 10.8615", 4.279)
        val theta = newStar(Symbol.Theta, "22h 16m 50.03635", "–07° 46′ 59.8480", 4.175)
        val lambda = newStar(Symbol.Lambda, "22h 52m 36.87441", "−07° 34′ 46.5542", 3.722)
        val tau = newStar(Symbol.Tau, "22h 49m 35.50157", "–13° 35′ 33.4767", 4.042)
        val delta = newStar(Symbol.Delta, "22h 54m 39.0125", "−15° 49′ 14.953", 3.252)
        val psi = newStar(Symbol.Psi, "23h 17m 54.21372", "–09° 10′ 57.0675", 4.403)
        val chi = newStar(Symbol.Chi, "23h 16m 50.93916", "−07° 43′ 35.4023", 5.06)
        val phi = newStar( Symbol.Phi, "23h 14m 19.35787", "–06° 02′ 56.3998", 4.223)
        newConstellation("Aquarius", com.stho.nyota.R.drawable.constellation_aquarius)
            .register(epsilon, mu, beta, alpha, pi, zeta, eta)
            .register(zeta, gamma, alpha)
            .register(beta, iota)
            .register(alpha, theta, lambda, tau, delta, psi, chi, phi, lambda)
            .translate(Language.Latin, "Aquarius")
            .translate(Language.German, "Wassermann")
            .translate(Language.English, "Water bearer")
            .build()
    }

    // Fische (Pisces)
    // https://en.wikipedia.org/wiki/Pisces_(constellation)
    // https://wissen.naanoo.de/esoterik/sternzeichen
    // (12) 20. Februar – 20. März
    // The Fish (englisch)
    // Pisces (latein)
    // Ikhthyes (griec)hisch)
    // Mīna (sanskrit)
    private fun registerPisces() {
        val alpha = newStar(Symbol.Alpha, "02h 02m 02.81972", "+02° 45′ 49.5410", 3.82)
        val beta = newStar(Symbol.Beta, "23h 03m 52.61349", "+03° 49′ 12.1662", +4.40)
        val gamma = newStar(Symbol.Gamma, "23h 17m 09.93749", "+03° 16′ 56.2380", 3.699)
        val delta = newStar(Symbol.Delta, "00h 48m 40.94433", "+07° 35′ 06.2926", 4.416)
        val epsilon = newStar(Symbol.Epsilon, "01h 02m 56.60862", "+07° 53′ 24.4855", 4.27)
        val zeta = newStar(Symbol.Zeta, "01h 13m 45.17477", "+07° 34′ 31.2745", 5.28)
        val eta = newStar(Symbol.Eta, "01h 31m 29.01026", "+15° 20′ 44.9685", 3.611)
        val theta = newStar(Symbol.Theta, "23h 27m 58.09529", "+6° 22′ 44.3720", 4.27)
        val iota = newStar(Symbol.Iota, "23h 39m 57.04138", "+05° 37′ 34.6475", 4.13)
        val kappa = newStar(Symbol.Kappa, "23h 26m 55.95586", "+01° 15′ 20.1900", 4.94)
        val lambda = newStar(Symbol.Lambda, "23h 42m 02.80612", "+01° 46′ 48.1484", 4.49)
        val mu = newStar(Symbol.Mu, "01h 30m 11.11444", "+06° 08′ 37.7577", 4.84)
        val nu = newStar( Symbol.Nu, "01h 41m 25.89391", "+05° 29′ 15.4062", 4.44)
        val xi = newStar(Symbol.Xi, "01h 45m 23.63185", "+09° 09′ 27.8530", 4.60)
        val omicron = newStar(Symbol.Omichron, "01h 45m 23.63185", "+09° 09′ 27.8530", 4.27)
        val pi = newStar(Symbol.Pi, "01h 37m 05.91523", "+12° 08′ 29.5186", 5.60)
        val rho = newStar(Symbol.RHO, "01h 26m 15.26209", "+19° 10′ 20.4526", 5.344)
        val sigma = newStar(Symbol.Sigma, "01h 02m 49.09645", "+31° 48′ 15.3471", 5.50)
        val tau = newStar(Symbol.Tau, "01h 11m 39.63647", "+30° 05′ 22.6909", 4.518)
        val upsilon = newStar(Symbol.Upsilon, "01h 19m 27.99289", "+27° 15′ 50.6155", 4.752)
        val phi = newStar(Symbol.Phi, "01h 13m 44.9471", "+24° 35′ 01.367", 4.676)
        val omega = newStar(Symbol.Omega, "23h 59m 18.69064", "+06° 51′ 47.9562", 4.01)
        val psi = newStar(Symbol.Psi, "01h 05m 40.95527", "+21° 28′ 23.4489", 5.273)
        val chi = newStar(Symbol.Chi, "01h 11m 27.21877", "+21° 02′ 04.7406", 4.64)
        newConstellation("Pisces", com.stho.nyota.R.drawable.constellation_pisces)
            .register(iota, lambda, kappa, gamma, theta, iota, omega, delta, epsilon, zeta, mu, nu, alpha, xi, omicron, pi, eta, rho, phi, tau, upsilon, phi)
            .register(beta)
            .register(sigma)
            .register(chi)
            .register(psi)
            .translate(Language.Latin, "Pisces")
            .translate(Language.German, "Fische")
            .translate(Language.English, "Fishes")
            .build()
    }

    // https://en.wikipedia.org/wiki/Auriga_(constellation)
    // (Fuhrmann)
    private fun registerAuriga() {
        val alpha = getStar("Capella")
        val beta = newStar(Symbol.Beta, "05h 59m 31.77", "+44° 56′ 50.8", 1.90)
        val iota = newStar(Symbol.Iota, "04h 56m 59.62", "+33° 09′ 58.1", 2.69)
        val epsilon = newStar(Symbol.Epsilon, "05h 01m 58.13", "+43° 49′ 23.9", 3.03)
        val theta = newStar(Symbol.Theta, "05h 59m 43.24", "+37° 12′ 46.0", 2.62)
        val eta = newStar(Symbol.Eta, "05h 06m 30.87", "+41° 14′ 04.7", 3.18)
        val delta = newStar( Symbol.Delta, "05h 59m 31.63", "+54° 17′ 04.77", 3.71)
        newConstellation("Auriga", com.stho.nyota.R.drawable.constellation_auriga)
            .register(iota, epsilon, alpha, beta, theta, iota)
            .register(eta)
            .register(delta)
            .translate(Language.Latin, "Auriga")
            .translate(Language.German, "Fuhrmann")
            .translate(Language.English, "Charioteer")
            .build()
    }

    private fun registerPerseus() {
        val alpha = newStar("Mirfak", Symbol.Alpha, "03h 24m 19.35", "+49° 51′ 40.5", 1.79)
        val beta = newStar("Algol", Symbol.Beta, "03h 08m 10.13", "+40° 57′ 20.3", 2.09)
        val zeta = newStar("Atik", Symbol.Zeta, "03h 54m 07.92", "+31° 53′ 01.2", 2.84)
        val epsilon = newStar(Symbol.Epsilon, "03h 57m 51.22", "+40° 00′ 37.0", 2.90)
        val xi = newStar("Menkib", Symbol.Xi, "03h 58m 57.90", "+35° 47′ 27.7", 3.98)
        val omicron = newStar(Symbol.Omichron, "03h 44m 19.13", "+32° 17′ 17.8", 3.84)
        val rho = newStar(Symbol.RHO, "03h 05m 10.50", "+38° 50′ 25.9", 3.32)
        val kappa = newStar(Symbol.Kappa, "03h 09m 29.63", "+44° 51′ 28.4", 3.79)
        val iota = newStar(Symbol.Iota, "03h 09m 02.88", "+49° 36′ 48.6", 4.05)
        val theta = newStar(Symbol.Theta, "02h 44m 11.69", "+49° 13′ 43.2", 4.10)
        val tau = newStar(Symbol.Tau, "02h 54m 15.46", "+52° 45′ 45.0", 3.93)
        val eta = newStar(Symbol.Eta, "02h 50m 41.79", "+55° 53′ 43.9", 3.77)
        val gamma = newStar(Symbol.Gamma, "03h 04m 47.79", "+53° 30′ 23.2", 2.91)
        val psi = newStar(Symbol.Psi, "03h 36m 29.36", "+48° 11′ 33.7", 4.32)
        val delta = newStar(Symbol.Delta, "03h 42m 55.48", "+47° 47′ 15.6", 3.01)
        val nu = newStar(Symbol.Nu, "03h 45m 11.64", "+42° 34′ 42.8", 3.77)
        val phi = newStar(Symbol.Phi, "01h 43m 39.62", "+50° 41′ 19.6", 4.01)
        val mu = newStar(Symbol.Mu, "04h 14m 53.86", "+48° 24′ 33.7", 4.12)
        val lambda = newStar(Symbol.Lambda, "04h 06m 35.06", "+50° 21′ 04.9", 4.25)
        val omega = newStar(Symbol.Omega, "03h 11m 17.40", "+39° 36′ 41.7", 4.61)
        val pi = newStar(Symbol.Pi, "02h 58m 45.65", "+39° 39′ 46.2", 4.68)
        newConstellation("Perseus", com.stho.nyota.R.drawable.constellation_perseus)
            .register(rho, beta, kappa, iota, tau, eta, gamma, alpha, delta, epsilon, xi, zeta, omicron)
            .register(lambda, mu, delta)
            .register(alpha, iota, theta)
            .register(epsilon, beta)
            .register(omega)
            .register(pi)
            .register(psi)
            .register(phi)
            .register(nu)
            .translate(Language.Latin, "Perseus")
            .translate(Language.German, "Perseus")
            .translate(Language.English, "Perseus")
            .build()
    }

    // Musca = Fly
    // https://en.wikipedia.org/wiki/Musca#/media/File:Musca_IAU.svg
    private fun registerMusca() {
        // Start alpha =
        // newConstellation("Musca", R.drawable.musca)
        //        .register(...);
        // TODO
    }

    // https://de.wikipedia.org/wiki/Cham%C3%A4leon_(Sternbild)
    private fun registerCamaeleon() {
        // Start alpha =
        // newConstellation("Chamaeleon", R.drawable.chamaeleon)
        //        .register(...);
        // TODO
    }

    // https://de.wikipedia.org/wiki/Zentaur_(Sternbild)
    private fun registerCentaurus() {
        // TODO
    }

    // https://de.wikipedia.org/wiki/Pfau_(Sternbild)
    // https://en.wikipedia.org/wiki/List_of_stars_in_Pavo
    private fun registerPavo() {
        val alpha = newStar("Peacock", Symbol. Alpha, "20h 25m 38.85", "−56° 44′ 05.6", 1.94)
        val beta = newStar(Symbol.Beta, "20h 44m 57.56", "−66° 12′ 11.7", 3.42)
        val delta = newStar(Symbol.Delta, "20h 08m 41.86", "−66° 10′ 45.6", 3.55)
        val lambda = newStar( Symbol.Lambda, "18h 52m 13.04", "−62° 11′ 15.2", 4.22)
        val eta = newStar(Symbol.Eta, "17h 45m 44.00", "−64° 43′ 25.4", 3.61)
        val zeta = newStar(Symbol.Zeta, "18h 43m 02.13", "−71° 25′ 39.8", 4.01)
        val epsilon = newStar(Symbol.Epsilon, "20h 00m 35.39", "−72° 54′ 36.7", 3.97)
        newConstellation("Pavo", com.stho.nyota.R.drawable.constellation_pavo)
            .register(alpha, beta, delta, lambda, eta, zeta, epsilon, beta)
            .translate(Language.Latin, "Pavo")
            .translate(Language.German, "Pfau")
            .translate(Language.English, "Peacock")
            .build()
    }

    // https://de.wikipedia.org/wiki/Eridanus_(Sternbild)
    // https://de.wikipedia.org/wiki/Achernar
    private fun registerEridanus() {
        val achernar = getStar("Achernar")
        // TODO not ready
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

    private fun registerSpecialElements() {
        newSpecialElement("S", Hour.fromDegree(0.0), Degree.fromNegative(90, 0, 0.0))
        newSpecialElement("N", Hour.fromDegree(0.0), Degree.fromPositive(90, 0, 0.0))
    }
}