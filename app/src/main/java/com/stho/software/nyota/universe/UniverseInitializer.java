package com.stho.software.nyota.universe;

import com.stho.software.nyota.R;
import com.stho.software.nyota.utilities.Degree;
import com.stho.software.nyota.utilities.Hour;

/**
 * Created by shoedtke on 31.08.2016.
 */
class UniverseInitializer extends AbstractUniverseInitializer {

    private final String ALPHA = "α";
    private final String BETA = "β";
    private final String GAMMA = "γ";
    private final String DELTA = "δ";
    private final String SIGMA = "σ";
    private final String TAU = "τ";
    private final String UPSILON = "υ";
    private final String EPSILON = "ε";
    private final String ETA = "η";
    private final String NU = "ν";
    private final String ZETA = "ζ";
    private final String THETA = "θ";
    private final String PHI = "φ";
    private final String XI = "ξ";
    private final String OMICRON = "ο";
    private final String OMEGA = "ω";
    private final String PSI = "ψ";
    private final String CHI = "χ";
    private final String LAMBDA = "λ";
    private final String PI = "π";
    private final String RHO = "ρ";
    private final String MU = "μ";
    private final String KAPPA = "κ";
    private final String IOTA = "ι";

    UniverseInitializer(Universe universe) {
        super(universe);
    }

