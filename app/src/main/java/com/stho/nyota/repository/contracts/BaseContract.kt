package com.stho.nyota.repository.contracts

import android.database.Cursor
import java.util.*

abstract class BaseContract {

    protected fun getString(cursor: Cursor, columnIndex: Int): String {
        return getStringOrDefault(cursor, columnIndex) ?: ""
    }

    protected fun getString(cursor: Cursor, columnIndex: Int, defaultValue: String): String {
        return getStringOrDefault(cursor, columnIndex) ?: defaultValue
    }

    protected fun getStringOrDefault(cursor: Cursor, columnIndex: Int): String? {
        return if (cursor.isNull(columnIndex)) null else cursor.getString(columnIndex).trim { it <= ' ' }
    }

    protected fun getInt(cursor: Cursor, columnIndex: Int): Int {
        return getInt(cursor, columnIndex, 0)
    }

    protected fun getInt(cursor: Cursor, columnIndex: Int, defaultValue: Int): Int {
        return if (cursor.isNull(columnIndex)) defaultValue else cursor.getInt(columnIndex)
    }

    protected fun getLong(cursor: Cursor, columnIndex: Int): Long {
        return getLong(cursor, columnIndex, 0)
    }

    protected fun getLong(cursor: Cursor, columnIndex: Int, defaultValue: Long): Long {
        return if (cursor.isNull(columnIndex)) defaultValue else cursor.getLong(columnIndex)
    }

    protected fun getDouble(cursor: Cursor, columnIndex: Int): Double {
        return getDouble(cursor, columnIndex, 0.0)
    }

    protected fun getDouble(cursor: Cursor, columnIndex: Int, defaultValue: Double): Double {
        return if (cursor.isNull(columnIndex)) defaultValue else cursor.getDouble(columnIndex)
    }

    protected fun getBoolean(cursor: Cursor, columnIndex: Int): Boolean {
        return getBoolean(cursor, columnIndex, false)
    }

    protected fun getBoolean(cursor: Cursor, columnIndex: Int, defaultValue: Boolean): Boolean {
        return if (cursor.isNull(columnIndex)) defaultValue else (cursor.getShort(columnIndex) > 0)
    }

    protected fun getDateTime(cursor: Cursor, columnIndex: Int): Calendar {
        return if (cursor.isNull(columnIndex)) Calendar.getInstance() else fromMillis(cursor.getLong(columnIndex))
    }

    protected fun getTimeZone(cursor: Cursor, columnIndex: Int): TimeZone {
        return if (cursor.isNull(columnIndex)) TimeZone.getDefault() else TimeZone.getTimeZone(cursor.getString(columnIndex))
    }

    companion object {
        private fun fromMillis(millis: Long): Calendar {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = millis
            return calendar
        }
    }
}