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

// TODO: this class is too big...

class Repository private constructor() {

    val universe: Universe = Universe()
    val settings: Settings = Settings()

    private val citiesLiveData = MutableLiveData<Cities>().apply { value = Cities() }
    private val momentLiveData = MutableLiveData<Moment>().apply { value = Moment.forNow(defaultAutomaticCity) }
    private val currentAutomaticTimeLiveData = MutableLiveData<UTC>().apply { value = defaultAutomaticTime }
    private val currentAutomaticLocationLiveData = MutableLiveData<Location>().apply { value = defaultAutomaticLocation }
    private val currentOrientationLiveData = MutableLiveData<Orientation>().apply { value = Orientation.defaultOrientation }

    val momentLD: LiveData<Moment>
        get() = momentLiveData

    val currentAutomaticTimeLD: LiveData<UTC>
        get() = currentAutomaticTimeLiveData

    internal var currentAutomaticTime: UTC
        get() = currentAutomaticTimeLiveData.value ?: UTC.forNow()
        private set(value) {
            val currentValue = currentAutomaticTimeLiveData.value
            if (currentValue == null || currentValue.timeInMillis != value.timeInMillis) {
                currentAutomaticTimeLiveData.postValue(value)
            }
        }

    val currentAutomaticLocationLD: LiveData<Location>
        get() = currentAutomaticLocationLiveData

    internal var currentAutomaticLocation: Location
        get() = currentAutomaticLocationLiveData.value ?: City.defaultLocationBerlinBuch
        private set(value) {
            val currentLocation = currentAutomaticLocationLiveData.value
            if (currentLocation == null || !currentLocation.isNearTo(value)) {
                currentAutomaticLocationLiveData.postValue(value)
            }
        }

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

    val solarSystem: SolarSystem
        get() = universe.solarSystem

