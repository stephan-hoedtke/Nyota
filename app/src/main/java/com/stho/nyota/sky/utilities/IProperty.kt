package com.stho.nyota.sky.utilities

enum class PropertyKey() {
    NULL,
    STAR,
    CONSTELLATION,
    TRANSLATION,
}

/**
 * Created by shoedtke on 28.09.2016.
 */
interface IProperty {
    val key: PropertyKey
    val imageId: Int
    val name: String
    val value: String
}
