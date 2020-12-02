package com.stho.nyota.sky.utilities

import java.util.*

/**
 * Created by shoedtke on 29.09.2016.
 */
class PropertyList : ArrayList<IProperty>() {

    fun add(imageId: Int, name: String, value: String): Boolean {
        return add(0, imageId, name, value)
    }

    fun add(imageId: Int, name: String, degree: Degree?): Boolean {
        return add(0, imageId, name, degree)
    }

    fun add(key: Int, imageId: Int, name: String, value: String): Boolean {
        return this.add(Property(key, imageId, name, value))
    }

    fun add(key: Int, imageId: Int, name: String, degree: Degree?): Boolean {
        return if (degree == null)
            false
        else
            return this.add(Property(key, imageId, name, degree.toString()))
    }

    fun add(imageId: Int, name: String, hour: Hour?): Boolean {
        return add(0, imageId, name, hour)
    }

    fun add(key: Int, imageId: Int, name: String, hour: Hour?): Boolean {
        return if (hour == null)
            false
        else
            this.add(Property(key, imageId, name, hour.toString()))
    }

    fun add(imageId: Int, name: String, utc: UTC?, timeZone: TimeZone?): Boolean {
        return add(0, imageId, name, utc, timeZone)
    }

    fun add(key: Int, imageId: Int, name: String, utc: UTC?, timeZone: TimeZone?): Boolean {
        return if (utc == null || timeZone == null)
            false
        else
            this.add(Property(key, imageId, name, Formatter.toString(utc, timeZone, Formatter.TimeFormat.DATETIME)))
    }

    fun add(imageId: Int, value: String): Boolean {
        return this.add(Property(0, imageId, "", value))
    }

    fun add(imageId: Int, degree: Degree?): Boolean {
        return if (degree == null)
            false
        else
            this.add(Property(0, imageId, "", degree.toString()))
    }

    fun add(imageId: Int, hour: Hour?): Boolean {
        return if (hour == null)
            false
        else
            this.add(Property(0, imageId, "", hour.toString()))
    }

    fun add(imageId: Int, utc: UTC?, timeZone: TimeZone?): Boolean {
        return if (utc == null || timeZone == null)
            false
        else
            this.add(Property(0, imageId, "", Formatter.toString(utc, timeZone, Formatter.TimeFormat.DATETIME_SEC)))
    }
}

