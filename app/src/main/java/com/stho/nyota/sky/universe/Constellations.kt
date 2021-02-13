package com.stho.nyota.sky.universe

import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Constellations {

    private val array: ArrayList<Constellation> = ArrayList()
    private val map: HashMap<Int, Constellation> = HashMap()

    val size: Int
        get() = array.size

    fun add(constellation: Constellation) {
        array.add(constellation)
        map[constellation.rank] = constellation
    }

    operator fun get(rank: Int): Constellation =
        map[rank] ?: throw Exception("Constellation $rank is not registered yet.")

    fun findConstellationById(constellationId: Long): Constellation? =
        if (constellationId > 0)
            array.find { x -> x.id == constellationId }
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
        array.first()

    fun createWithId(id: Long, rank: Int, name: String, abbreviation: String, english: String, german: String, french: String, greek: String, author: String, year: Int, brightness: Double, visibility: String, map: String, link: String) {
        Constellation.createWithId(id, rank, name, abbreviation, english, german, french, greek, author, year, brightness, visibility, map, link).also {
            add(it)
        }
    }

    val values: Collection<Constellation>
        get() = array

}
