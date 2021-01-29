package com.stho.nyota.sky.universe

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

    fun findTargetById(id: Long): Target? =
        map.values.find { it.id == id }

    fun findTargetByKey(key: String): Target? =
        when (Target.isValidKey(key)) {
            true -> {
                val targetName = Target.nameFromKey(key)
                get(targetName)
            }
            false -> null
        }

    val values: List<Target>
        get() = array

    internal fun createWithId(id: Long, name: String, friendlyName: String, ra: Double, decl: Double) =
        Target.createWithId(id, name, friendlyName, ra, decl).also { add(it) }
}
