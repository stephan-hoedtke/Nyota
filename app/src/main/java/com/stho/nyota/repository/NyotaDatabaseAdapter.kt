package com.stho.nyota.repository

import android.database.sqlite.SQLiteDatabase
import com.stho.nyota.settings.Settings
import com.stho.nyota.sky.universe.Satellite
import com.stho.nyota.sky.universe.Target
import com.stho.nyota.sky.utilities.City


// see:
// https://developer.android.com/training/data-storage/sqlite
class NyotaDatabaseAdapter(private val db: SQLiteDatabase) {

    fun saveSettings(settings: Settings) {
        val contract = SettingsContract(db)
        contract.write("UpdateLocationAutomatically", settings.updateLocationAutomatically)
        contract.write("UpdateTimeAutomatically", settings.updateTimeAutomatically)
        contract.write("CurrentLocation", settings.currentLocation)
        contract.write("DisplayNames", settings.displayNames)
        contract.write("DisplaySymbols", settings.displaySymbols)
        contract.write("DisplayMagnitude", settings.displayMagnitude)
    }

    fun readSettings(settings: Settings) {
        val contract = SettingsContract(db)
        settings.updateLocationAutomatically = contract.readBoolean("UpdateLocationAutomatically", settings.updateLocationAutomatically)
        settings.updateTimeAutomatically = contract.readBoolean("UpdateTimeAutomatically", settings.updateTimeAutomatically)
        settings.currentLocation = contract.readStringOrDefault("CurrentLocation", settings.currentLocation)
        settings.displayNames = contract.readBoolean("DisplayNames", settings.displayNames)
        settings.displaySymbols = contract.readBoolean("DisplaySymbols", settings.displaySymbols)
        settings.displayMagnitude = contract.readBoolean("DisplayMagnitude", settings.displayMagnitude)

    }

    fun saveCities(cities: Collection<City>) {
        val contract = CitiesContract(db)
        cities.forEach { contract.write(it) }
    }

    fun saveCity(city: City) {
        val contract = CitiesContract(db)
        contract.write(city)
    }

    fun readLocations(): Collection<City> {
        val contract = CitiesContract(db)
        return contract.read()
    }

    fun saveSatellites(satellites: Collection<Satellite>) {
        val contract = SatellitesContract(db)
        satellites.forEach { contract.write(it) }
    }

    fun saveSatellite(satellite: Satellite) {
        val contract = SatellitesContract(db)
        contract.write(satellite)
    }

    fun readSatellites(): Collection<Satellite> {
        val contract = SatellitesContract(db)
        return contract.read()
    }

    fun saveTargets(targets: Collection<Target>) {
        val contract = TargetContract(db)
        targets.forEach { contract.write(it) }
    }

    fun saveTarget(target: Target) {
        val contract = TargetContract(db)
        contract.write(target)
    }

    fun readTargets(): Collection<Target> {
        val contract = TargetContract(db)
        return contract.read()
    }
}


