package com.stho.nyota.sky.universe

import com.stho.nyota.sky.utilities.City
import java.util.*

class Targets {

    private val map: HashMap<String, Target> = HashMap<String, Target>()
    private val array: ArrayList<Target> = ArrayList<Target>()

    val size: Int
        get() = array.size

    fun addAll(collection: Collection<Target>) {
        for (target in collection) {
            add(target)
        }
    }

    fun add(target: Target) {
        val targetName = target.name
        val existingTarget = map[targetName]
        if (existingTarget != null) {
            val index = array.indexOf(existingTarget)
            array[index] = target
            map[targetName] = target
        } else {
            array.add(target)
            map[targetName] = target
        }
    }

    fun isValidIndex(index: Int) =
        0 <= index && index < array.size

    operator fun get(index: Int): Target =
        array[index]

    operator fun get(targetName: String): Target? {
        return map[targetName]
    }

    fun exists(targetName: String?): Boolean {
        return if (targetName != null)
            map.containsKey(targetName)
        else
            false
    }

    fun findTargetById(id: Long): Target? {
        return map.values.firstOrNull { it.id == id }
    }

    fun findTargetByName(targetName: String?): Target? {
        return if (targetName != null)
            map[targetName]
        else
            null
    }

    val values: List<Target>
        get() = array
}
