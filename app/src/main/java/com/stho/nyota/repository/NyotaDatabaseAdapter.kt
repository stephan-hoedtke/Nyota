package com.stho.nyota.repository

import android.database.sqlite.SQLiteDatabase
import com.stho.nyota.settings.Settings
import com.stho.nyota.sky.universe.Satellite
import com.stho.nyota.sky.universe.Satellites
import com.stho.nyota.sky.universe.Target
import com.stho.nyota.sky.universe.Targets
import com.stho.nyota.sky.utilities.Cities
import com.stho.nyota.sky.utilities.City


// see:
// https://developer.android.com/training/data-storage/sqlite
class NyotaDatabaseAdapter(private val db: SQLiteDatabase) {

    fun saveSettings(settings: Settings) {
        val contract = SettingsContract(db)
        contract.write(UpdateOrientationAutomatically, settings.updateOrientationAutomatically)
        contract.write(UpdateLocationAutomatically, settings.updateLocationAutomatically)
        contract.write(UpdateTimeAutomatically, settings.updateTimeAutomatically)
        contract.write(CurrentLocation, settings.currentLocation)
        contract.write(DisplayNames, settings.displayNames)
        contract.write(DisplaySymbols, settings.displaySymbols)
        contract.write(DisplayMagnitude, settings.displayMagnitude)
    }

    fun readSettings(settings: Settings) {
        val contract = SettingsContract(db)
        settings.updateOrientationAutomatically = contract.readBoolean(UpdateOrientationAutomatically, settings.updateOrientationAutomatically)
        settings.updateLocationAutomatically = contract.readBoolean(UpdateLocationAutomatically, settings.updateLocationAutomatically)
        settings.updateTimeAutomatically = contract.readBoolean(UpdateTimeAutomatically, settings.updateTimeAutomatically)
        settings.currentLocation = contract.readStringOrDefault(CurrentLocation, settings.currentLocation)
        settings.displayNames = contract.readBoolean(DisplayNames, settings.displayNames)
        settings.displaySymbols = contract.readBoolean(DisplaySymbols, settings.displaySymbols)
        settings.displayMagnitude = contract.readBoolean(DisplayMagnitude, settings.displayMagnitude)
    }

    companion object {
        private const val UpdateOrientationAutomatically = "UpdateOrientationAutomatically"
        private const val UpdateLocationAutomatically = "UpdateLocationAutomatically"
        private const val UpdateTimeAutomatically = "UpdateTimeAutomatically"
        private const val CurrentLocation = "CurrentLocation"
        private const val DisplayNames = "DisplayNames"
        private const val DisplaySymbols = "DisplaySymbols"
        private const val DisplayMagnitude = "DisplayMagnitude"
    }

    fun saveCities(cities: Cities) {
        val contract = CitiesContract(db)
        for (city in cities.values) {
            contract.write(city)
        }
    }

    fun saveCity(city: City) =
        CitiesContract(db).write(city)

    fun readCities(cities: Cities) =
        CitiesContract(db).read(cities)

    fun saveSatellites(satellites: Satellites) {
        val contract = SatellitesContract(db)
        for (satellite in satellites.values) {
            contract.write(satellite)
        }
    }

    fun saveSatellite(satellite: Satellite) =
        SatellitesContract(db).write(satellite)

    fun readSatellites(satellites: Satellites) =
        SatellitesContract(db).read(satellites)

    fun saveTargets(targets: Targets) {
        val contract = TargetContract(db)
        for (target in targets.values) {
            contract.write(target)
        }
    }

    fun saveTarget(target: Target) =
        TargetContract(db).write(target)

    fun readTargets(targets: Targets) =
        TargetContract(db).read(targets)
}


