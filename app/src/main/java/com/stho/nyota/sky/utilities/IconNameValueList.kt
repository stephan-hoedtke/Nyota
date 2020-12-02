package com.stho.nyota.sky.utilities

import java.util.*

/**
 * Created by shoedtke on 29.09.2016.
 */
class IconNameValueList : ArrayList<IIconNameValue>() {
    fun add(imageId: Int, name: String, value: String): Boolean {
        return this.add(IconNameValue(imageId, name, value))
    }

    fun add(imageId: Int, name: String, degree: Degree?): Boolean {
        return if (degree == null)
            false
        else
            return this.add(IconNameValue(imageId, name, degree.toString()))
    }

    fun add(imageId: Int, name: String, hour: Hour?): Boolean {
        return if (hour == null)
            false
        else
            this.add(IconNameValue(imageId, name, hour.toString()))
    }

    fun add(imageId: Int, name: String, utc: UTC?, timeZone: TimeZone?): Boolean {
        return if (utc == null || timeZone == null)
            false
        else
            this.add(IconNameValue(imageId, name, Formatter.toString(utc, timeZone, Formatter.TimeFormat.DATETIME)))
    }
}