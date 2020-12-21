package com.stho.nyota.repository

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.stho.nyota.sky.universe.Target
import com.stho.nyota.sky.universe.Targets

internal class TargetContract(private val db: SQLiteDatabase) : BaseContract() {
    fun createTable() {
        db.execSQL(SQL_CREATE_TABLE)
    }

    fun dropTable() {
        db.execSQL(SQL_DROP_TABLE)
    }

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
            val ra = getDouble(cursor, 2)
            val decl = getDouble(cursor, 3)
            targets.createWithId(id, name, ra, decl)
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
        values.put(COLUMN_RA, target.RA)
        values.put(COLUMN_DECL, target.Decl)
        return values
    }

    private fun getQueryArgs(id: Long): Array<String> =
        arrayOf(id.toString())

    companion object {
        private const val TABLE_NAME = "targets"
        private const val COLUMN_ROWID = "rowid"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_RA = "ra"
        private const val COLUMN_DECL = "decl"
        private const val SQL_CREATE_TABLE = "CREATE TABLE targets (name TEXT, ra DOUBLE, decl DOUBLE)"
        private const val SQL_DROP_TABLE = "DROP TABLE IF EXISTS targets"
        private const val SQL_QUERY_ALL = "SELECT rowid, name, ra, decl FROM targets ORDER BY name ASC"
        private const val SQL_QUERY = "SELECT name, ra, decl FROM targets WHERE rowid = ?"
    }

}