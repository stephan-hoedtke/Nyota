package com.stho.nyota.repository.contracts

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.stho.nyota.sky.universe.Satellite
import com.stho.nyota.sky.universe.Satellites

internal class SatellitesContract(private val db: SQLiteDatabase) : BaseContract() {

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
            val friendlyName = getString(cursor, 2)
            val noradSatelliteNumber = getInt(cursor, 3)
            val elements = getString(cursor, 4)
            satellites.createWithId(id, name, friendlyName, noradSatelliteNumber, elements)
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
        values.put(COLUMN_FRIENDLY_NAME, satellite.friendlyName)
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
        private const val COLUMN_FRIENDLY_NAME = "friendlyName"
        private const val COLUMN_NORAD_SATELLITE_NUMBER = "noradSatelliteNumber"
        private const val COLUMN_TLE = "tle"
        private const val SQL_QUERY_ALL = "SELECT rowid, name, friendlyName, noradSatelliteNumber, tle FROM satellites ORDER BY name ASC"
    }

}