    private fun load(context: Context) {
        val helper = NyotaDatabaseHelper(context)
        val adapter = NyotaDatabaseAdapter(helper.writableDatabase)

        Cities().also {
            adapter.readCities(it)
            ensureDefaultAutomaticCity(it)
            citiesLiveData.value = it
        }

        adapter.readConstellations(universe.constellations)
        adapter.readStars(universe.stars)

        adapter.readSatellites(universe.satellites)

        universe.targets.also {
            adapter.readTargets(it)
            if (ensureDefaultTargets(it)) {
                adapter.saveTargets(it)
            }
        }

        settings.also {
            adapter.readSettings(it)
            ensureDefaultSettings(it)
        }

        UniverseInitializer(universe).initialize()

        applySettings()

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

    fun saveSettings(context: Context) {
        val helper = NyotaDatabaseHelper(context)
        val adapter = NyotaDatabaseAdapter(helper.writableDatabase)
        adapter.saveSettings(settings)
    }

    fun touchMoment() =
        momentLiveData.postValue(momentLiveData.value)

    private fun ensureDefaultAutomaticCity(cities: Cities) =
        cities.ensureDefaultAutomaticCity()

    private fun ensureDefaultSettings(settings: Settings): Boolean =
        // TODO: Clean
        when {
            settings.currentLocation.isEmpty() -> {
                settings.currentLocation = "Berlin"
                true
            }
            else -> false
        }

    private fun ensureDefaultTargets(targets: Targets): Boolean =
        when (targets.size) {
            0 -> {
                targets.add(Target.createDefaultNeowise())
                true
            }
            else -> false
        }

    internal fun updateOrientation(orientation: Orientation) {
        currentOrientationLiveData.postValue(orientation)
    }

    internal fun updateForNow() {
        updateMomentForNow()
        updateAutomaticMomentForNow()
    }

    internal fun updateForNow(location: Location) {
        updateMomentForNow(location)
        updateAutomaticMomentForNow(location)
    }

    private fun updateAutomaticMomentForNow() {
        currentAutomaticTime = UTC.forNow()
    }

    private fun updateAutomaticMomentForNow(location: Location) {
        currentAutomaticLocation = location
        currentAutomaticTime = UTC.forNow()
    }

    // TODO: decouple Moment and City: assign City -> Moment.Location, Moment.Name, ...

    private fun updateMomentForNow() {
        if (settings.updateTimeAutomatically) {
            val moment = liveMoment.forNow()
            momentLiveData.postValue(moment)
        }
    }

    // TODO: make it better with Moment = Moment(Location, UTC, cityName, useAutomaticLocation)

    private fun updateMomentForNow(location: Location) {
        var moment: Moment = liveMoment
        if (settings.updateLocationAutomatically && moment.city.isAutomatic) {
            moment.city.location = location
        }
        if (settings.updateTimeAutomatically) {
            moment = moment.forNow()
        }
        momentLiveData.postValue(moment)
    }

    fun addHours(hours: Double) {
        val moment: Moment = liveMoment.addHours(hours)
        momentLiveData.postValue(moment)
        updateTimeAutomatically = false
    }

    fun next(interval: Interval) {
        val moment = liveMoment.next(interval)
        momentLiveData.postValue(moment)
        updateTimeAutomatically = false
    }

    fun previous(interval: Interval) {
        val moment = liveMoment.previous(interval)
        momentLiveData.postValue(moment)
        updateTimeAutomatically = false
    }

    fun reset() {
        val moment = liveMoment.forNow()
        momentLiveData.postValue(moment)
    }

    fun setCity(city: City) {
        val moment = Moment(city, liveMoment.utc)
        momentLiveData.postValue(moment)
    }

    fun setDate(year: Int, month: Int, dayOfMonth: Int) {
        val moment = liveMoment.forThisDate(year, month, dayOfMonth)
        momentLiveData.postValue(moment)
        updateTimeAutomatically = false
    }

    fun setTime(hourOfDay: Int, minute: Int) {
        val moment = liveMoment.forThisTime(hourOfDay, minute)
        momentLiveData.postValue(moment)
        updateTimeAutomatically = false
    }

    fun setTime(utc: UTC) {
        val moment = liveMoment.forUTC(utc)
        momentLiveData.postValue(moment)
        updateTimeAutomatically = false
    }

    internal fun getSatelliteOrDefault(key: String?): Satellite =
        key?.let { universe.satellites.findSatelliteByKey(it) } ?: satellites.first()

    internal fun getPlanetOrDefault(key: String?): AbstractPlanet =
        key?.let { solarSystem.findPlanetByKey(it) } ?: solarSystem.venus

    internal fun getConstellationByKeyOrDefault(key: String?): Constellation =
        key?.let { universe.constellations.findConstellationByKey(it) } ?: universe.constellations[Constellation.Crux]

    internal fun getStarByKeyOrDefault(key: String?): Star =
        key?.let { universe.stars.findStarByKey(it) } ?: universe.vip.first()

    internal fun getElementByKeyOrDefault(key: String?): IElement =
        key?.let { universe.findElementByKey(it) } ?: solarSystem.moon

    internal fun getElementByKey(key: String?): IElement? =
        key?.let { universe.findElementByKey(it) }

    internal fun getCityOrNewCity(cityName: String?): City =
        liveCities.findCityByName(cityName) ?: City.createNewCity(cityName ?: City.NEW_CITY)

    internal fun createDefaultCities(context: Context) {
        val helper = NyotaDatabaseHelper(context)
        val adapter = NyotaDatabaseAdapter(helper.writableDatabase)

        liveCities.also {
            it.createDefaultCities()
            adapter.saveCities(it)
            citiesLiveData.postValue(it)
        }
    }

    internal fun updateCityDistances() =
        liveCities.updateDistances(referenceLocation = currentAutomaticLocation)

    private fun applySettings() {
        val city =  citiesLiveData.value!!.findCityByName(settings.currentLocation)
        if (city != null) {
            setCity(city)
        }
        // TODO: clean
        //        updateTimeAutomatically = settings.updateTimeAutomatically
        //        updateLocationAutomatically = settings.updateLocationAutomatically
        //        updateOrientationAutomatically = settings.updateOrientationAutomatically
    }

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
        city.markAsDeleted()
        saveCity(context, city)
        liveCities.also {
            it.delete(city)
            citiesLiveData.postValue(it)
        }
    }

    fun undoDeleteCity(context: Context, position: Int, city: City) {
        city.markAsNew()
        saveCity(context, city)
        liveCities.also {
            it.undoDelete(position, city)
            citiesLiveData.postValue(it)
        }
    }

    fun updateCity(context: Context, city: City) {
        saveCity(context, city)
        liveCities.also {
            it.add(city, overwrite = true)
            citiesLiveData.postValue(it)
        }
    }

    var updateOrientationAutomatically: Boolean
        get() = settings.updateOrientationAutomatically
        private set(value) {
            settings.updateOrientationAutomatically = value
        }

    var updateTimeAutomatically: Boolean
        get() = settings.updateTimeAutomatically
        private set(value) {
            settings.updateTimeAutomatically = value
        }

    var updateLocationAutomatically: Boolean
        get() = settings.updateLocationAutomatically
        private set(value) {
            settings.updateLocationAutomatically = value
        }

    fun toggleAutomaticTime() {
        updateTimeAutomatically = !updateTimeAutomatically
    }

    fun toggleAutomaticLocation() {
        updateLocationAutomatically = !updateLocationAutomatically
    }

    private val defaultAutomaticTime: UTC
        get() = UTC.forNow()

    private val defaultAutomaticLocation: Location
        get() = City.defaultLocationBerlinBuch

    private val defaultAutomaticCity: City
        get() = Cities.automaticCity

    private val liveMoment: Moment
        get() = momentLiveData.value!!

    private val liveCities: Cities
        get() = citiesLiveData.value!!
}




