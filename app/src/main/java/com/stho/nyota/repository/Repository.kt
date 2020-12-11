package com.stho.nyota.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.stho.nyota.Interval
import com.stho.nyota.settings.Settings
import com.stho.nyota.sky.universe.*
import com.stho.nyota.sky.universe.Target
import com.stho.nyota.sky.utilities.*
import java.util.*
import java.util.concurrent.Semaphore

// TODO: this class is too big...

class Repository private constructor() {

    private val lock = Semaphore(1)

    var universe: Universe = Universe()

    private val settingsLiveData = MutableLiveData<Settings>()
    private val citiesLiveData = MutableLiveData<Cities>()
    private val momentLiveData = MutableLiveData<Moment>()
    private val currentAutomaticMomentLiveData = MutableLiveData<Moment>()
    private val currentOrientationLiveData = MutableLiveData<Orientation>()

    init {
        settingsLiveData.value = Settings()
        citiesLiveData.value = Cities()
        momentLiveData.value = Moment.forNow(defaultCity)
        currentAutomaticMomentLiveData.value = defaultAutomaticMoment
        currentOrientationLiveData.value = Orientation.defaultOrientation
    }

    val momentLD: LiveData<Moment>
        get() = momentLiveData

    val currentAutomaticMomentLD: LiveData<Moment>
        get() = currentAutomaticMomentLiveData

    val currentAutomaticLocation: Location
        get() = currentAutomaticMomentLiveData.value!!.city.location

    val currentOrientationLD: LiveData<Orientation>
        get() = currentOrientationLiveData

    val currentOrientation: Orientation
        get() = currentOrientationLiveData.value ?: Orientation.defaultOrientation

    val citiesLD: LiveData<Cities>
        get() = citiesLiveData

    val universeLD: LiveData<Universe>
        get() = Transformations.map(momentLiveData) { moment ->
            universe.updateFor(moment, true)
        }

    val moonLD: LiveData<Moon>
        get() = Transformations.map(momentLiveData) { moment ->
            universe.updateFor(moment, true).solarSystem.moon.also { it.calculateNewFullMoon(moment.utc) }
        }

    val cities: List<City>
        get() = citiesLiveData.value!!.values

    val satellites: List<Satellite>
        get() = universe.satellites.values

    val targets: List<Target>
        get() = universe.targets.values

    val settings: Settings
        get() = settingsLiveData.value!!

    val solarSystem: SolarSystem
        get() = universe.solarSystem

    private fun load(context: Context) {
        val helper = NyotaDatabaseHelper(context)
        val adapter = NyotaDatabaseAdapter(helper.writableDatabase)

        Cities().also {
            it.addAll(adapter.readLocations())
            if (ensureDefaultCities(it)) {
                adapter.saveCities(it.values)
            }
            citiesLiveData.postValue(it)
        }

        universe.satellites.also {
            it.addAll(adapter.readSatellites())
        }

        universe.targets.also {
            it.addAll(adapter.readTargets())
            if (ensureDefaultTargets(it)) {
                adapter.saveTargets(it.values)
            }
        }

        Settings().also {
            adapter.readSettings(it)
            if (ensureDefaultSettings(it)) {
                adapter.saveSettings(it)
            }
            settingsLiveData.postValue(it)
        }
    }

    fun saveCity(context: Context, city: City) {
        val helper = NyotaDatabaseHelper(context)
        val adapter = NyotaDatabaseAdapter(helper.writableDatabase)
        adapter.saveCity(city)
    }

    fun saveSatellite(context: Context, satellite: Satellite) {
        val helper = NyotaDatabaseHelper(context)
        val adapter = NyotaDatabaseAdapter(helper.writableDatabase)
        adapter.saveSatellite(satellite)
        touchMoment()
    }

    fun touchMoment() =
        momentLiveData.postValue(momentLiveData.value)

    private fun ensureDefaultCities(cities: Cities): Boolean {
        if (cities.size == 0) {
            cities.addAll(City.createCities())
            return true
        }
        return false
    }

    private fun ensureDefaultSettings(settings: Settings): Boolean {
        if (settings.currentLocation.isNullOrEmpty()) {
            settings.currentLocation = "Berlin"
            return true
        }
        return false
    }

    private fun ensureDefaultTargets(targets: Targets): Boolean {
        if (targets.size == 0) {
            targets.add(Target.createDefaultNeowise())
            return true
        }
        return false
    }

    internal fun updateOrientation(orientation: Orientation) {
        currentOrientationLiveData.postValue(orientation)
    }

    internal fun updateForNow(location: Location?) {
        updateMomentForNow(location)
        updateAutomaticMomentForNow(location)
    }

    private fun updateMomentForNow(location: Location?) {
        try {
            lock.acquire()
            updateMomentForNowSynchronized(location)
         }
        finally {
            lock.release()
        }
    }

    internal fun addHours(hours: Double) {
        try {
            lock.acquire()
            addHoursSynchronized(hours)
        }
        finally {
            lock.release()
        }
    }

    internal fun next(interval: Interval) {
        try {
            lock.acquire()
            nextSynchronized(interval)
         }
        finally {
            lock.release()
        }
    }

    internal fun previous(interval: Interval) {
        try {
            lock.acquire()
            previousSynchronized(interval)
        }
        finally {
            lock.release()
        }
    }