    // https://en.wikipedia.org/wiki/88_modern_constellations
    // https://wissen.naanoo.de/esoterik/sternzeichen
    // https://www.sternfreunde-muenster.de/
    void initialize() {
        registerPolarStar();
        registerCrux();
        registerTriangulumAustrale();
        registerOrion();
        registerUrsMajor();
        registerUrsMinor();
        registerCassiopeiae();
        registerPleiades();
        registerPegasus();
        registerAndromeda();
        registerAquila();
        registerCygnus();
        registerLacerta();
        registerAra();
        registerHydra();

        registerAries();
        registerTaurus();
        registerGemini();
        registerCancer();
        registerLeo();
        registerVirgo();
        registerLibra();
        registerScorpius();
        registerSagittarius();
        registerCapricornus();
        registerAquarius();
        registerPisces();

        registerISS();
        registerSpecialElements();
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

    private void registerPolarStar() {
        asVIP(newStar("Polaris", "P", "2h 31m 49.09", "+89° 15′ 50.8", 1.98));
        asVIP(newStar("Sirius", "1.", "6h 45m 8.82", "-16° 42' 56.9", -1.46));
        asVIP(newStar("Aldebaran", "A", "4h 35m 55.24", "+16° 30' 33.5", 0.87));
        asVIP(newStar("Canopus", "2.", "06h 23m 57.10988", "−52° 41′ 44.3810", -0.74));
        asVIP(newStar("Alpha Centauri", "3.", "14h 39m 36.49400", "–60° 50′ 02.3737", -0.27)); // A+B
        asVIP(newStar("Arcturus", "4.", "14h 15m 39.7", "+19° 10′ 56.0", -0.05));
        asVIP(newStar("Vega", "5.", "18h 36m 56.33635", "+38° 47′ 01.2802", +0.026));
    }

    private void registerCrux() {
        Star alpha = newStar("Acrux", ALPHA, "12h 26m 35.99", "-63° 5' 56.7", 1.3);
        Star beta = newStar("Becrux", BETA, "12h 47m 43.3", "-59° 41' 20", 1.25);
        Star gamma = newStar("Gacrux", GAMMA, "12h 31m 10.0", "-57° 6' 48", 1.59);
        Star delta = newStar("Decrux", DELTA, "12h 15m 8.7", "-58° 44' 56", 2.79);
        Star epsilon = newStar("Epsilon Crucis", EPSILON, "12h 21m 21.61", "-60° 24' 4.1", 3.59);
        newConstellation("Crux", R.drawable.cross)
                .register(alpha, gamma)
                .register(beta, delta)
                .register(epsilon);
    }

    // https://en.wikipedia.org/wiki/Triangulum_Australe
    private void registerTriangulumAustrale() {
        Star alpha = newStar("Alpha Trianguli Australis", "ALPHA", "16h 48m 39.89508", "–69° 01′ 39.7626", 1.91);
        Star beta = newStar("Beta Trianguli Australis", BETA, "15h 55m 08.56206", "−63° 25′ 50.6155", 2.85);
        Star gamma = newStar("Gamma Trianguli Australis", GAMMA, "15h 18m 54.58198", "–68° 40′ 46.3654", 2.87);
        Star epsilon = newStar("Epsilon Trianguli Australis", EPSILON, "15h 36m 43.22223", "−66° 19′ 01.3334", 4.11);
        newConstellation("Triangulum Australe", R.drawable.triangulum_australe)
                .register(alpha, gamma, epsilon, beta, alpha);
    }

    // https://en.wikipedia.org/wiki/Saiph
    // http://www.sternenhimmel-aktuell.de/Orion.htm
    private void registerOrion() {
        Star alpha = newStar("Betelgeuse", ALPHA, "05h 55m 10.30536", "+07° 24′ 25.4304", 0.50);
        Star gamma = newStar("Bellatrix", GAMMA, "05h 25m 07.86325", "+06° 20′ 58.9318", 1.64);
        Star beta = newStar("Rigel", BETA, "05h 14m 32.27210", "−08° 12′ 05.8981", 0.13);
        Star kappa = newStar("Saiph", KAPPA, "05h 47m 45.38884", "−09° 40′ 10.5777", 2.09);
        Star zeta = newStar("Alnitak", ZETA, "05h 40m 45.52666", "−01° 56′ 34.2649", 1.77);
        Star epsilon = newStar("Alnilam", EPSILON, "05h 36m 12.8", "−01° 12′ 06.9", 1.69);
        Star delta = newStar("Mintaka", DELTA, "05h 32m 00.40009", "−00° 17′ 56.7424", 2.23);
        Star lambda = newStar("Meissa", LAMBDA, "05h 35m 08.27761", "+09° 56′ 02.9611", 3.33);
        newConstellation("Orion", R.drawable.orion)
                .register(kappa, zeta, alpha, gamma, delta, beta)
                .register(zeta, epsilon, delta)
                .register(lambda);
    }

    // https://en.wikipedia.org/wiki/Ursa_Major
    private void registerUrsMajor() {
        Star eta = newStar("Alkaid", ETA,"13h 47m 32.43776","+49° 18′ 47.7602",1.86);
        Star zeta = newStar("Mizar", ZETA,"13h 23m 55.54048", "+54° 55′ 31.2671",2.04);
        Star epsilon = newStar("Alioth", EPSILON, "12h 54m 01.74959", "+55° 57′ 35.3627", 1.77);
        Star delta = newStar("Megrez", DELTA, "12h 15m 25.56063", "+57° 01′ 57.4156", 3.31);
        Star gamma = newStar("Phecda", GAMMA, "11h 53m 49.84732", "+53° 41′ 41.1350", 2.44);
        Star beta = newStar("Merak", BETA, "11h 01m 50.47654", "+56° 22′ 56.7339", 2.37);
        Star alpha = newStar("Dubhe", ALPHA, "11h 03m 43.67152", "+61° 45′ 03.7249", 1.79);
        newConstellation("Urs Major", R.drawable.urs_major)
            .register(eta, zeta, epsilon, delta, gamma, beta, alpha, delta);
    }

    // https://en.wikipedia.org/wiki/Ursa_Minor
    private void registerUrsMinor() {
        Star polaris = newStar("Polaris", ALPHA, "02h 31m 49.09", "+89° 15′ 50.8", 1.98);
        Star delta = newStar("Yildun", DELTA, "17h 32m 12.99671", "+86° 35′ 11.2584", 4.36);
        Star epsilon = newStar("Epsilon Ursa Minoris", EPSILON, "16h 45m 58.24168", "+82° 02′ 14.1233", 4.19);
        Star zeta = newStar("Ahfa al Farkadain", ZETA, "15h 44m 03.5193", "+77° 47′ 40.175", 4.32);
        Star beta = newStar("Kochab", BETA, "14h 50m 42.32580", "+74° 09′ 19.8142", 2.08);
        Star gamma = newStar("Pherkad", GAMMA, "15h 20m 43.71604", "+71° 50′ 02.4596", 3.05);
        Star eta = newStar("Anwar Al Farkadain", ETA, "16h 17m 30.28696", "+75° 45′ 19.1885", 4.95);
        newConstellation("Urs Minor", R.drawable.urs_minor)
                .register(polaris, delta, epsilon, zeta, beta, gamma, eta, zeta);
    }

    private void registerCassiopeiae() {

        // Cassiopeiae
        Star beta = newStar("Caph", BETA, "00h 09m 10.68518", "+59° 08′ 59.2120", 2.28);
        Star alpha = newStar("Schedar", ALPHA, "00h 40m 30.4405", "+56° 32′ 14.392", 2.24);
        Star gamma = newStar("Gamma Cassiopeiae", GAMMA, "00h 56m 42.50108", "+60° 43′ 00.2984", 2.47);
        Star delta = newStar("Ruchbah", DELTA, "01h 25m 48.95147", "+60° 14′ 07.0225", 2.68);
        Star epsilon = newStar("Segin", EPSILON, "01h 54m 23.72567", "+63° 40′ 12.3628", 3.37);
        newConstellation("Cassiopeia", R.drawable.cassiopeia)
                .register(beta, alpha, gamma, delta, epsilon);
    }

    // seven sisters,
    // https://en.wikipedia.org/wiki/Pleiades
    private void registerPleiades() {
        Star alcyone = newStar("Alcyone", "1", "03h 47m 29.077", "+24° 06′ 18.49", 2.87);
        Star electra = newStar("Electra", "2", "03h 44m 52.53688", "+24° 06′ 48.0112", 3.70);
        Star atlas = newStar("Atlas", "3", "03h 49m 09.74258", "+24° 03′ 12.3003", 3.63);
        Star maia = newStar("Maia", "4", "03h 45m 49.6067", "+24° 22′ 03.895", 3.871);
        Star merope = newStar("Merope", "5", "03h 46m 19.57384", "+23° 56′ 54.0812", 4.18);
        Star taygeta = newStar("Taygeta", "6", "03h 45m 12.49578", "+24° 28′ 02.2097", 4.30);
        Star celaeno = newStar("Celaeno", "7", "03h 44m 48.2154", "+24° 17′ 22.093", 5.448);
        Star pleione = newStar("Pleione", "8", "03h 49m 11.2161", "+24° 08′ 12.163", 5.048);
        Star sterope = newStar("Sterope", "9", "03h 46m 02.90030", "+24° 31′ 40.4313", 6.43);
        newConstellation("Pleiades", R.drawable.pleiades)
                .register(alcyone)
                .register(electra)
                .register(atlas)
                .register(maia)
                .register(merope)
                .register(taygeta)
                .register(celaeno)
                .register(pleione)
                .register(sterope);
    }

    // https://en.wikipedia.org/wiki/Pegasus_(constellation)
    private void registerPegasus() {
        Star alpha = newStar("Alpha Pegasi", ALPHA, "23h 04m 45.65345", "+15° 12′ 18.9617", 2.48);
        Star beta = newStar("Beta Pegasi", BETA, "23h 03m 46.45746", "+28° 04′ 58.0336", 2.42);
        Star gamma = newStar("Gamma Pegasi", GAMMA, "00h 13m 14.15123", "+15° 11′ 00.9368", 2.84);
        Star andromedae = newStar("Alpha Andromedae", DELTA, "00h 08m 23.25988", "+29° 05′ 25.5520", 2.06);
        Star epsilon = newStar("Epsilon Pegasi", EPSILON, "21h 44m 11.15614", "+09° 52′ 30.0311", 2.399);
        Star zeta = newStar("Zeta Pegasi", ZETA, "22h 41m 27.72072", "+10° 49′ 52.9079", 3.414);
        Star eta = newStar("Eta Pegasi", ETA, "22h 43m 00.13743", "+30° 13′ 16.4822", 2.95);
        Star pi = newStar("Pi Pegasi", PI, "22h 09m 13.633", "+33° 10′ 20.41", 5.595);
        Star iota = newStar("Iota Pegasi", IOTA, "22h 07m 00.666", "+25° 20′ 42.40", 3.84);
        Star kappa = newStar("Kappa Pegasi", KAPPA, "21h 44m 38.7344", "+25° 38′ 42.128", 4.159);
        Star theta = newStar("Theta Pegasi", THETA, "22h 10m 11.98528", "+06° 11′ 52.3078", 3.53);
        Star mu = newStar("Mu Pegasi", MU, "22h 50m 00.19315", "+24° 36′ 05.6984", 3.514);
        Star tau = newStar("Tau Pegasi", TAU, "23h 20m 38.24188", "+23° 44′ 25.2098", 4.58);
        Star lambda = newStar("Lambda Pegasi", LAMBDA, "22h 46m 31.87786", "+23° 33′ 56.3561", 3.65);
        Star upsilon = newStar("Upsilon Pegasi", UPSILON, "23h 25m 22.78350", "+23° 24′ 14.7606", 4.40);
        newConstellation("Pegasus", R.drawable.pegasus)
                .register(epsilon, theta, zeta, alpha, gamma, andromedae, beta, eta, pi)
                .register(beta, mu, lambda, iota, kappa)
                .register(alpha,beta)
                .register(tau)
                .register(upsilon);
    }

    // https://en.wikipedia.org/wiki/Andromeda_(constellation)
    private void registerAndromeda() {
        Star andromedae = newStar("Alpha Andromedae", DELTA, "00h 08m 23.25988", "+29° 05′ 25.5520", 2.06);
        Star delta = newStar("Delta Andromedae", DELTA, "00h 39m 19.67518","+30° 51′ 39.6783", 3.28);
        Star beta = newStar("Beta Andromedae", BETA, "01h 09m 43.92388","+35° 37′ 14.0075", 2.05);
        Star gamma = newStar("Gamma Andromedae", GAMMA, "02h 03m 53.9531", "+42° 19′ 47.009", 2.10);
        Star eta = newStar("Eta Andromedae", ETA, "00h 57m 12.4000","+23° 25′ 03.533", 4.403);
        Star zeta = newStar("Zeta Andromedae", ZETA, "00h 47m 20.32547", "+24° 16′ 01.8408", 3.92);
        Star epsilon = newStar("Epsilon Andromedae", EPSILON, "00h 38m 33.34610", "+29° 18′ 42.3135", 4.357);
        Star pi = newStar("Pi Andromedae", PI, "00h 36m 52.84926", "+33° 43′ 09.6384", 4.36);
        Star iota = newStar("Iota Andromedae", IOTA, "23h 38m 08.20130", "+43° 16′ 05.0649", 4.29);
        Star kappa = newStar("Kappa Andromedae", KAPPA, "23h 40m 24.50763", "+44° 20′ 02.1566", 4.139);
        Star lambda = newStar("Lambda Andromedae", LAMBDA, "23h 37m 33.84261", "+46° 27′ 29.3380", 3.65);
        newConstellation("Andromeda", R.drawable.andromeda)
                .register(andromedae, delta, beta, gamma)
                .register(eta, zeta, epsilon, delta, pi, iota, kappa, lambda);
    }

    // https://en.wikipedia.org/wiki/List_of_stars_in_Aquila
    private void registerAquila() {
        Star altair = newStar("Altair", ALPHA, "19h 50m 46.99855", "+08° 52′ 05.9563", 0.76);
        Star beta = newStar("Beta Aquilae", BETA, "19h 55m 18.79256", "+06° 24′ 24.3425", 3.87);
        Star gamma = newStar("Gamma Aquilae", GAMMA, "19h 46m 15.58029", "+10° 36′ 47.7408", 2.712);
        Star zeta = newStar("Zeta Aquilae", ZETA, "19h 05m 24.60802", "+13° 51′ 48.5182", 2.983);
        Star theta = newStar("Theta Aquilae", THETA, "20h 11m 18.28528", "–00° 49′ 17.2626", 3.26);
        Star eta = newStar("Eta Aquilae", ETA, "19h 52m 28.36775", "+01° 00′ 20.3696", 3.87);
        Star delta = newStar("Delta Aquilae", DELTA, "19h 25m 29.90139", "+03° 06′ 53.2061", 3.365);
        Star lambda = newStar("Lambda Aquilae", LAMBDA, "19h 06m 14.93898", "−04° 52′ 57.2007", 3.43);
        newConstellation("Aquila", R.drawable.aquila)
                .register(altair, zeta, delta, altair, theta, eta, delta, lambda);

    }

    // Schwan (Cygnus)
    // https://en.wikipedia.org/wiki/Cygnus_(constellation)
    private void registerCygnus() {
        Star alpha = newStar("Deneb", ALPHA, "20h 41m 25.91", "+45° 16′ 49.2", 1.25);
        Star gamma = newStar("Gamma Cygni", GAMMA, "20h 22m 13.70184", "+40° 15′ 24", 2.23);
        Star eta = newStar("Eta Cygni", ETA, "19h 56m 18.37222", "+35° 05′ 00.3228", 3.89);
        Star beta = newStar("Albireo", BETA, "19h 30m 43.286", "+27° 57′ 34.84", 3.18);
        Star zeta = newStar("Zeta Cygni", ZETA, "21h 12m 56.18594", "+30° 13′ 36.8957", 3.26);
        Star epsilon = newStar("Epsilon Cygni", EPSILON, "20h 46m 12.68236", "+33° 58′ 12.9250", 2.48);
        Star delta = newStar("Delta Cygni", DELTA, "19h 44m 58.47854", "+45° 07′ 50.9161", 2.87);
        Star iota = newStar("Iota Cygni", IOTA, "19h 29m 42.36", "+51° 43′ 47.2", 3.77);
        Star kappa = newStar("Kappa Cygni", KAPPA, "19h 17m 06.16865", "+53° 22′ 06.4534", 3.814);
        newConstellation("Cygnus", R.drawable.cygnus)
                .register(alpha, gamma, eta, beta)
                .register(zeta, epsilon, gamma, delta, iota, kappa);
    }

    // Eidechse, Lacerta
    // https://en.wikipedia.org/wiki/Lacerta
    private void registerLacerta() {
        Star alpha = newStar("Alpha Lacertae", ALPHA, "22h 31m 17.5010", "+50° 16′ 56.969", 3.78);
        Star beta = newStar("Beta Lacertae", BETA, "22h 23m 33.624", "+52° 13′ 44.56", 4.43);
        Star _4 = newStar("4 Lacertae", "4", "22h 24m 30.99068", "+49° 28′ 35.0176", 4.60);
        Star _5 = newStar("5 Lacertae", "5", "22h 29m 31.823", "+47° 42′ 24.79", 4.36);
        Star _2 = newStar("2 Lacertae", "2", "22h 21m 01.54727", "+46° 32′ 11.6461", 4.540);
        Star _1 = newStar("1 Lacertae", "1", "22h 15m 58.17673", "+37° 44′ 55.4385", 4.15);
        Star _6 = newStar("6 Lacertae", "6", "22h 30m 29.26005", "+43° 07′ 24.1565", 4.52);
        Star _11 = newStar("11 Lacertae", "11", "22h 40m 30.85881", "+44° 16′ 34.7042", 4.46);
        // useless: Star _9 = newStar("9 Lacertae", "9", "22h 50m 21.77464s", "+41° 57′ 12.2181″", 5.928);
        // useless: Star _10
        // useless: Star _14 = newStar("14 Lacertae", "14", "22h 50m 21.77464s", "+41° 57′ 12.2181″", 5.928);
        // useless: Star _15 = newStar("15 Lacertae", "15", "22h 52m 02.03426s", "+43° 18′ 44.6962″", 4.96);
        // useless: V424
        // useless: Star _16 = newStar("16 Lacertae", "16", "22h 56m 23.62966s", "+41° 36′ 13.9438″", 5.58);
        // Alpha Lacertae
        Star xx = newStar("HD 211073", "X", "22h 13m 52.72948", "+39° 42′ 53.7340", 4.49);
        // useless: Star yy = newStar("NGC 7243", "Y", "22h 15m 08.5s", "+49° 53′ 51″", 6.40);
        newConstellation("Lacerta", R.drawable.lacerta)
                .register(beta, alpha, _4, _5, _2, _6, xx, _1)
                .register(_6, _11, _5)
                .register();
    }

    // Ara, Altar, southern
    // https://en.wikipedia.org/wiki/Ara_(constellation)
    // https://www.constellation-guide.com/constellation-list/lacerta-constellation/
    private void registerAra() {
        Star alpha = newStar("Alpha Arae", ALPHA, "17h 31m 50.49153", "−49° 52′ 34.1220", 2.76);
        Star beta = newStar("Beta Arae", BETA, "17h 25m 17.98835", "−55° 31′ 47.5868", 2.84);
        Star gamma = newStar("Gamma Arae", GAMMA, "17h 25m 23.65931", "–56° 22′ 39.8148", 3.34);
        Star delta = newStar("Delta Arae", DELTA, "17h 31m 05.91272", "–60° 41′ 01.8522", 3.62);
        Star epsilon = newStar("Epsilon Arae", EPSILON, "16h 59m 35.04880", "–53° 09′ 37.5713", 4.068);
        Star zeta = newStar("Zeta Arae", ZETA, "16h 58m 37.21217", "–55° 59′ 24.5203", 3.13);
        Star eta = newStar("Eta Arae", ETA, "16h 49m 47.15653", "–59° 02′ 28.9575", 3.76);
        newConstellation("Ara", R.drawable.ara)
                .register(alpha, beta, gamma, delta, eta, zeta, epsilon, alpha);
    }

    // Wasserschlange (hydra)
    // https://en.wikipedia.org/wiki/Hydra_(constellation)
    private void registerHydra() {
        Star alpha = newStar("Alphard", ALPHA, "09h 27m 35.2433", "−08° 39′ 30.969", 2.00);
        Star beta = newStar("Beta Hydrae", BETA, "11h 52m 54.521", "−33° 54′ 29.25", 4.276);
        Star gamma = newStar("Gamma Hydrae", GAMMA, "13h 18m 55.29719", "–23° 10′ 17.4514", 2.993);
        Star delta = newStar("Delta Hydrae", DELTA, "08h 37m 39.36627", "+05° 42′ 13.6057", 4.146);
        Star epsilon = newStar("Ashlesha", EPSILON, "8h 46m 46.51223", "+06° 25′ 07.6855", 3.38);
        Star zeta = newStar("Zeta Hydrae", ZETA, "08h 55m 23.62614", "+05° 56′ 44.0354", 3.10);
        Star eta = newStar("Eta Hydrae", ETA, "08h 43m 13.47499", "+03° 23′ 55.1867", 4.294);
        Star theta = newStar("Theta Hydrae", THETA, "08h 43m 13.47499", "+03° 23′ 55.1867", 3.888);
        Star iota = newStar("Ukdah", IOTA, "09h 39m 51.36145", "−01° 08′ 34.1135", 3.91);
        Star kappa = newStar("Kappa Hydrae", KAPPA, "09h 40m 18.36496", "−14° 19′ 56.2675", 5.06);
        Star lambda = newStar("Lambda Hydrae", LAMBDA, "10h 10m 35.27667", "−12° 21′ 14.6938", 3.61);
        Star mu = newStar("Mu Hydrae", MU, "10h 26m 05.42630", "−14° 19′ 56.2675", 3.83);
        Star nu = newStar("Nu Hydrae", NU, "10h 49m 37.48875", "–16° 11′ 37.1360", 3.115);
        Star rho = newStar("Rho Hydrae", RHO, "08h 48m 25.97057", "+05° 50′ 16.1283", 4.34);
        Star upsilon = newStar("Zhang", UPSILON, "09h 51m 28.69384", "−14° 50′ 47.7710", 4.12);
        Star tau = newStar("Tau Hydrae", TAU, "09h 29m 08.89655", "−02° 46′ 08.2649", 4.59);
        Star xi = newStar("Xi Hydra", XI, "11h 33m 00.11505", "−31° 51′ 27.4435", 3.54);
        Star omicron = newStar("Omicron Hydrae", OMICRON, "11h 40m 12.78970", "−34° 44′ 40.7733", 4.70);
        Star pi = newStar("Pi Hydrae", PI, "14h 06m 22.29749", "–26° 40′ 56.5024", 3.25);
        Star phi = newStar("Phi Hydrae", PHI, "10h 38m 34.95281", "−16° 52′ 35.6665", 4.90);
        Star chi = newStar("Chi Hydrae", CHI, "11h 05m 19.90766", "−27° 17′ 36.9957", 4.94);
        Star sigma = newStar("Minchir", SIGMA, "08h 38m 45.43747", "+03° 20′ 29.1701", 4.48);
        Star psi = newStar("Psi Hydrae", PSI, "13h 09m 03.27026", "−23° 07′ 05.0501", 4.97);
        Star omega = newStar("Omega Hydrae", OMEGA, "09h 05m 58.36642", "+05° 05′ 32.3360", 5.00);
        newConstellation("Hydra", R.drawable.hydra)
                .register(rho, eta, sigma, delta, epsilon, zeta, theta, iota, alpha, upsilon, lambda, mu, phi, nu, chi, xi, delta, gamma, pi)
        .register(omega)
        .register(tau)
        .register(kappa)
        .register(omicron)
        .register(psi);
    }

    // Widder, Aries
    // https://en.wikipedia.org/wiki/Aries_(constellation)
    // https://wissen.naanoo.de/esoterik/sternzeichen-widder
    // The Ram (englisch)
    // Aries (latein)
    // Krios (griechisch)
    // Mesa (sanskrit)
    // (1) 21. März – 20. April
    private void registerAries() {
        Star alpha = newStar("Hamal", ALPHA, "02h 07m 10.40570", "+23° 27′ 44.7032", 2.00);
        Star beta = newStar("Beta Arietis", BETA, "01h 54m 38.41099", "+20° 48′ 28.9133", 2.655);
        Star gamma = newStar("Gamma Arietis", GAMMA, "01h 53m 31.81479", "+19° 17′ 37.8790", 3.86);
        Star delta = newStar("Delta Arietis", DELTA, "03h 11m 37.76465", "+19° 43′ 36.0397", 4.349);
        Star epsilon = newStar("Epsilon Arietis", EPSILON, "02h 59m 12.72536", "+21° 20′ 25.5575", 4.63);
        Star zeta = newStar("Zeta Arietis", ZETA, "03h 14m 54.09731", "+21° 02′ 40.0103", 4.89);
        Star _41 = newStar("41 Arietis", "41", "02h 49m 59.03324", "+27° 15′ 37.8260", 3.63);
        newConstellation("Aries", R.drawable.constellation)
                .register(epsilon, _41, alpha, beta, gamma)
                .register(delta)
                .register(zeta);
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
    private void registerTaurus() {
        Star alpha = newStar("Aldebaran", ALPHA, "04h 35m 55.23907", "+16° 30′ 33.4885", 0.86);
        Star beta = newStar("Beta Tauri", BETA, "05h 26m 17.51312", "+28° 36′ 26.8262", 1.65);
        Star zeta = newStar("Zeta Tauri", ZETA, "05h 37m 38.68542", "+21° 08′ 33.1588", 3.01);
        Star theta = newStar("Theta Tauri", THETA, "04h 28m 34.49603", "+15° 57′ 43.8494", 3.84);
        Star lambda = newStar("Lambda Tauri", LAMBDA, "04h 00m 40.81572", "+12° 29′ 25.2259", 3.47);
        Star delta = newStar("Delta Tauri", DELTA, "04h 22m 56.09253", "+17° 32′ 33.0487", 3.772);
        Star epsilon = newStar("Epsilon Tauri", EPSILON, "04h 28m 37.00", "+19° 10′ 50", +3.53);
        Star omicron = newStar("Omicron Tauri", OMICRON, "03h 24m 48.79796", "+09° 01′ 43.9489",3.61);
        Star gamma = newStar("Gamma Tauri", GAMMA, "04h 19m 47.6037", "+15° 37′ 39.512", 3.654);
        Star xi = newStar("Xi Tauri", XI, "03h 27m 10.151", "+09° 43′ 57.63", 3.73);
        Star tau = newStar("Tau Tauri", TAU, "04h 42m 14.70161", "+22° 57′ 24.9214", 4.27);
        newConstellation("Taurus", R.drawable.taurus)
                .register(zeta, alpha, theta, gamma, delta, epsilon, beta)
                .register(gamma, lambda, xi, omicron)
                .register(tau);
     }

    // Zwilling, Gemini
    // https://en.wikipedia.org/wiki/Gemini_(constellation)
    // https://deepsky.astronomie.info/Gem/index.de.php
    // https://wissen.naanoo.de/esoterik/sternzeichen-zwilling
    // (3) 21. Mai – 21. Juni
    // The Twins (englisch)
    // Gemini (latein)
    // Didymoi (griechisch)
    // Mithuna (sanskrit)
    private void registerGemini() {
        Star alpha = newStar("Castor", ALPHA, "07h 34m 35.863", "+31° 53′ 17.79", 1.93);
        Star beta = newStar("Pollux", BETA, "07h 45m 18.94987", "+28° 01′ 34.3160", 1.14);
        Star theta = newStar("Theta Geminorum", THETA, "06h 52m 47.33887", "+33° 57′ 40.5175", 3.59);
        Star tau = newStar("Tau Geminorum", TAU, "07h 11m 08.37042", "+30° 14′ 42.5831", 4.42);
        Star iota = newStar("Iota Geminorum", IOTA, "07h 25m 43.59532", "+27° 47′ 53.0929", 3.791);
        Star upsilon = newStar("Upsilon Geminorum", UPSILON, "07h 35m 55.34970", "+26° 53′ 44.6751", 4.04);
        Star kappa = newStar("Kappa Geminorum", KAPPA, "07h 44m 26.85357", "+24° 23′ 52.7872", 3.568);
        Star delta = newStar("Delta Geminorum", DELTA, "07h 20m 07.37978", "+21° 58′ 56.3377", 3.53);
        Star zeta = newStar("Zeta Geminorum", ZETA, "07h 04m 06.53079", "+20° 34′ 13.0739", 3.93);
        Star gamma = newStar("Gamma Geminorum", GAMMA, "06h 37m 42.71050", "+16° 23′ 57.4095",1.915);
        Star lambda = newStar("Lambda Geminorum", LAMBDA, "07h 18m 05.57977", "+16° 32′ 25.3905", 3.571);
        Star xi = newStar("Xi Geminorum", XI, "06h 45m 17.36432", "+12° 53′ 44.1311", 3.35);
        Star epsilon = newStar("Epsilon Geminorum", EPSILON, "06h 43m 55.92626", "+25° 07′ 52.0515", 3.06);
        Star nu = newStar("Nu Geminorum", NU, "06h 28m 57.78613", "+20° 12′ 43.6856", 4.16);
        Star mu = newStar("Mu Geminorum", MU, "06h 22m 57.62686", "+22° 30′ 48.8979", 2.857);
        Star eta = newStar("Eta Geminorum", ETA, "06h 14m 52.657", "+22° 30′ 24.48", 3.15);
        newConstellation("Gemini", R.drawable.gemini)
                .register(theta, tau, iota, upsilon, kappa)
                .register(beta, upsilon, delta, zeta, gamma)
                .register(delta, lambda, xi)
                .register(alpha, tau, epsilon, nu)
                .register(epsilon, mu , eta);
    }

    // Krebs, Cancer
    // https://en.wikipedia.org/wiki/Cancer_(constellation)
    // https://wissen.naanoo.de/esoterik/sternzeichen-krebs
    // (4) 22. Juni – 22. Juli
    // The Crab (englisch)
    // Cancer (latein)
    // Karkinos (griechisch)
    // Karkaṭa (sanskrit)
    private void registerCancer() {
        Star alpha = newStar("Alpha Cancri", ALPHA, "08h 58m 29.2217", "+11° 51′ 27.723", 4.20);
        Star beta = newStar("Beta Cancri", BETA, "08h 16m 30.9206", "+09° 11′ 07.961", 3.50);
        Star delta = newStar("Delta Cancri", DELTA, "08h 44m 41.09921", "+18° 09′ 15.5034", 3.94);
        Star gamma = newStar("Gamma Cancri", GAMMA, "08h 43m 17.14820", "+21° 28′ 06.6008", 4.652);
        Star iota = newStar("Iota Cancri", IOTA, "08h 46m 41.81988", "+28° 45′ 35.6190", 4.02);
        Star epsilon = newStar("Epsilon Cancri", EPSILON, "08h 40m 27.01052", "+19° 32′ 41.3133", 6.29);
        newConstellation("Cancer", R.drawable.cancer)
                .register(iota, gamma, delta, beta)
                .register(delta, alpha)
                .register(epsilon);
    }

    // Löwe, Leo
    // https://en.wikipedia.org/wiki/Leo_(constellation)
    // https://wissen.naanoo.de/esoterik/sternzeichen-loewe
    // (5) 23. Juli – 23. August
    // The Lion (englisch)
    // Leo (latein)
    // Leōn (griechisch)
    // Siṃha (sanskrit)
    private void registerLeo() {
        Star alpha = newStar("Regulus", ALPHA, "10h 08m 22.311", "+11° 58′ 01.95", 1.40);
        Star eta = newStar("Eta Leonis", ETA, "10h 07m 19.95186", "+16° 45′ 45.592", 3.486);
        Star gamma = newStar("Gamma Leonis", GAMMA, "10h 19m 58.35056", "+19° 50′ 29.3468", 2.08);
        Star delta = newStar("Delta Leonis", DELTA, "11h 14m 06.50142", "+20° 31′ 25.3853", 2.56);
        Star beta = newStar("Denebola", BETA, "11h 49m 03.57834", "+14° 34′ 19.4090", 2.113);
        Star zeta = newStar("Zeta Leonis", ZETA, "10h 16m 41.41597", "+23° 25′ 02.3221", 3.33);
        Star mu = newStar("Mu Leonis", MU, "09h 52m 45.81654", "+26° 00′ 25.0319", 3.88);
        Star epsilon = newStar("Epsilon Leonis", EPSILON, "09h 45m 51.07330", "+23° 46′ 27.3208", 2.98);
        Star theta = newStar("Theta Leonis", THETA, "11h 14m 14.40446", "+15° 25′ 46.4541", 3.324);
        Star iota = newStar("Iota Leonis", IOTA, "11h 23m 55.45273", "+10° 31′ 46.2195", 4.00);
        Star sigma = newStar("Sigma Leonis", SIGMA, "11h 21m 08.1943", "+06° 01′ 45.558", 4.046);
        Star lambda = newStar("Lambda Leonis", LAMBDA, "09h 31m 43.22754", "+22° 58′ 04.6904", 4.32);
        Star kappa = newStar("Kappa Leonis", KAPPA, "09h 24m 39.25874", "+26° 10′ 56.3650", 4.460);
        Star omicron = newStar("Omicron Leonis", OMICRON, "09h 41m 09.03", "+09° 53′ 32.30", 3.52);
        Star rho = newStar("Rho Leonis", RHO, "10h 32m 48.67168", "+09° 18′ 23.7094", 3.83);
        newConstellation("Leo", R.drawable.leo)
                .register(alpha, eta, gamma, delta, beta)
                .register(gamma, zeta, mu, epsilon)
                .register(theta)
                .register(iota)
                .register(sigma)
                .register(lambda)
                .register(kappa)
                .register(omicron)
                .register(rho);
    }

    // Jungfrau, Virgo
    // https://en.wikipedia.org/wiki/List_of_stars_in_Virgo
    // https://wissen.naanoo.de/esoterik/sternzeichen-jungfrau
    // (6) 24. August – 23. September
    // The Maiden (englisch)
    // Virgo (latein)
    // Parthenos (griechisch)
    // Kanyā (sanskrit)
    private void registerVirgo() {
        Star spica = newStar("Spica", ALPHA, "13h 25m 11.60", "−11° 09′ 40.5", 0.98);
        Star gamma = newStar("Gamma Virginis", GAMMA, "12h 41m 39.64344", "–01° 26′ 57.7421", 2.74);
        Star epsilon = newStar("Epsilon Virginis", EPSILON, "13h 02m 10.59785", "+10° 57′ 32.9415", 2.826);
        Star zeta = newStar("Zeta Virginis", ZETA,"13h 34m 41.591", "–00° 35′ 44.95", 3.376);
        Star delta = newStar("Delta Virginis", DELTA, "12h 55m 36.20861", "+3° 23′ 50.8932", 3.32);
        Star beta = newStar("Beta Virginis", BETA, "11h 50m 41.71824", "+1° 45′ 52.9910", 3.604);
        Star eta = newStar("Eta Virginis", ETA, "12h 19m 54.35783", "–00° 40′ 00.5095", 3.890);
        Star nu = newStar("Nu Virginis", NU, "11h 45m 51.55957", "+06° 31′ 45.7413", 4.04);
        Star theta = newStar("Theta Virginis", THETA,"13h 09m 56.99067", "−05° 32′ 20.4185", 4.37);
        newConstellation("Virgo", R.drawable.virgo)
                .register(spica, theta, gamma, eta, beta, nu)
                .register(epsilon, delta, gamma)
                .register(zeta, theta);
    }

    // Waage (Libra)
    // https://en.wikipedia.org/wiki/Libra_(constellation)
    // https://wissen.naanoo.de/esoterik/sternzeichen-waage
    // (7) 24. September – 23. Oktober
    // The Scales (englisch)
    // Libra (latein)
    // Zygos (griechisch)
    // Tulā (sanskrit)
    private void registerLibra() {
        Star alpha = newStar("Alpha Librae", ALPHA, "14h 50m 41.18097", "–15° 59′ 50.0482", 2.741);
        Star beta = newStar("Beta Librae", BETA, "15h 17m 00.41382", "−09° 22′ 58.4919", 2.61);
        Star gamma = newStar("Gamma Librae", GAMMA, "15h 35m 31.57881", "−14° 47′ 22.3278", 3.91);
        Star delta = newStar( "Delta Librae", DELTA, "15h 00m 58.35013", "−08° 31′ 08.2063", 4.93);
        Star sigma = newStar("Sigma Librae", SIGMA, "15h 04m 04.21608", "–25° 16′ 55.0606", 3.20);
        Star tau = newStar("Tau Librae", TAU, "15h 38m 39.36950", "−29° 46′ 39.8956",  3.68);
        Star upsilon = newStar("Upsilon Librae", UPSILON, "15h 37m 01.45020", "−28° 08′ 06.2926", 3.628);
        newConstellation("Libra", R.drawable.libra)
                .register(sigma, alpha, beta, gamma, tau, upsilon)
                .register(alpha, gamma);
    }

    // Skorpion (Scorpius)
    // https://en.wikipedia.org/wiki/Scorpius
    // https://wissen.naanoo.de/esoterik/sternzeichen-skorpion
    // (8) 24. Oktober – 22. November
    // The Scorpion (englisch)
    // Scorpio (latein)
    // Skorpios (griechisch)
    // Vṛścika (sanskrit)
    private void registerScorpius() {
        Star nu = newStar("Nu Scorpii", NU, "16h 11m 59.740", "−19° 27′ 38.33", 4.349);
        Star beta = newStar("Beta Scorpii", BETA, "16h 05m 26.23198", "–19° 48′ 19.6300", 2.62);
        Star delta = newStar("Delta Scorpii", DELTA, "16h 00m 20.00528", "–22° 37′ 18.1431", 2.307);
        Star pi = newStar("Pi Scorpii", PI, "15h 58m 51.11324", "−26° 06′ 50.7886", 2.89);
        Star rho = newStar("Rho Scorpii", RHO, "15h 56m 53.07624", "−29° 12′ 50.6612", 3.86);
        Star sigma = newStar("Sigma Scorpii", SIGMA, "16h 21m 11.31571", "–25° 35′ 34.0515", 2.88);
        Star antares = newStar("Antares", ALPHA, "16h 29m 24.45970", "−26° 25′ 55.2094", 0.6);
        Star tau = newStar("Tau Scorpii", TAU, "16h 35m 52.95285", "−28° 12′ 57.6615", 2.82);
        Star epsilon = newStar("Epsilon Scorpii", EPSILON, "16h 50m 09.8", "–34° 17′ 36", 2.310);
        Star mu = newStar("Mu Scorpii", MU, "16h 51m 52.23111", "−38° 02′ 50.5694", 3.04);
        Star zeta = newStar("Zeta Scorpii", ZETA, "16h 53m 59.72650", "−42° 21′ 43.3063", 3.59);
        Star eta = newStar("Eta Scorpii", ETA, "17h 12m 09.19565", "–43° 14′ 21.0905", 3.33);
        Star iota = newStar("Iota Scorpii", IOTA, "17h 47m 35.08113", "−40° 07′ 37.1893", 3.03);
        Star kappa = newStar("Kappa Scorpii", KAPPA, "17h 42m 29.27520", "–39° 01′ 47.9391", 2.39);
        Star theta = newStar("Theta Scorpii", THETA, "17h 37m 19.12985", "–42° 59′ 52.1808", 1.84);
        Star lambda = newStar("Lambda Scorpii", LAMBDA, "17h 33m 36.520", "−37° 06′ 13.76", 1.62);
        Star upsilon = newStar("Upsilon Scorpii", UPSILON, "17h 30m 45.83712", "–37° 17′ 44.9285", 2.70);
        newConstellation("Scorpius", R.drawable.scorpius)
                .register(sigma, nu)
                .register(sigma, beta)
                .register(sigma, delta)
                .register(sigma, pi)
                .register(sigma, rho)
                .register(sigma, antares, tau, epsilon, mu, zeta, eta, theta, iota, kappa, lambda)
                .register(kappa, upsilon);
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
    private void registerSagittarius() {
        Star delta = newStar("Delta Sagittarii", DELTA, "18h 20m 59.64354", "−29° 49′ 41.1659", 2.70);
        Star epsilon = newStar("Epsilon Sagittarii", EPSILON, "18h 24m 10.31840", "–34° 23′ 04.6193", 1.85);
        Star zeta = newStar("Zeta Sagittarii", ZETA, "19h 02m 36.73024", "–29° 52′ 48.2279", 2.59);
        Star phi = newStar("Phi Sagittarii", PHI, "18h 45m 39.38610", "–26° 59′ 26.7944", 3.17);
        Star lambda = newStar("Lambda Sagittarii", LAMBDA, "18h 27m 58.24072", "−25° 25′ 18.1146", 2.82);
        Star gamma = newStar("Gamma Sagittarii", GAMMA, "18h 05m 48.48810", "–30° 25′ 26.7235", 2.98);
        Star sigma = newStar("Sigma Sagittarii", SIGMA, "18h 55m 15.92650", "–26° 17′ 48.2068", 2.05);
        Star tau = newStar("Tau Sagittarii", TAU, "19h 06m 56.40897", "–27° 40′ 13.5189", 3.326);
        Star eta = newStar("Eta Sagittarii", ETA, "18h 17m 37.63505", "−36° 45′ 42.0667", 3.11);
        Star pi = newStar("Pi Sagittarii", PI, "19h 09m 45.83293", "–21° 01′ 25.0103", 2.89);
        Star xi = newStar("Xi1 Sagittarii", XI, "18h 57m 20.47670", "−20° 39′ 22.8539", 5.06);
        Star mu = newStar("Mu Sagittarii", MU, "18h 13m 45.8", "−21° 03′ 32", 3.85);
        newConstellation("Teapot", R.drawable.teapot)
                .register(epsilon, delta, gamma, epsilon, zeta, phi, delta, lambda, phi, sigma, tau, zeta);
        newConstellation("Sagittarius", R.drawable.sagittarius)
                .register(eta, epsilon, delta, lambda, mu)
                .register(lambda, phi, sigma)
                .register(xi, pi, sigma, tau, zeta);
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
    private void registerCapricornus() {
        Star alpha = newStar("Alpha Capricorni", ALPHA, "20h 18m 03.25595", "−12° 32′ 41.4684", 3.57);
        Star beta = newStar("Beta Capricorni", BETA, "20h 21m 00.7", "−14° 46′ 53", 3.05);
        Star psi = newStar("Psi Capricorni", PSI, "20h 46m 05.73263", "−25° 16′ 15.2312", 4.13);
        Star omicron = newStar("Omicron Capricorni", OMICRON, "20h 29m 53.91117", "−18° 34′ 59.4803", 5.94);
        Star omega = newStar("Omega Capricorni", OMEGA, "20h 51m 49.29084", "−26° 55′ 08.8574", 4.11);
        Star zeta = newStar("Zeta Capricorni", ZETA, "21h 26m 40.02634", "−22° 24′ 40.8042", 3.74);
        Star epsilon = newStar("Epsilon Capricorni", EPSILON, "21h 37m 04.83068", "−19° 27′ 57.6464", 4.62);
        Star delta = newStar("Delta Capricorni", DELTA, "21h 47m 02.44424", "−16° 07′ 38.2335", 2.81);
        Star gamma = newStar("Gamma Capricorni", GAMMA, "21h 40m 05.4563", "−16° 39′ 44.308", 3.67);
        Star theta = newStar("Theta Capricorni", THETA, "21h 05m 56.82783", "−17° 13′ 58.3021", 4.07);
        newConstellation("Capricornus", R.drawable.capricornus)
                .register(alpha, beta, psi, omega, zeta, epsilon, delta, gamma, theta, alpha)
                .register(omicron);
    }

    // Wassermann
    // https://en.wikipedia.org/wiki/Aquarius_(constellation)
    // https://wissen.naanoo.de/esoterik/sternzeichen-wassermann
    // (11) 21. Januar – 19. Februar
    // The Water-Bearer (englisch)
    // Aquarius (latein)
    // Hydrokhoos (griechisch)
    // Kumbha (sanskrit)
    private void registerAquarius() {
        Star epsilon = newStar("Epsilon Aquarii", EPSILON, "20h 47m 40.55260", "−09° 29′ 44.7877", 3.77);
        Star mu = newStar("Mu Aquarii", MU, "20h 52m 39.23277", "−08° 58′ 59.9499", 4.731);
        Star beta = newStar("Beta Aquarii", BETA, "21h 31m 33.53171", "–05° 34′ 16.2320", 2.87);
        Star alpha = newStar("Alpha Aquarii", ALPHA, "22h 05m 47.03593", "−00° 19′ 11.4568", 2.942);
        Star pi = newStar("Pi Aquarii", PI, "22h 25m 16.62285", "+01° 22′ 38.6346", 4.42);
        Star zeta = newStar("Zeta Aquarii", ZETA, "22h 28m 49.90685", "–00° 01′ 11.7942", 3.65);
        Star eta = newStar("Eta Aquarii", ETA, "22h 35m 21.38126", "–00° 07′ 02.9888", 4.04);
        Star gamma = newStar("Gamma Aquarii", GAMMA, "22h 21m 39.37542", "–01° 23′ 14.4031", 3.849);
        Star iota = newStar("Iota Aquarii", IOTA, "22h 06m 26.22742", "–13° 52′ 10.8615", 4.279);
        Star theta = newStar("Theta Aquarii", THETA, "22h 16m 50.03635", "–07° 46′ 59.8480", 4.175);
        Star lambda = newStar("Lambda Aquarii", LAMBDA, "22h 52m 36.87441", "−07° 34′ 46.5542", 3.722);
        Star tau = newStar("Tau Aquarii", TAU, "22h 49m 35.50157", "–13° 35′ 33.4767", 4.042);
        Star delta = newStar("Delta Aquarii", DELTA, "22h 54m 39.0125", "−15° 49′ 14.953", 3.252);
        Star psi = newStar("Psi Aquarii", PSI, "23h 17m 54.21372", "–09° 10′ 57.0675", 4.403);
        Star chi = newStar("Chi Aquarii", CHI, "23h 16m 50.93916", "−07° 43′ 35.4023", 5.06);
        Star phi = newStar("Phi Aquarii", PHI, "23h 14m 19.35787", "–06° 02′ 56.3998", 4.223);
        newConstellation("Aquarius", R.drawable.aquarius)
                .register(epsilon, mu, beta, alpha, pi, zeta, eta)
                .register(zeta, gamma, alpha)
        .register(beta, iota)
        .register(alpha, theta, lambda, tau, delta, psi, chi, phi, lambda);
    }

    // Fische (Pisces)
    // https://en.wikipedia.org/wiki/Pisces_(constellation)
    // https://wissen.naanoo.de/esoterik/sternzeichen
    // (12) 20. Februar – 20. März
    // The Fish (englisch)
    // Pisces (latein)
    // Ikhthyes (griec)hisch)
    // Mīna (sanskrit)
    private void registerPisces() {
        Star alpha = newStar("Alpha Piscium", ALPHA, "02h 02m 02.81972", "+02° 45′ 49.5410", 3.82);
        Star beta = newStar("Beta Piscium", BETA, "23h 03m 52.61349", "+03° 49′ 12.1662", +4.40);
        Star gamma = newStar("Gamma Piscium", GAMMA, "23h 17m 09.93749", "+03° 16′ 56.2380", 3.699);
        Star delta = newStar("Delta Piscium", DELTA, "00h 48m 40.94433", "+07° 35′ 06.2926", 4.416);
        Star epsilon = newStar("Epsilon Piscium", EPSILON, "01h 02m 56.60862", "+07° 53′ 24.4855", 4.27);
        Star zeta = newStar("Zeta Piscium", ZETA, "01h 13m 45.17477", "+07° 34′ 31.2745", 5.28);
        Star eta = newStar("Eta Piscium", ETA, "01h 31m 29.01026", "+15° 20′ 44.9685", 3.611);
        Star theta = newStar("Theta Piscium", THETA, "23h 27m 58.09529", "+6° 22′ 44.3720", 4.27);
        Star iota = newStar("Iota Piscium", IOTA, "23h 39m 57.04138", "+05° 37′ 34.6475", 4.13);
        Star kappa = newStar("Kappa Piscium", KAPPA, "23h 26m 55.95586", "+01° 15′ 20.1900", 4.94);
        Star lambda = newStar("Lambda Piscium", LAMBDA, "23h 42m 02.80612", "+01° 46′ 48.1484", 4.49);
        Star mu = newStar("Mu Piscium", MU, "01h 30m 11.11444", "+06° 08′ 37.7577", 4.84);
        Star nu = newStar("Nu Piscium", NU, "01h 41m 25.89391", "+05° 29′ 15.4062", 4.44);
        Star xi = newStar("Xi Piscium", XI, "01h 45m 23.63185", "+09° 09′ 27.8530", 4.60);
        Star omicron = newStar("Omicron Piscium", OMICRON, "01h 45m 23.63185", "+09° 09′ 27.8530", 4.27);
        Star pi = newStar("Pi Piscium", PI, "01h 37m 05.91523", "+12° 08′ 29.5186", 5.60);
        Star rho = newStar("Rho Piscium", RHO, "01h 26m 15.26209", "+19° 10′ 20.4526", 5.344);
        Star sigma = newStar("Sigma Piscium", SIGMA, "01h 02m 49.09645", "+31° 48′ 15.3471", 5.50);
        Star tau = newStar("Tau Piscium", TAU, "01h 11m 39.63647", "+30° 05′ 22.6909", 4.518);
        Star upsilon = newStar("Upsilon Piscium", UPSILON, "01h 19m 27.99289", "+27° 15′ 50.6155", 4.752);
        Star phi = newStar("Phi Piscium", PHI, "01h 13m 44.9471", "+24° 35′ 01.367", 4.676);
        Star omega = newStar("Omega Piscium", OMEGA, "23h 59m 18.69064", "+06° 51′ 47.9562", 4.01);
        Star psi = newStar("Psi Piscium", PSI, "01h 05m 40.95527", "+21° 28′ 23.4489", 5.273);
        Star chi = newStar("Chi Piscium", CHI, "01h 11m 27.21877", "+21° 02′ 04.7406", 4.64);
        newConstellation("Pisces", R.drawable.pisces)
                .register(iota, lambda, kappa, gamma, theta, iota, omega, delta, epsilon, zeta, mu, nu, alpha, xi, omicron, pi, eta, rho, phi, tau, upsilon, phi)
                .register(beta)
                .register(sigma)
                .register(chi)
                .register(psi);
    }

    private void registerISS() {
        // TLE from NORAD, Jan 18 2017, 19:27
        newSatellite("ISS", "1 25544U 98067A   17018.16759178  .00002139  00000-0  39647-4 0  9999\r\n2 25544  51.6445  66.1799 0007746  95.7219  10.7210 15.54083144 38425");
    }

    private void registerSpecialElements() {
        newSpecialElement("S", Hour.fromDegree(0), Degree.fromNegative(90, 0, 0));
        newSpecialElement( "N", Hour.fromDegree(0), Degree.fromPositive(90, 0, 0));
    }
}


