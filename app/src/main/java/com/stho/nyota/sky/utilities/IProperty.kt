package com.stho.nyota.sky.utilities

enum class PropertyKeyType() {
    NULL,
    STAR,
    CONSTELLATION,
}

/**
 * Created by shoedtke on 28.09.2016.
 */
interface IProperty {
    val keyType: PropertyKeyType
    val key: String
    val imageId: Int
    val name: String
    val value: String
    val hints: String
    val hasHints: Boolean
}