    internal fun reset() {
        try {
            lock.acquire()
            resetSynchronized()
        } finally {
            lock.release()
        }
    }

    internal fun setCity(city: City) {
        try {
            lock.acquire()
            setCitySynchronized(city)
        }
        finally {
            lock.release()
        }
    }

    internal fun setDate(year: Int, month: Int, dayOfMonth: Int) {
        try {
            lock.acquire()
            setDateSynchronized(year, month, dayOfMonth)
        }
        finally {
            lock.release()
        }
    }

    internal fun setTime(hourOfDay: Int, minute: Int) {
        try {
            lock.acquire()
            setTimeSynchronized(hourOfDay, minute)
        }
        finally {
            lock.release()
        }
    }

    internal fun setTime(utc: UTC) {
        try {
            lock.acquire()
            setTimeSynchronized(utc)
        }
        finally {
            lock.release()
        }
    }


    private fun updateAutomaticMomentForNow(location: Location?) {
        var auto = currentAutomaticMomentLiveData.value!!
        if (location != null) {
            auto.city.location = location
        }
        auto.city.timeZone = TimeZone.getDefault()
        auto = auto.forNow()
        currentAutomaticMomentLiveData.postValue(auto)
    }

    private fun updateMomentForNowSynchronized(location: Location?) {
        var moment: Moment = momentLiveData.value!!
        if (settings.updateLocationAutomatically && moment.city.isAutomatic && location != null) {
            moment.city.location = location
        }
        if (settings.updateTimeAutomatically) {
            moment = moment.forNow()
        }
        momentLiveData.postValue(moment);
    }

    private fun addHoursSynchronized(hours: Double) {
        val moment: Moment = momentLiveData.value!!.addHours(hours)
        momentLiveData.postValue(moment);
    }

    private fun nextSynchronized(interval: Interval) {
        val moment = momentLiveData.value!!.next(interval)
        momentLiveData.postValue(moment);
    }

    private fun previousSynchronized(interval: Interval) {
        val moment = momentLiveData.value!!.previous(interval)
        momentLiveData.postValue(moment)
    }

    private fun resetSynchronized() {
        val moment = momentLiveData.value!!.forNow()
        momentLiveData.postValue(moment)
    }

    private fun setCitySynchronized(city: City) {
        val moment = Moment(city, momentLiveData.value!!.utc)
        momentLiveData.postValue(moment)
    }

    private fun setDateSynchronized(year: Int, month: Int, dayOfMonth: Int) {
        val moment = momentLiveData.value!!.forThisDate(year, month, dayOfMonth)
        momentLiveData.postValue(moment)
    }

    private fun setTimeSynchronized(hourOfDay: Int, minute: Int) {
        val moment = momentLiveData.value!!.forThisTime(hourOfDay, minute)
        momentLiveData.postValue(moment)
    }

    private fun setTimeSynchronized(utc: UTC) {
        val moment = momentLiveData.value!!.forUTC(utc)
        momentLiveData.postValue(moment)
    }

    internal fun getSatelliteOrDefault(satelliteName: String?): Satellite =
        universe.satellites.findSatelliteByName(satelliteName) ?: satellites.first()

    internal fun getPlanetOrDefault(planetName: String?): AbstractPlanet =
        universe.findPlanetByName(planetName) ?: solarSystem.planets.first() as AbstractPlanet

    internal fun getConstellationOrDefault(constellationName: String?): Constellation =
        universe.findConstellationByName(constellationName) ?: universe.constellations.first()

    internal fun getStarOrDefault(starName: String?): Star =
        universe.findStarByName(starName) ?: universe.vip.first()

    internal fun getElementOrDefault(elementName: String?): IElement =
        universe.findElementByName(elementName) ?: solarSystem.moon

    internal fun getCityOrNewCity(cityName: String?): City =
        citiesLiveData.value?.findCityByName(cityName) ?: City.createNewCity()

    companion object {
        private var singleton: Repository? = null
        private var lockObject: Any = Any()

        /**
         * Returns the singleton-instance of the repository, if it was created already, or
         * - creates a new repository instance and
         * - loads the data from the database using the provided context
         * otherwise
         * @return the singleton-instance of the repository
         */
        fun requireRepository(context: Context): Repository {

            if (singleton != null) {
                return singleton!!
            }

            synchronized(lockObject) {
                if (singleton == null) {
                    singleton = Repository()
                    singleton!!.load(context)

                }
                return singleton!!
            }
        }
    }

    fun deleteCity(context: Context, city: City) {
        citiesLiveData.value?.also {
            city.markAsDeleted()
            it.delete(city)
            saveCity(context, city)
            citiesLiveData.postValue(it)
        }
    }

    fun undoDeleteCity(context: Context, position: Int, city: City) {
        citiesLiveData.value?.also {
            city.markAsNew()
            it.undoDelete(position, city)
            saveCity(context, city)
            citiesLiveData.postValue(it)
        }
    }

    fun updateCity(context: Context, city: City) {
        citiesLiveData.value?.also {
            it.add(city)
            saveCity(context, city)
            citiesLiveData.postValue(it)
        }
    }

    private val defaultCity: City
        get() = citiesLiveData.value!!.defaultCity

    private val defaultAutomaticCity: City
        get() = citiesLiveData.value!!.defaultAutomaticCity

    private val defaultAutomaticMoment: Moment
        get() = Moment.forNow(defaultAutomaticCity)

}




