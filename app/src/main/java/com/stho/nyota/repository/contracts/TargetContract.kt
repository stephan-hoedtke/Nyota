package com.stho.nyota.repository.contracts

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.stho.nyota.sky.universe.Target
import com.stho.nyota.sky.universe.Targets

internal class TargetContract(private val db: SQLiteDatabase) : BaseContract() {

    fun write(target: Target) {
        when {
            target.isPersistent -> update(target.id, getContentValues(target))
            target.isToDelete -> delete(target.id)
            target.isNew -> target.id = create(getContentValues(target))
        }
    }

    fun read(targets: Targets) {
        val cursor = db.rawQuery(SQL_QUERY_ALL, null)
        while (cursor.moveToNext()) {
            val id = getLong(cursor, 0)
            val name = getString(cursor, 1)
            val friendlyName = getString(cursor, 2)
            val ra = getDouble(cursor, 3)
            val decl = getDouble(cursor, 4)
            targets.createWithId(id, name, friendlyName, ra, decl)
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

    private fun getContentValues(target: Target): ContentValues {
        val values = ContentValues()
        values.put(COLUMN_NAME, target.name)
        values.put(COLUMN_FRIENDLY_NAME, target.friendlyName)
        values.put(COLUMN_RIGHT_ASCENSION, target.RA)
        values.put(COLUMN_DECLINATION, target.Decl)
        return values
    }

    private fun getQueryArgs(id: Long): Array<String> =
        arrayOf(id.toString())

    companion object {
        private const val TABLE_NAME = "targets"
        private const val COLUMN_ROWID = "rowid"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_FRIENDLY_NAME = "friendlyName"
        private const val COLUMN_RIGHT_ASCENSION = "rightAscension"
        private const val COLUMN_DECLINATION = "declination"
        private const val SQL_QUERY_ALL = "SELECT rowid, name, friendlyName, rightAscension, declination FROM targets ORDER BY name ASC"
    }

}