package com.stho.nyota.sky.utilities

import com.stho.nyota.sky.universe.Constellation
import com.stho.nyota.sky.universe.Star
import java.util.*

/**
 * Created by shoedtke on 29.09.2016.
 */
class PropertyList : ArrayList<IProperty>() {

    fun add(imageId: Int, name: String, value: String) =
        add(Property(PropertyKeyType.NULL, "", imageId, name, value))

    fun add(star: Star) =
        add(Property(PropertyKeyType.STAR, star.key, star.symbol.imageId, star.toString(), star.position.toString(), star.magnAsString))

    fun add(constellation: Constellation) =
        add(Property(PropertyKeyType.CONSTELLATION, constellation.key, constellation.imageId, constellation.name, constellation.position.toString()))

    fun add(keyType: PropertyKeyType, key: String, imageId: Int, name: String, value: String) =
        add(Property(keyType, key, imageId, name, value))

    fun add(imageId: Int, name: String, degree: Degree?) =
        degree?.also {
            val value = it.toString()
            add(Property(PropertyKeyType.NULL, "", imageId, name, value))
        }

    fun add(imageId: Int, name: String, hour: Hour?) =
        hour?.also {
            val value = it.toString()
            add(Property(PropertyKeyType.NULL, "", imageId, name, value))
        }

    fun add(imageId: Int, name: String, utc: UTC?, timeZone: TimeZone) =
        utc?.also {
            val value = Formatter.toString(it, timeZone, Formatter.TimeFormat.DATETIME)
            add(Property(PropertyKeyType.NULL, "", imageId, name, value))
        }
}


