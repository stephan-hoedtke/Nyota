package com.stho.nyota.sky.utilities

import java.util.*

/**
 * Created by shoedtke on 29.09.2016.
 */
class PropertyList : ArrayList<IProperty>() {

    fun add(imageId: Int, name: String, value: String) =
        add(PropertyKey.NULL, imageId, name, value)

    fun add(imageId: Int, name: String, degree: Degree?) =
        add(PropertyKey.NULL, imageId, name, degree)

    fun add(imageId: Int, name: String, hour: Hour?) =
        add(PropertyKey.NULL, imageId, name, hour)

    fun add(imageId: Int, name: String, utc: UTC?, timeZone: TimeZone) =
        add(PropertyKey.NULL, imageId, name, utc, timeZone)

    fun add(key: PropertyKey, imageId: Int, name: String, value: String) =
        add(Property(key, imageId, name, value))

    fun add(key: PropertyKey, imageId: Int, name: String, degree: Degree?) =
        degree?.also {
            val value = it.toString()
            add(Property(key, imageId, name, value))
        }

    fun add(key: PropertyKey, imageId: Int, name: String, hour: Hour?) =
        hour?.also {
            val value = it.toString()
            add(Property(key, imageId, name, value))
        }

    fun add(key: PropertyKey, imageId: Int, name: String, utc: UTC?, timeZone: TimeZone) =
        utc?.also {
            val value = Formatter.toString(it, timeZone, Formatter.TimeFormat.DATETIME)
            add(Property(key, imageId, name, value))
        }
}


