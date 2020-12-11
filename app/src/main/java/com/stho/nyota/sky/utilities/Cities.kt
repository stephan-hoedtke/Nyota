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

    fun addAll(collection: Collection<City>) {
        for (city in collection) {
            add(city)
        }
    }

    fun add(city: City) {
        val cityName = city.name
        val existingCity = map[cityName]
        if (existingCity != null) {
            val index = array.indexOf(existingCity)
            array[index] = city
            map[cityName] = city
        } else {
            array.add(city)
            map[cityName] = city
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

    fun delete(city: City): Boolean {
        if (map.remove(city.name, city)) {
            array.remove(city)
            return true
        }
        return false
    }

    fun undoDelete(position: Int, city: City) {
        val cityName = city.name
        map[cityName] = city
        array.add(position, city)
    }

    val defaultCity: City =
        findCityByName(City.BERLIN) ?: array.firstOrNull() ?: City.createDefaultBerlin().also { add(it) }

    val defaultAutomaticCity: City =
        array.firstOrNull { city -> city.isAutomatic } ?: City.createNewAutomaticCity().also { add(it) }
}