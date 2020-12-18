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
// TODO: remove the lock (its nonsense!)

class Repository private constructor() {

    var universe: Universe = Universe()

    private val settingsLiveData = MutableLiveData<Settings>()
    private val citiesLiveData = MutableLiveData<Cities>()
    private val momentLiveData = MutableLiveData<Moment>()
    private val currentAutomaticTimeLiveData = MutableLiveData<UTC>()
    private val currentAutomaticLocationLiveData = MutableLiveData<Location>()
    private val currentOrientationLiveData = MutableLiveData<Orientation>()

    init {
        settingsLiveData.value = Settings()
        citiesLiveData.value = Cities()
        momentLiveData.value = Moment.forNow(defaultCity)
        currentAutomaticTimeLiveData.value = defaultAutomaticTime
        currentAutomaticLocationLiveData.value = defaultAutomaticLocation
        currentOrientationLiveData.value = Orientation.defaultOrientation
    }

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

    private fun ensureDefaultCities(cities: Cities): Boolean =
        when (cities.size) {
            0 -> {
                cities.createDefaultCities(true)
                true
            }
            else -> false
        }

    private fun ensureDefaultSettings(settings: Settings): Boolean =
        when {
            settings.currentLocation.isNullOrEmpty() -> {
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

    internal fun updateForNow(location: Location) {
        updateMomentForNow(location)
        updateAutomaticMomentForNow(location)
    }

    private fun updateAutomaticMomentForNow(location: Location) {
        currentAutomaticLocation = location
        currentAutomaticTime = UTC.forNow()
    }

    // TODO: decouple Moment and City: assign City -> Moment.Location, Moment.Name, ...

    private fun updateMomentForNow(location: Location?) {
        var moment: Moment = liveMoment
        if (settings.updateLocationAutomatically && moment.city.isAutomatic && location != null) {
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
        liveCities.findCityByName(cityName) ?: City.createNewCity(cityName ?: City.NEW_CITY)

    internal fun createDefaultCities(context: Context) {
        val helper = NyotaDatabaseHelper(context)
        val adapter = NyotaDatabaseAdapter(helper.writableDatabase)

        liveCities.also {
            it.createDefaultCities(false)
            adapter.saveCities(it.values)
            citiesLiveData.postValue(it)
        }
    }

    internal fun updateCityDistances() =
        liveCities.updateDistances(referenceLocation = currentAutomaticLocation)

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
        liveCities.also {
            city.markAsDeleted()
            it.delete(city)
            saveCity(context, city)
            citiesLiveData.postValue(it)
        }
    }

    fun undoDeleteCity(context: Context, position: Int, city: City) {
        liveCities.also {
            city.markAsNew()
            it.undoDelete(position, city)
            saveCity(context, city)
            citiesLiveData.postValue(it)
        }
    }

    fun updateCity(context: Context, city: City) {
        liveCities.also {
            it.addOrUpdate(city)
            saveCity(context, city)
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
            if (value) {
                ensureAutomaticCity()
            }
        }

    private fun ensureAutomaticCity() {
        if (!liveMoment.city.isAutomatic) {
            momentLiveData.postValue(Moment.forNow(defaultAutomaticCity))
        }
    }

    fun toggleAutomaticTime() {
        updateTimeAutomatically = !updateTimeAutomatically
    }

    fun toggleAutomaticLocation() {
        updateLocationAutomatically = !updateLocationAutomatically
    }

    fun disableAutomaticLocation() {
        updateLocationAutomatically = false
    }

    private val defaultCity: City
        get() = citiesLiveData.value!!.defaultCity

    private val defaultAutomaticCity: City
        get() = citiesLiveData.value!!.defaultAutomaticCity

    private val defaultAutomaticTime: UTC
        get() = UTC.forNow()

    private val defaultAutomaticLocation: Location
        get() = City.defaultLocationBerlinBuch

    private val liveMoment: Moment
        get() = momentLiveData.value!!

    private val liveCities: Cities
        get() = citiesLiveData.value!!
}




