package com.stho.nyota.repository

import android.database.sqlite.SQLiteDatabase
import com.stho.nyota.repository.contracts.*
import com.stho.nyota.settings.Settings
import com.stho.nyota.sky.universe.*
import com.stho.nyota.sky.universe.Target
import com.stho.nyota.sky.utilities.Cities
import com.stho.nyota.sky.utilities.City
import com.stho.nyota.sky.utilities.LiveMode
import com.stho.nyota.sky.utilities.projections.Projection


// see:
// https://developer.android.com/training/data-storage/sqlite
class NyotaDatabaseAdapter(private val db: SQLiteDatabase) {

    fun readConstellations(constellations: Constellations) =
        ConstellationsContract(db).read(constellations)

    fun readStars(stars: Stars) =
        StarsContract(db).read(stars)

    fun readCities(cities: Cities) =
        CitiesContract(db).read(cities)

    fun saveCities(cities: Cities) {
        val contract = CitiesContract(db)
        for (city in cities.values) {
            contract.write(city)
        }
    }

    fun saveCity(city: City) =
        CitiesContract(db).write(city)


    fun readSatellites(satellites: Satellites) =
        SatellitesContract(db).read(satellites)

    fun saveSatellites(satellites: Satellites) {
        val contract = SatellitesContract(db)
        for (satellite in satellites.values) {
            contract.write(satellite)
        }
    }

    fun saveSatellite(satellite: Satellite) =
        SatellitesContract(db).write(satellite)

    fun readTargets(targets: Targets) =
        TargetContract(db).read(targets)

    fun saveTargets(targets: Targets) {
        val contract = TargetContract(db)
        for (target in targets.values) {
            contract.write(target)
        }
    }

    fun saveTarget(target: Target) =
        TargetContract(db).write(target)

    fun readSettings(settings: Settings) {
        val contract = SettingsContract(db)
        settings.updateOrientationAutomatically = contract.readBoolean(UpdateOrientationAutomaticallyKey, settings.updateOrientationAutomatically)
        settings.updateLocationAutomatically = contract.readBoolean(UpdateLocationAutomaticallyKey, settings.updateLocationAutomatically)
        settings.updateTimeAutomatically = contract.readBoolean(UpdateTimeAutomaticallyKey, settings.updateTimeAutomatically)
        settings.currentLocation = contract.readString(CurrentLocationKey, settings.currentLocation)
        settings.displaySymbols = contract.readBoolean(DisplaySymbolsKey, settings.displaySymbols)
        settings.displayConstellations = contract.readBoolean(DisplayConstellationsKey, settings.displayConstellations)
        settings.displayConstellationNames = contract.readBoolean(DisplayConstellationNamesKey, settings.displayConstellationNames)
        settings.displayPlanetNames = contract.readBoolean(DisplayPlanetNamesKey, settings.displayPlanetNames)
        settings.displayStarNames = contract.readBoolean(DisplayStarNamesKey, settings.displayStarNames)
        settings.displayTargets = contract.readBoolean(DisplayTargetsKey, settings.displayTargets)
        settings.displaySatellites = contract.readBoolean(DisplaySatellitesKey, settings.displaySatellites)
        settings.displayGrid = contract.readBoolean(DisplayGridKey, settings.displayGrid)
        settings.displayEcliptic = contract.readBoolean(DisplayEclipticKey, settings.displayEcliptic)
        settings.displayHints = contract.readBoolean(DisplayHintsKey, settings.displayHints)
        settings.sphereProjection = Projection.deserialize(contract.readString(SphereProjectionKey, settings.sphereProjection.serialize()))
        settings.magnitude = contract.readDouble(MagnitudeKey, settings.magnitude)
        settings.radius = contract.readDouble(RadiusKey, settings.radius)
        settings.lambda = contract.readDouble(LambdaKey, settings.lambda)
        settings.gamma = contract.readDouble(GammaKey, settings.gamma)
        settings.liveMode = LiveMode.deserialize(contract.readString(LiveModeKey, settings.liveMode.serialize()))
        settings.isDirty = false
    }

    fun saveSettings(settings: Settings) {
        val contract = SettingsContract(db)
        contract.write(UpdateOrientationAutomaticallyKey, settings.updateOrientationAutomatically)
        contract.write(UpdateLocationAutomaticallyKey, settings.updateLocationAutomatically)
        contract.write(UpdateTimeAutomaticallyKey, settings.updateTimeAutomatically)
        contract.write(CurrentLocationKey, settings.currentLocation)
        contract.write(DisplaySymbolsKey, settings.displaySymbols)
        contract.write(DisplayConstellationsKey, settings.displayConstellations)
        contract.write(DisplayConstellationNamesKey, settings.displayConstellationNames)
        contract.write(DisplayPlanetNamesKey, settings.displayPlanetNames)
        contract.write(DisplayStarNamesKey, settings.displayStarNames)
        contract.write(DisplayTargetsKey, settings.displayTargets)
        contract.write(DisplaySatellitesKey, settings.displaySatellites)
        contract.write(DisplayGridKey, settings.displayGrid)
        contract.write(DisplayEclipticKey, settings.displayEcliptic)
        contract.write(DisplayHintsKey, settings.displayHints)
        contract.write(SphereProjectionKey, settings.sphereProjection.serialize())
        contract.write(MagnitudeKey, settings.magnitude)
        contract.write(RadiusKey, settings.radius)
        contract.write(LambdaKey, settings.lambda)
        contract.write(GammaKey, settings.gamma)
        contract.write(LiveModeKey, settings.liveMode.serialize())
        settings.isDirty = false
    }

    companion object {
        private const val UpdateOrientationAutomaticallyKey = "UpdateOrientationAutomatically"
        private const val UpdateLocationAutomaticallyKey = "UpdateLocationAutomatically"
        private const val UpdateTimeAutomaticallyKey = "UpdateTimeAutomatically"
        private const val CurrentLocationKey = "CurrentLocation"
        private const val DisplaySymbolsKey = "DisplaySymbols"
        private const val DisplayConstellationsKey = "DisplayConstellations"
        private const val DisplayConstellationNamesKey = "DisplayConstellationNames"
        private const val DisplayPlanetNamesKey = "DisplayPlanetNames"
        private const val DisplayStarNamesKey = "DisplayStarNames"
        private const val DisplayTargetsKey = "DisplayTargets"
        private const val DisplaySatellitesKey = "DisplaySatellites"
        private const val DisplayGridKey = "DisplayGrid"
        private const val DisplayEclipticKey = "DisplayEcliptic"
        private const val DisplayHintsKey = "DisplayHints"
        private const val SphereProjectionKey = "SphereProjection"
        private const val MagnitudeKey = "Magnitude"
        private const val RadiusKey = "Radius"
        private const val LambdaKey = "Lambda"
        private const val GammaKey = "Gamma"
        private const val LiveModeKey = "LiveMode"
    }

}


