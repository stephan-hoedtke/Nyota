package com.stho.nyota.sky.utilities

import java.util.*

/**
 * Created by shoedtke on 29.09.2016.
 */
class IconValueList : ArrayList<IIconValue>() {
    fun add(imageId: Int, value: String): Boolean {
        return this.add(IconValue(imageId, value))
    }

    fun add(imageId: Int, degree: Degree?): Boolean {
        return if (degree == null)
            false
        else
            this.add(IconValue(imageId, degree.toString()))
    }

    fun add(imageId: Int, hour: Hour?): Boolean {
        return if (hour == null)
            false
        else
            this.add(IconValue(imageId, hour.toString()))
    }

    fun add(imageId: Int, utc: UTC?, timeZone: TimeZone?): Boolean {
        return if (utc == null || timeZone == null)
            false
        else
            this.add(IconValue(imageId, Formatter.toString(utc, timeZone, Formatter.TimeFormat.DATETIME_SEC)))
    }
}