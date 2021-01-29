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
            val link = getString(cursor, 6)
            constellations.createWithId(id, rank, name, abbreviation, english, german, link)
        }
        cursor.close()
    }

    companion object {
        private const val SQL_QUERY_ALL = "SELECT id, rank, name, abbreviation, english, german, link FROM constellations ORDER BY name ASC"
    }

}