package com.stho.nyota.repository

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase

internal class SettingsContract(private val db: SQLiteDatabase) : BaseContract() {
    fun createTable() {
        db.execSQL(SQL_CREATE_TABLE)
    }

    fun dropTable() {
        db.execSQL(SQL_DROP_TABLE)
    }

    fun write(name: String, value: Boolean) {
        write(name, serializeFromBoolean(value))
    }

    fun write(name: String, value: String?) {
        val values = getContentValues(name, value ?: "")
        if (update(name, values) == 0) {
            create(values)
        }
    }

    fun readString(name: String, defaultValue: String): String {
        return readStringOrDefault(name) ?: defaultValue
    }

    fun readStringOrDefault(name: String, defaultValue: String?): String? {
        return readStringOrDefault(name) ?: defaultValue
    }

    fun readBoolean(name: String, defaultValue: Boolean): Boolean {
        return deserializeIntoBoolean(readStringOrDefault(name), defaultValue)
    }

    fun readBoolean(name: String): Boolean {
        return deserializeIntoBoolean(readStringOrDefault(name), false)
    }

    private fun readStringOrDefault(name: String): String? {
        var value: String? = null
        val cursor = db.rawQuery(SQL_QUERY, getQueryArgs(name))
        if (cursor.moveToFirst()) {
            value = getStringOrDefault(cursor, 0)
        }
        cursor.close()
        return value
    }


    private fun update(key: String?, values: ContentValues): Int {
        val whereClause = "$COLUMN_NAME = ?"
        return db.update(TABLE_NAME, values, whereClause, getQueryArgs(key))
    }

    private fun create(values: ContentValues) {
        db.insert(TABLE_NAME, null, values)
    }

    companion object {
        private const val TABLE_NAME = "settings"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_VALUE = "value"
        private const val SQL_CREATE_TABLE = "CREATE TABLE settings (name TEXT, value TEXT)"
        private const val SQL_DROP_TABLE = "DROP TABLE IF EXISTS settings"
        private const val SQL_QUERY = "SELECT value FROM settings WHERE name = ?"
        private const val TRUE = "1"
        private const val FALSE = "0"
        private fun getContentValues(name: String?, value: String): ContentValues {
            val values = ContentValues()
            values.put(COLUMN_NAME, name)
            values.put(COLUMN_VALUE, value)
            return values
        }

        private fun getQueryArgs(name: String?): Array<String?> {
            return arrayOf(name)
        }

        private fun deserializeIntoBoolean(value: String?, defaultValue: Boolean): Boolean {
            return when {
                "TRUE".equals(value, true) -> true
                "FALSE".equals(value, true) -> false
                else -> defaultValue
            }
        }

        private fun serializeFromBoolean(value: Boolean): String {
            return if (value) "TRUE" else "FALSE"
        }
    }

}