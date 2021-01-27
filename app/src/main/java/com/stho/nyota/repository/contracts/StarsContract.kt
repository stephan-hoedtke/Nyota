package com.stho.nyota.repository.contracts

import android.database.sqlite.SQLiteDatabase
import com.stho.nyota.sky.universe.*

internal class StarsContract(private val db: SQLiteDatabase) : BaseContract() {

    fun read(stars: Stars) {
        val cursor = db.rawQuery(SQL_QUERY_ALL, null)
        while (cursor.moveToNext()) {
            val id = getLong(cursor, 0)
            val hd = getInt(cursor, 1)
            val name = getString(cursor, 2)
            val friendlyName = getString(cursor, 3)
            val symbol = getString(cursor, 4)
            val rightAscension = getDouble(cursor, 5)
            val declination = getDouble(cursor, 6)
            val magnitude = getDouble(cursor, 7, 100.0)
            val distance = getDouble(cursor, 8)
            val constellationId = getLong(cursor, 9)
            stars.createWithId(id, hd, name, friendlyName, symbol, rightAscension, declination, magnitude, distance, constellationId)
        }
        cursor.close()
    }

    companion object {
        private const val SQL_QUERY_ALL = "SELECT id, hd, name, friendlyName, symbol, rightAscension, declination, magnitude, distance, constellationId FROM stars ORDER BY id ASC"
    }

}