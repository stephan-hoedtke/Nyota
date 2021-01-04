package com.stho.nyota.sky.utilities

import android.location.LocationManager
import com.stho.nyota.R
import java.util.*

/*
    Remark: for the timeZone we can use the timezone ids
    public static Set<String> getAvailableIDs (TimeZone.SystemTimeZoneType zoneType,
                String region,
                Integer rawOffset)
 */
class City private constructor(override var id: Long, var name: String, var location: Location, var timeZone: TimeZone, val isAutomatic: Boolean = false) : ILocation, IDBObject {

    override val uniqueTransientId: Long by lazy {
        System.nanoTime()
    }

    override var status: IDBObject.Status = IDBObject.Status.NEW

    override val isToDelete
        get() = IDBObject.isToDelete(status)

    override val isNew
        get() = IDBObject.isNew(status)

    override val isPersistent
        get() = IDBObject.isPersistent(status)

    val nameEx: String
        get() = if (isAutomatic) "$name*" else name

    override fun toString(): String =
        "$name $location $timeZone"

    val imageId: Int by lazy {
        when {
            name.contains("berlin", true) -> R.drawable.city_berlin
            name.contains("münchen", true) -> R.drawable.city_munic
            name.contains("sassnitz", true) -> R.drawable.city_sassnitz
            name.contains("salaam", true) -> R.drawable.city_daressalaam
            name.contains("lagos", true) -> R.drawable.city_lagos
            name.contains("monrovia",true) -> R.drawable.city_monrovia
            name.contains("lusaka", true) -> R.drawable.city_lusaka
            name.contains("kigali", true) -> R.drawable.city_kigali
            name.contains("paris", true) -> R.drawable.city_paris
            name.contains("hamburg", true) -> R.drawable.city_hamburg
            name.contains("venice", true) -> R.drawable.city_venice
            name.contains("salamanca", true) -> R.drawable.city_salamanca
            name.contains("hamburg", ignoreCase = true) -> R.drawable.city_hamburg
            name.contains("eichst", true) -> R.drawable.city_eichstaett
            else -> R.drawable.city_lights
        }
    }

    val copyright: Copyright by lazy {
        when {
            name.contains("münchen", true) ||
            name.contains("paris", true) ||
            name.contains("hamburg", true) ||
            name.contains("venice", true) -> Copyright(
                text = "Copyright © Horst & Daniel Zielske, Magical Realism, LUMAS",
                link = "https://www.lumas.de/pictures/horst_daniel_zielske/horst_daniel_zielske_magical_realism/",
                imageId = R.drawable.logo_magic_realism
            )

            name.contains("salamanca", true) -> Copyright(
                text = "Copyright © 1995-2020 EducationDynamics, LLC",
                link = "https://www.studyabroad.com/in-spain/salamanca",
                imageId = R.drawable.logo_sab
            )

            name.contains("sassnitz", true) -> Copyright(
                text = "Copyright © Kreuzfahrer-Guide",
                link = "https://kreuzfahrerguide.de/sassnitz-hafen-tipps-ausfluege/",
                imageId = R.drawable.logo_kreuzfahrer_guide
            )

            name.contains("eichst", true) -> Copyright(
                text = "Copyright © 2020 Wikipedia",
                link = "https://en.wikipedia.org/wiki/Eichst%C3%A4tt",
                imageId = R.drawable.logo_eichstaett
            )

            name.contains("daressalaam", true) ||
            name.contains("monrovia", true) ||
            name.contains("lagos", ignoreCase = true) ||
            name.contains("lusaka", true) ||
            name.contains("kigali", true) -> Copyright(
                text = "Copyright © 2020 Stephan Hödtke",
                link = "bbc.co.uk",
                imageId = R.drawable.nyota
            )

            else -> Copyright(
                text = "Copyright © Amyn Nasser, LUMAS",
                link = "https://www.lumas.de/pictures/amyn_nasser/amcdxxiii_the_little_house_that_fought_demolition/?gclid=EAIaIQobChMI4IzG3ICq7QIVteq7CB1b9QnMEAEYASADEgJSsfD_BwE\n",
                imageId = R.drawable.nyota
            )
        }
    }

    class Copyright(val text: String, val link: String, val imageId: Int)

    private fun isNearTo(otherLocation: ILocation): Boolean =
        location.isNearTo(otherLocation)

    private fun getHorizontalDistanceInKmTo(otherLocation: ILocation): Double =
        location.getHorizontalDistanceInKmTo(otherLocation)

    override val latitude: Double
        get() = location.latitude

    override val longitude: Double
        get() = location.longitude

    override val altitude: Double
        get() = location.altitude

    fun matches(otherCity: City): Boolean =
        when {
            (name.compareTo(otherCity.name, ignoreCase = true) == 0) -> true
            (isAutomatic && otherCity.isAutomatic) -> true
            (isNearTo(otherCity.location)) -> true
            else -> false
        }

    var distanceInKm: Double = 0.0
        private set

    fun updateDistance(referenceLocation: ILocation) {
        distanceInKm = getHorizontalDistanceInKmTo(referenceLocation)
    }

    internal fun createDefault(): City =
        City.createCities().firstOrNull { it.name == this.name } ?: this

    companion object {

        internal const val BERLIN = "Berlin"
        internal const val BERLIN_BUCH_LATITUDE = 52.6425
        internal const val BERLIN_BUCH_LONGITUDE = 13.4925
        internal const val BERLIN_BUCH_ALTITUDE = 0.1037 // in km
        internal const val NEW_CITY = "New City"

        internal fun createNewAutomaticCity(): City =
            City(id = 0L, name = "You", location = Location.getDefault(), timeZone = TimeZone.getDefault(), isAutomatic = true)

        internal fun createNewCity(cityName: String): City =
            City(id = 0L, name = cityName, location = Location.getDefault(), timeZone = TimeZone.getDefault())

        internal fun createNewCity(cityName: String, location: Location, timeZone: TimeZone): City =
            City(id = 0L, name = cityName, location = location, timeZone = timeZone)

        internal fun createNewCityFor(location: Location): City =
            City(id = 0L, name = "-", location = location, timeZone = TimeZone.getDefault())

        internal fun createCityWithId(id: Long, name: String, location: Location, timeZone: TimeZone): City =
            City(id, name, location, timeZone, false)

        internal fun getLastKnownAndroidLocationFromLocationManager(locationManager: LocationManager): android.location.Location? {
            var location: android.location.Location? = null
            try {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            } catch (ex: SecurityException) {
                // ignore
            }
            return location
        }

        internal val availableTimeZones: Array<String>
            get() = TimeZone.getAvailableIDs()

    }
}
