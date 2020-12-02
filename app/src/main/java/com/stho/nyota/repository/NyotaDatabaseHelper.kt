package com.stho.nyota.repository

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class NyotaDatabaseHelper(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        SettingsContract(db).createTable()
        CitiesContract(db).createTable()
        SatellitesContract(db).createTable()
        TargetContract(db).createTable()
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Just re-create
        SettingsContract(db).dropTable()
        CitiesContract(db).dropTable()
        SatellitesContract(db).dropTable()
        TargetContract(db).dropTable()
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        super.onDowngrade(db, oldVersion, newVersion)
        // Nothing we can do
    }

    companion object {
        private const val DATABASE_NAME = "nyota.sqlite"
        private const val DATABASE_VERSION = 5
    }
}