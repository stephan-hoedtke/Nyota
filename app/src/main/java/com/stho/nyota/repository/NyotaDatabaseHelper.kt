package com.stho.nyota.repository

import android.content.Context
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.*

/**
 * Database helper to use the database which is provided as embedded asset with the name nyota.sqlite
 * See: C# project NyotaDataProvider which is used to generate the database
 * The database pre-defines the table structures required for Nyota including all stars, constellations, cities...)
 */
class NyotaDatabaseHelper(private val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val preferences: SharedPreferences = context.getSharedPreferences(context.packageName + "database_versions", Context.MODE_PRIVATE);

    override fun onCreate(db: SQLiteDatabase) {
        // Nothing to do
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Nothing to do
    }

    @Synchronized
    private fun installOrUpdateIfNecessary() {
        if (installedDatabaseIsOutdated()) {
            context.deleteDatabase(DATABASE_NAME)
            installDatabaseFromAssets()
            writeDatabaseVersionInPreferences()
        }
    }

    override fun getWritableDatabase(): SQLiteDatabase {
        installOrUpdateIfNecessary()
        return super.getWritableDatabase()
    }

    override fun getReadableDatabase(): SQLiteDatabase {
        installOrUpdateIfNecessary()
        return super.getReadableDatabase()
    }

    private fun installedDatabaseIsOutdated(): Boolean {
        return preferences.getInt(DATABASE_NAME, 0) < DATABASE_VERSION
    }

    private fun writeDatabaseVersionInPreferences() {
        preferences.edit()
            .putInt(DATABASE_NAME, DATABASE_VERSION)
            .apply()
    }

    private fun installDatabaseFromAssets() {
        try {
            val inputFileName = "$ASSETS_PATH/$DATABASE_NAME"
            val inputStream = context.assets.open(inputFileName)
            val outputFileName = context.getDatabasePath(DATABASE_NAME).path
            val outputFile = File(outputFileName)
            val outputStream = FileOutputStream(outputFile)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.flush()
            outputStream.close()
        } catch (ex: Throwable) {
            ex.printStackTrace()
            throw RuntimeException("The database $DATABASE_NAME couldn't be installed.", ex)
        }
    }

    companion object {
        private const val DATABASE_NAME = "nyota.sqlite"
        private const val DATABASE_VERSION = 19
        private const val ASSETS_PATH = "databases"
    }
}
