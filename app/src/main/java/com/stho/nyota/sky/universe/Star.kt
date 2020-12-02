package com.stho.nyota.sky.universe

/**
 * Created by shoedtke on 31.08.2016.
 */
class Star internal constructor(override val name: String, val symbol: String, ra: Double, decl: Double, val brightness: Double) : AbstractElement() {

    override val imageId: Int
        get() = com.stho.nyota.R.mipmap.star

    override val largeImageId: Int
        get() = com.stho.nyota.R.drawable.star

    init {
        RA = ra
        Decl = decl
    }
}