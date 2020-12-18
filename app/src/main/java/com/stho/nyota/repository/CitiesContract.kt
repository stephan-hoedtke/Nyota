package com.stho.nyota.repository

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.stho.nyota.sky.utilities.Cities
import com.stho.nyota.sky.utilities.City
import com.stho.nyota.sky.utilities.Location
import java.util.*
import kotlin.collections.ArrayList

internal class CitiesContract(private val db: SQLiteDatabase) : BaseContract() {
    fun createTable() {
        db.execSQL(SQL_CREATE_TABLE)
    }

    fun dropTable() {
        db.execSQL(SQL_DROP_TABLE)
    }

    fun write(city: City) {
        when {
            city.isPersistent -> update(city.id, getContentValues(city))
            city.isToDelete -> delete(city.id)
            city.isNew -> city.id = create(getContentValues(city))
        }
    }

    fun read(): Collection<City> {
        val cities = ArrayList<City>()
        val cursor = db.rawQuery(SQL_QUERY_ALL, null)
        while (cursor.moveToNext()) {
            val id = getLong(cursor, 0)
            val name = getString(cursor, 1)
            val longitude = getDouble(cursor, 2)
            val latitude = getDouble(cursor, 3)
            val altitude = getDouble(cursor, 4)
            val timeZone: TimeZone = getTimeZone(cursor, 5)
            val isAutomatic: Boolean = getBoolean(cursor, 6)
            val location = Location(latitude = latitude, longitude = longitude, altitude = altitude)
            val city = City.create(id, name, location, timeZone, isAutomatic)
            cities.add(city)
        }
        cursor.close()
        return cities
    }

    private fun delete(id: Long) {
        val whereClause = "$COLUMN_ROWID = ?"
        db.delete(TABLE_NAME, whereClause, getQueryArgs(id))
    }

    private fun update(id: Long, values: ContentValues) {
        val whereClause = "$COLUMN_ROWID = ?"
        db.update(TABLE_NAME, values, whereClause, getQueryArgs(id))
    }

    private fun create(values: ContentValues): Long {
        return db.insert(TABLE_NAME, null, values)
    }

    private fun getContentValues(city: City): ContentValues {
        val values = ContentValues()
        values.put(COLUMN_NAME, city.name)
        values.put(COLUMN_LONGITUDE, city.longitude)
        values.put(COLUMN_LATITUDE, city.latitude)
        values.put(COLUMN_ALTITUDE, city.altitude)
        values.put(COLUMN_TIMEZONE, city.timeZone.displayName)
        values.put(COLUMN_AUTOMATIC, city.isAutomatic)
        return values
    }

    private fun getQueryArgs(id: Long): Array<String> {
        return arrayOf(id.toString())
    }

    companion object {
        private const val TABLE_NAME = "cities"
        private const val COLUMN_ROWID = "rowid"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_LONGITUDE = "longitude"
        private const val COLUMN_LATITUDE = "latitude"
        private const val COLUMN_ALTITUDE = "altitude"
        private const val COLUMN_TIMEZONE = "timezone"
        private const val COLUMN_AUTOMATIC = "automatic"
        private const val SQL_CREATE_TABLE = "CREATE TABLE cities (name TEXT, longitude DOUBLE, latitude DOUBLE, altitude DOUBLE, timezone TEXT, automatic BOOLEAN)"
        private const val SQL_DROP_TABLE = "DROP TABLE IF EXISTS cities"
        private const val SQL_QUERY_ALL = "SELECT rowid, name, longitude, latitude, altitude, timezone, automatic FROM cities ORDER BY name ASC"
        private const val SQL_QUERY = "SELECT name, longitude, latitude, altitude, timezone, automatic FROM cities WHERE rowid = ?"
    }

}