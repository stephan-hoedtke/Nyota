package com.stho.nyota.sky.universe

import com.stho.nyota.sky.utilities.UTC
import java.util.*
import kotlin.collections.ArrayList

class Hint private constructor(private val description: String) {

    private val elements: ArrayList<IElement> = ArrayList()

    val size: Int
        get() = elements.size

    operator fun get(index: Int) =
        elements[index]

    override fun toString(): String =
        description

    companion object {
        fun create(description: String, from: Star, to: Star): Hint =
            Hint(description).apply {
                elements.add(from)
                elements.add(to)
            }

        fun create(description: String, one: Star, two: Star, three: Star): Hint =
            Hint(description).apply {
                elements.add(one)
                elements.add(two)
                elements.add(three)
            }
    }
}
