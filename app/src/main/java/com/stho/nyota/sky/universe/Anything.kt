package com.stho.nyota.sky.universe

class Anything(override val name: String, RA: Double, Decl: Double, mag: Double, distance: Double) : AbstractElement(RA, Decl, mag) {

    override val key: String =
        toKey(name)

    override val imageId: Int
        get() = com.stho.nyota.R.drawable.star

    override val largeImageId: Int
        get() = imageId

    companion object {
        private fun toKey(name: String): String =
            "ANY:$name"

        fun isValidKey(key: String): Boolean =
            key.startsWith("ANY:")

        fun nameFromKey(key: String): String =
            key.substring(4)
    }
}
