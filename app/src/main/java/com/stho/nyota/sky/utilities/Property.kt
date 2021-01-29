package com.stho.nyota.sky.utilities

/**
 * Created by shoedtke on 28.09.2016.
 */
data class Property(override val keyType: PropertyKeyType, override val key: String, override val imageId: Int, override val name: String, override val value: String) : IProperty
