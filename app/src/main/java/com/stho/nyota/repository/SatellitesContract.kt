package com.stho.nyota.repository

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.stho.nyota.sky.universe.Satellite
import com.stho.nyota.sky.universe.Satellites
import java.util.*
import kotlin.collections.ArrayList

internal class SatellitesContract(private val db: SQLiteDatabase) : BaseContract() {

    fun createTable() {
        db.execSQL(SQL_CREATE_TABLE)
    }

    fun dropTable() {
        db.execSQL(SQL_DROP_TABLE)
    }

    fun write(satellite: Satellite) {
        when {
            satellite.isPersistent -> update(satellite.id, getContentValues(satellite))
            satellite.isToDelete -> delete(satellite.id)
            satellite.isNew -> satellite.id = create(getContentValues(satellite))
        }
    }

    fun read(satellites: Satellites) {
        val cursor = db.rawQuery(SQL_QUERY_ALL, null)
        while (cursor.moveToNext()) {
            val id = getLong(cursor, 0)
            val name = getString(cursor, 1)
            val displayName = getString(cursor, 2)
            val noradSatelliteNumber = getInt(cursor, 3)
            val elements = getString(cursor, 4)
            satellites.createWithId(id, name, displayName, noradSatelliteNumber, elements)
        }
        cursor.close()
    }

    private fun delete(id: Long) {
        val whereClause = "$COLUMN_ROWID = ?"
        db.delete(TABLE_NAME, whereClause, getQueryArgs(id))
    }

    private fun update(id: Long, values: ContentValues) {
        val whereClause = "$COLUMN_ROWID = ?"
        db.update(TABLE_NAME, values, whereClause, getQueryArgs(id))
    }

    private fun create(values: ContentValues): Long =
        db.insert(TABLE_NAME, null, values)

    private fun getContentValues(satellite: Satellite): ContentValues {
        val values = ContentValues()
        values.put(COLUMN_NAME, satellite.name)
        values.put(COLUMN_DISPLAY_NAME, satellite.displayName)
        values.put(COLUMN_NORAD_SATELLITE_NUMBER, satellite.noradSatelliteNumber)
        values.put(COLUMN_TLE, satellite.tle.serialize())
        return values
    }

    private fun getQueryArgs(id: Long): Array<String> {
        return arrayOf(id.toString())
    }

    companion object {
        private const val TABLE_NAME = "satellites"
        private const val COLUMN_ROWID = "rowid"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_DISPLAY_NAME = "displayName"
        private const val COLUMN_NORAD_SATELLITE_NUMBER = "noradSatelliteNumber"
        private const val COLUMN_TLE = "tle"
        private const val SQL_CREATE_TABLE = "CREATE TABLE satellites (name TEXT, displayName TEXT, noradSatelliteNumber INTEGER, tle TEXT)"
        const val SQL_DROP_TABLE = "DROP TABLE IF EXISTS satellites"
        private const val SQL_QUERY_ALL = "SELECT rowid, name, displayName, noradSatelliteNumber, tle FROM satellites ORDER BY name ASC"
        private const val SQL_QUERY = "SELECT name, displayName, noradSatelliteNumber, tle FROM satellites WHERE rowid = ?"
    }

}