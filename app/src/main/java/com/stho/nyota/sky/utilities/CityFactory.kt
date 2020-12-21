package com.stho.nyota.sky.utilities

import java.util.*

// TODO: create city with image
// TODO: store city image in DB
// TODO: let use choose the image for a city
// TODO: restore city data in "edit city"

internal fun City.Companion.createCities(): List<City> =
    listOf(
        createDefaultBerlinBuch(),
        createDefaultLusaka(),
        createDefaultMonrovia(),
        createDefaultKigali(),
        createDefaultDaressalaam(),
        createDefaultLagos(),
        createDefaultMunich(),
        createDefaultSassnitz(),
        createDefaultSalamanca(),
        createDefaultParis(),
        createDefaultHamburg(),
        createDefaultEichstaett(),
    )

internal val City.Companion.defaultLocationBerlinBuch: Location
    get() = Location(BERLIN_BUCH_LATITUDE, BERLIN_BUCH_LONGITUDE, BERLIN_BUCH_ALTITUDE)

internal val City.Companion.defaultTimeZoneCET: TimeZone
    get() = TimeZone.getTimeZone("CET") // Central European Time, GMT+1

internal fun City.Companion.createDefaultBerlinBuch(): City =
    City.createNewCity(BERLIN, defaultLocationBerlinBuch, defaultTimeZoneCET)

private fun City.Companion.createDefaultLusaka(): City {
    val location = Location(-15.3875, 28.3228, 1.279)
    val timeZone = TimeZone.getTimeZone("CAT") // Central African Time, GMT+2
    return City.createNewCity("Lusaka", location, timeZone)
}

private fun City.Companion.createDefaultMonrovia(): City {
    val location = Location(6.3156, -10.8074, 0.174)
    val timeZone = TimeZone.getTimeZone("GMT")
    return City.createNewCity("Monrovia", location, timeZone)
}

private fun City.Companion.createDefaultKigali(): City {
    val location = Location(-1.9441, 30.0619, 1.567)
    val timeZone = TimeZone.getTimeZone("CAT") // Central African Time, GMT+2
    return City.createNewCity("Kigali", location, timeZone)
}

private fun City.Companion.createDefaultDaressalaam(): City {
    val location = Location(-6.792354, 39.2083284, 0.061)
    val timeZone = TimeZone.getTimeZone("EAT") // East African Time, GMT+3
    return City.createNewCity("Daressalaam", location, timeZone)
}

private fun City.Companion.createDefaultLagos(): City {
    val location = Location(6.5244, 3.3792, 0.041)
    val timeZone = TimeZone.getTimeZone("WAT") // West Africa Standard Time, GMT+1
    return City.createNewCity("Lagos", location, timeZone)
}

internal fun City.Companion.createDefaultMunich(): City {
    val location = Location(48.1351, 11.5820, 0.519)
    val timeZone = TimeZone.getTimeZone("CET") // Central European Standard Time
    return City.createNewCity("München", location, timeZone)
}

private fun City.Companion.createDefaultSassnitz(): City {
    val location = Location(54.5159131, 13.6331916, 0.030)
    val timeZone = TimeZone.getTimeZone("CET") // Central European Standard Time
    return City.createNewCity("Sassnitz", location, timeZone)
}

private fun City.Companion.createDefaultSalamanca(): City {
    val location = Location(40.9701039, -5.6635397, 0.802)
    val timeZone = TimeZone.getTimeZone("CET") // Central European Standard Time
    return City.createNewCity("Salamanca", location, timeZone)
}

private fun City.Companion.createDefaultParis(): City {
    val location = Location(48.8566, 2.3522, 0.035)
    val timeZone = TimeZone.getTimeZone("CET") // Central European Standard Time
    return City.createNewCity("Salamanca", location, timeZone)
}

private fun City.Companion.createDefaultHamburg(): City {
    val location = Location(53.5511, 9.9937, 0.006)
    val timeZone = TimeZone.getTimeZone("CET") // Central European Standard Time
    return City.createNewCity("Hamburg", location, timeZone)
}

private fun City.Companion.createDefaultEichstaett(): City {
    val location = Location(48.8912508, 11.189986, 0.393)
    val timeZone = TimeZone.getTimeZone("CET") // Central European Standard Time
    return City.createNewCity("Eichstätt", location, timeZone)
}
