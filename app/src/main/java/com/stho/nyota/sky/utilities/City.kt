package com.stho.nyota.sky.utilities

import android.location.LocationManager
import com.stho.nyota.R
import com.stho.nyota.sky.universe.Earth
import java.util.*
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/*
    Remark: for the timeZone we can use the timezone ids
    public static Set<String> getAvailableIDs (TimeZone.SystemTimeZoneType zoneType,
                String region,
                Integer rawOffset)
 */
class City internal constructor(var name: String, var location: Location, var timeZone: TimeZone) : ILocation, IDBObject {

    override var id: Long = 0

    override val uniqueTransientId: Long by lazy { System.nanoTime() }

    override var status: IDBObject.Status = IDBObject.Status.NEW

    override val isToDelete
        get() = IDBObject.isToDelete(status)

    override val isNew
        get() = IDBObject.isNew(status)

    override val isPersistent
        get() = IDBObject.isPersistent(status)

    var isAutomatic: Boolean = false
        private set

    val nameEx: String
        get() = if (isAutomatic) "$name*" else name

    override fun toString(): String {
        return "$name $location $timeZone"
    }

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
                text = "Horst & Daniel Zielske, Magical Realism, LUMAS",
                link = "https://www.lumas.de/pictures/horst_daniel_zielske/horst_daniel_zielske_magical_realism/",
                imageId = R.drawable.logo_magic_realism
            )

            name.contains("salamanca", true) -> Copyright(
                text = "© Copyright 1995-2020 EducationDynamics, LLC",
                link = "https://www.studyabroad.com/in-spain/salamanca",
                imageId = R.drawable.logo_sab
            )

            name.contains("sassnitz", true) -> Copyright(
                text = "Kreuzfahrer-Guide",
                link = "https://kreuzfahrerguide.de/sassnitz-hafen-tipps-ausfluege/",
                imageId = R.drawable.logo_kreuzfahrer_guide
            )

            name.contains("eichst", true) -> Copyright(
                text = "@Copyright 2020 Wikipedia",
                link = "https://en.wikipedia.org/wiki/Eichst%C3%A4tt",
                imageId = R.drawable.logo_eichstaett
            )

            name.contains("daressalaam", true) ||
            name.contains("monrovia", true) ||
            name.contains("lagos", ignoreCase = true) ||
            name.contains("lusaka", true) ||
            name.contains("kigali", true) -> Copyright(
                text = "© Copyright 2020 Stephan Hödtke",
                link = "bbc.co.uk",
                imageId = R.drawable.nyota
            )

            else -> Copyright(
                text = "Amyn Nasser, LUMAS",
                link = "https://www.lumas.de/pictures/amyn_nasser/amcdxxiii_the_little_house_that_fought_demolition/?gclid=EAIaIQobChMI4IzG3ICq7QIVteq7CB1b9QnMEAEYASADEgJSsfD_BwE\n",
                imageId = R.drawable.nyota
            )
        }
    }

    class Copyright(val text: String, val link: String, val imageId: Int)

    fun isNear(otherLocation: ILocation): Boolean {
        return getHorizontalDistanceInKmTo(otherLocation) < TEN_METERS_IN_KM
    }

    fun isNear(otherLocation: android.location.Location): Boolean {
        return getHorizontalDistanceInKmTo(otherLocation) < TEN_METERS_IN_KM
    }

    private fun getHorizontalDistanceInKmTo(otherLocation: ILocation): Double {
        return getHorizontalDistanceInKmTo(location.latitude, location.longitude, otherLocation.latitude, otherLocation.longitude)
    }

    private fun getHorizontalDistanceInKmTo(otherLocation: android.location.Location): Double {
        return getHorizontalDistanceInKmTo(location.latitude, location.longitude, otherLocation.latitude, otherLocation.longitude)
    }

    override val latitude: Double
        get() = location.latitude

    override val longitude: Double
        get() = location.longitude

    override val altitude: Double
        get() = location.altitude

    fun matches(otherCity: City): Boolean {
        return when {
            (name.compareTo(otherCity.name, ignoreCase = true) == 0) -> true
            (isAutomatic && otherCity.isAutomatic) -> true
            (isNear(otherCity.location)) -> true
            else -> false
        }
    }

    companion object {

        private const val ONE_METER_IN_KM = 0.001
        private const val TEN_METERS_IN_KM = 0.01
        internal const val BERLIN = "Berlin"
        internal const val BERLIN_LATITUDE = 52.6425
        internal const val BERLIN_LONGITUDE = 13.4925
        internal const val BERLIN_ALTITUDE = 0.1037 // in km

        internal fun createNewAutomaticCity(): City =
            createNewCity("You", true)

        internal fun createNewAutomaticCityFromAndroidLocation(location: android.location.Location): City =
            createNewCityFromAndroidLocation(location, "You", true)

        internal fun createNewCity(cityName: String = "Home", isAutomatic: Boolean = true): City =
            createNewCityFromLocation(Location(0.0, 0.0), cityName, isAutomatic)

        internal fun createNewCityFromAndroidLocation(location: android.location.Location, cityName: String = "Home", isAutomatic: Boolean = true): City =
            createNewCityFromLocation(Location.fromAndroidLocation(location), cityName, isAutomatic)

        private fun createNewCityFromLocation(location: Location, cityName: String, isAutomatic: Boolean): City =
            City(cityName, location, TimeZone.getDefault()).also { it.isAutomatic = isAutomatic }


        internal val berlinAsAndroidLocation: android.location.Location
            get() {
                return android.location.Location("Berlin").apply {
                    latitude = BERLIN_LATITUDE
                    longitude = BERLIN_LONGITUDE
                    altitude = BERLIN_ALTITUDE
                }
            }

        internal val berlinAsLocation: com.stho.nyota.sky.utilities.Location
            get() {
                return com.stho.nyota.sky.utilities.Location(BERLIN_LATITUDE, BERLIN_LONGITUDE, BERLIN_ALTITUDE)
            }

        fun create(id: Long, name: String, location: Location, timeZone: TimeZone, isAutomatic: Boolean): City {
            return City(name, location, timeZone).apply {
                this.isAutomatic = isAutomatic
                this.id = id
            }
        }

        fun create(location: Location, timeZone: TimeZone): City {
            return City("Anywhere", location, timeZone)
        }

        internal fun getLastKnownAndroidLocationFromLocationManager(locationManager: LocationManager): android.location.Location? {
            var location: android.location.Location? = null
            try {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            } catch (ex: SecurityException) {
                // ignore
            }
            return location
        }

        internal fun getHorizontalDistanceInKmTo(latitude: Double, longitude: Double, otherLatitude: Double, otherLongitude: Double): Double {
            val sinDeltaPhi = sin(Radian.fromDegrees(otherLatitude - latitude) / 2)
            val sinDeltaLambda = sin(Radian.fromDegrees(otherLongitude - longitude) / 2)
            val a = sinDeltaPhi * sinDeltaPhi + cos(Radian.fromDegrees(latitude)) * cos(Radian.fromDegrees(otherLatitude)) * sinDeltaLambda * sinDeltaLambda
            val c = 2 * atan2(sqrt(a), sqrt(1 - a))
            return Earth.RADIUS * c
        }

        internal val availableTimeZones: Array<String>
            get() = TimeZone.getAvailableIDs()
    }
}