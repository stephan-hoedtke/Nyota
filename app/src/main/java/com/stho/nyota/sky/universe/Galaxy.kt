package com.stho.nyota.sky.universe

import com.stho.nyota.R

class Galaxy(override val name: String, override val imageId: Int, RA: Double, Decl: Double, mag: Double, distance: Double) : AbstractElement(RA, Decl, mag) {

    override val largeImageId: Int
        get() = imageId

}
