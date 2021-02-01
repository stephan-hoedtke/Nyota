package com.stho.nyota.repository.contracts

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import java.lang.Exception
import java.util.*

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

    fun write(name: String, value: Int) {
        write(name, serializeFromInt(value))
    }

    fun write(name: String, value: Double) {
        write(name, serializeFromDouble(value))
    }

    fun write(name: String, value: String?) {
        val values = getContentValues(name, value ?: "")
        if (update(name, values) == 0) {
            create(values)
        }
    }

    fun readString(name: String, defaultValue: String): String =
        readStringOrDefault(name) ?: defaultValue

    fun readStringOrDefault(name: String, defaultValue: String?): String? =
        readStringOrDefault(name) ?: defaultValue

    fun readBoolean(name: String, defaultValue: Boolean): Boolean =
        deserializeIntoBoolean(readStringOrDefault(name), defaultValue)

    fun readInt(name: String, defaultValue: Int): Int =
        deserializeIntoInt(readStringOrDefault(name), defaultValue)

    fun readDouble(name: String, defaultValue: Double): Double =
        deserializeIntoDouble(readStringOrDefault(name), defaultValue)

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
        private fun getContentValues(name: String?, value: String): ContentValues {
            val values = ContentValues()
            values.put(COLUMN_NAME, name)
            values.put(COLUMN_VALUE, value)
            return values
        }

        private fun getQueryArgs(name: String?): Array<String?> =
            arrayOf(name)

        private fun deserializeIntoBoolean(value: String?, defaultValue: Boolean): Boolean =
            try {
                when {
                    "TRUE".equals(value, true) -> true
                    "FALSE".equals(value, true) -> false
                    else -> defaultValue
                }
            } catch (ex: Exception) {
                defaultValue
            }

        private fun serializeFromBoolean(value: Boolean): String =
            if (value) "TRUE" else "FALSE"

        private fun deserializeIntoInt(value: String?, defaultValue: Int): Int =
            try {
                value?.toInt() ?: defaultValue
            } catch (ex: Exception) {
                defaultValue
            }

        private fun deserializeIntoDouble(value: String?, defaultValue: Double): Double =
            try {
                value?.toDouble() ?: defaultValue
            } catch (ex: Exception) {
                defaultValue
            }

        private fun serializeFromInt(value: Int): String =
            value.toString()

        private fun serializeFromDouble(value: Double): String =
            value.toString()
    }
}