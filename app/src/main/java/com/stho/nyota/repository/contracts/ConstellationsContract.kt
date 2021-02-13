package com.stho.nyota.repository.contracts

import android.database.sqlite.SQLiteDatabase
import com.stho.nyota.sky.universe.Constellations

internal class ConstellationsContract(private val db: SQLiteDatabase) : BaseContract() {

    fun read(constellations: Constellations) {
        val cursor = db.rawQuery(SQL_QUERY_ALL, null)
        while (cursor.moveToNext()) {
            val id = getLong(cursor, 0)
            val rank = getInt(cursor, 1)
            val name = getString(cursor, 2)
            val abbreviation = getString(cursor, 3)
            val english = getString(cursor, 4)
            val german = getString(cursor, 5)
            val french = getString(cursor, 6)
            val greek = getString(cursor, 7)
            val author = getString(cursor, 8)
            val year = getInt(cursor, 9)
            val brightness = getDouble(cursor, 10)
            val visibility = getString(cursor, 11)
            val map = getString(cursor, 12)
            val link = getString(cursor, 13)
            constellations.createWithId(id, rank, name, abbreviation, english, german, french, greek, author, year, brightness, visibility, map, link)
        }
        cursor.close()
    }

    companion object {
        private const val SQL_QUERY_ALL = "SELECT id, rank, name, abbreviation, english, german, french, greek, author, year, brightness, visibility, map, link FROM constellations ORDER BY name ASC"
    }

}