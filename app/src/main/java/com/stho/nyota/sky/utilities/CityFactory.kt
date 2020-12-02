package com.stho.nyota.sky.utilities

import com.stho.nyota.R
import java.util.*

internal fun City.Companion.createCities(): List<City> {
    return listOf(
        createDefaultBerlin(),
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
        createNewCity("You", true)
    )
}

internal fun City.Companion.createDefaultBerlin(): City {
    val location = Location(BERLIN_LATITUDE, BERLIN_LONGITUDE, BERLIN_ALTITUDE)
    val timeZone = TimeZone.getTimeZone("CET") // Central European Time, GMT+1
    return City(BERLIN, location, timeZone)
}

private fun City.Companion.createDefaultLusaka(): City {
    val location = Location(-15.3875, 28.3228, 1.279)
    val timeZone = TimeZone.getTimeZone("CAT") // Central African Time, GMT+2
    return City("Lusaka", location, timeZone)
}

private fun City.Companion.createDefaultMonrovia(): City {
    val location = Location(6.3156, -10.8074, 0.174)
    val timeZone = TimeZone.getTimeZone("GMT")
    return City("Monrovia", location, timeZone)
}

private fun City.Companion.createDefaultKigali(): City {
    val location = Location(-1.9441, 30.0619, 1.567)
    val timeZone = TimeZone.getTimeZone("CAT") // Central African Time, GMT+2
    return City("Kigali", location, timeZone)
}

private fun City.Companion.createDefaultDaressalaam(): City {
    val location = Location(-6.792354, 39.2083284, 0.061)
    val timeZone = TimeZone.getTimeZone("EAT") // East African Time, GMT+3
    return City("Daressalaam", location, timeZone)
}

private fun City.Companion.createDefaultLagos(): City {
    val location = Location(6.5244, 3.3792, 0.041)
    val timeZone = TimeZone.getTimeZone("WAT") // West Africa Standard Time, GMT+1
    return City("Lagos", location, timeZone)
}

private fun City.Companion.createDefaultMunich(): City {
    val location = Location(48.1351, 11.5820, 0.519)
    val timeZone = TimeZone.getTimeZone("CET") // Central European Standard Time
    return City("München", location, timeZone)
}

private fun City.Companion.createDefaultSassnitz(): City {
    val location = Location(54.5159131, 13.6331916, 0.030)
    val timeZone = TimeZone.getTimeZone("CET") // Central European Standard Time
    return City("Sassnitz", location, timeZone)
}

private fun City.Companion.createDefaultSalamanca(): City {
    val location = Location(40.9701039, -5.6635397, 0.802)
    val timeZone = TimeZone.getTimeZone("CET") // Central European Standard Time
    return City("Salamanca", location, timeZone)
}

private fun City.Companion.createDefaultParis(): City {
    val location = Location(48.8566, 2.3522, 0.035)
    val timeZone = TimeZone.getTimeZone("CET") // Central European Standard Time
    return City("Salamanca", location, timeZone)
}

private fun City.Companion.createDefaultHamburg(): City {
    val location = Location(53.5511, 9.9937, 0.006)
    val timeZone = TimeZone.getTimeZone("CET") // Central European Standard Time
    return City("Hamburg", location, timeZone)
}
