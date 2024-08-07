package com.stho.nyota.sky.universe

class SpecialElement internal constructor(override val name: String, ra: Double, decl: Double) : AbstractElement(RA = ra, Decl = decl) {

    override val key: String =
        "SPECIAL:$name"

    override val imageId: Int
        get() = throw NotImplementedError()

    override val largeImageId: Int
        get() = throw NotImplementedError()

}
