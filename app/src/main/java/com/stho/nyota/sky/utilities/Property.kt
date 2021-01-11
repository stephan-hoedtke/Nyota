package com.stho.nyota.sky.utilities

/**
 * Created by shoedtke on 28.09.2016.
 */
data class Property(override val key: PropertyKey, override val imageId: Int, override val name: String, override val value: String) : IProperty

// TODO: remove useless interfaces like ITime, IProperty, ILocation etc.