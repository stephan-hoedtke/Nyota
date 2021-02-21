package com.stho.nyota.sky.universe

import java.lang.Exception
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Constellations {

    enum class Filter {
        Ptolemaeus,
        Zodiac,
        IAU;

        fun serialize(): String =
            toString()

        companion object {
            fun deserialize(value: String): Filter {
                return try {
                    valueOf(value)
                } catch (ex: Exception) {
                    IAU
                }
            }
        }
    }

    private val list: ArrayList<Constellation> = ArrayList()
    private val ranks: HashMap<Int, Constellation> = HashMap()

    val size: Int
        get() = list.size

    fun add(constellation: Constellation) {
        list.add(constellation)
        ranks[constellation.rank] = constellation
    }

    operator fun get(rank: Int): Constellation =
        ranks[rank] ?: throw Exception("Constellation $rank is not registered yet.")

    fun findConstellationById(constellationId: Long): Constellation? =
        if (constellationId > 0)
            list.find { x -> x.id == constellationId }
        else
            null

    fun findConstellationByKey(key: String): Constellation? =
        when (Constellation.isValidKey(key)) {
            true -> {
                val rank = Constellation.rankFromKey(key)
                get(rank)
            }
            false -> null
        }

    fun first(): Constellation =
        list.first()

    fun createWithId(id: Long, rank: Int, name: String, abbreviation: String, english: String, german: String, french: String, greek: String, author: String, year: Int, brightness: Double, visibility: String, map: String, link: String) {
        Constellation.createWithId(id, rank, name, abbreviation, english, german, french, greek, author, year, brightness, visibility, map, link).also {
            add(it)
        }
    }

    val values: Collection<Constellation>
        get() = list

}
