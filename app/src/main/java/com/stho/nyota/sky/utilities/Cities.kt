package com.stho.nyota.sky.utilities

import android.location.LocationManager
import java.util.*

class Cities {

    private val map: HashMap<String, City> = HashMap<String, City>()
    private val array: ArrayList<City> = ArrayList<City>()

    val size: Int
        get() = map.size

    var selectedCity: City? = null
        private set

    fun addAll(collection: Collection<City>, overwrite: Boolean = false) {
        for (city in collection) {
            add(city, overwrite)
        }
    }

    fun add(city: City) =
        add(city, false)

    fun addOrUpdate(city: City) =
        add(city, true)

    private fun add(city: City, overwrite: Boolean): Boolean {
        val cityName = city.name
        val existingCity = map[cityName]
        return when {
            existingCity == null -> {
                array.add(city)
                map[cityName] = city
                true
            }
            overwrite -> {
                val index = array.indexOf(existingCity)
                array[index] = city
                map[cityName] = city
                true
            }
            else -> {
                false
            }
        }
    }

    operator fun get(index: Int): City =
        array[index]

    operator fun get(cityName: String): City? =
        map[cityName]

    fun exists(cityName: String?): Boolean =
        if (cityName != null)
            map.containsKey(cityName)
        else
            false

    fun findCityByIndex(index: Int): City? =
        if (0 <= index && index < array.size)
            array[index]
        else
            null

    fun findCityById(id: Long): City? =
        map.values.firstOrNull { it.id == id }

    fun findCityByName(cityName: String?): City? =
        if (cityName != null)
            map[cityName]
        else
            null

    fun indexOf(city: City): Int =
        array.indexOf(city)

    fun findMatchingCity(cityToFind: City): City? =
        map.values.firstOrNull { it.matches(cityToFind) }

    fun createDefaultCities(overwrite: Boolean) {
        val defaultCities = City.createCities()
        addAll(defaultCities, overwrite)
    }

    private fun getCityForCurrentLocation(locationManager: LocationManager): City =
        defaultAutomaticCity.also {
            val location = City.getLastKnownAndroidLocationFromLocationManager(locationManager)
            if (location != null) {
                it.location
            }
        }

    private fun selectCityByName(cityName: String) {
        selectedCity = findCityByName(cityName)
    }

    fun isSelected(city: City): Boolean =
        city == selectedCity

    fun first(): City =
        array.first()

    val values: List<City>
        get() = array

    fun delete(city: City) {
        if (map.remove(city.name, city)) {
            array.remove(city)
        }
    }

    fun undoDelete(position: Int, city: City) {
        val cityName = city.name
        map[cityName] = city
        array.add(position, city)
    }

    val defaultCity: City =
        findCityByName(City.BERLIN) ?: array.firstOrNull() ?: City.createDefaultBerlinBuch().also { add(it) }

    val defaultAutomaticCity: City =
        array.firstOrNull { city -> city.isAutomatic } ?: City.createNewAutomaticCity().also { add(it) }

    fun updateDistances(referenceLocation: ILocation) {
        for (city in values) {
            city.updateDistance(referenceLocation)
        }
    }
}
