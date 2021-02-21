package com.stho.nyota.sky.universe

import java.util.*

class Targets {

    private val list: ArrayList<Target> = ArrayList<Target>()
    private val names: HashMap<String, Target> = HashMap<String, Target>()

    val size: Int
        get() = list.size

    fun addAll(collection: Collection<Target>) {
        for (target in collection) {
            add(target)
        }
    }

    fun add(target: Target) {
        val targetName = target.name
        val existingTarget = names[targetName]
        if (existingTarget != null) {
            val index = list.indexOf(existingTarget)
            list[index] = target
            names[targetName] = target
        } else {
            list.add(target)
            names[targetName] = target
        }
    }

    fun isValidIndex(index: Int) =
        0 <= index && index < list.size

    operator fun get(index: Int): Target =
        list[index]

    operator fun get(targetName: String): Target? {
        return names[targetName]
    }

    fun findTargetById(id: Long): Target? =
        names.values.find { it.id == id }

    fun findTargetByKey(key: String): Target? =
        when (Target.isValidKey(key)) {
            true -> {
                val targetName = Target.nameFromKey(key)
                get(targetName)
            }
            false -> null
        }

    val values: List<Target>
        get() = list

    internal fun createWithId(id: Long, name: String, friendlyName: String, ra: Double, decl: Double) =
        Target.createWithId(id, name, friendlyName, ra, decl).also { add(it) }
}
