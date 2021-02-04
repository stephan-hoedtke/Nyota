package com.stho.nyota.sky.universe

import com.stho.nyota.sky.utilities.Degree
import com.stho.nyota.sky.utilities.Hour
import com.stho.nyota.sky.utilities.IDBObject
import kotlin.concurrent.fixedRateTimer

class Target(override val name: String, val friendlyName: String, ra: Double, decl: Double) : AbstractElement(), IDBObject {

    init {
        RA = ra
        Decl = decl
    }

    override val key: String =
        toKey(name)

    val hasFriendlyName: Boolean
        get() = friendlyName.isNotBlank()

    override var id: Long = 0

    override val uniqueTransientId: Long by lazy { System.nanoTime() }

    override var status: IDBObject.Status = IDBObject.Status.NEW

    override val isToDelete
        get() = IDBObject.isToDelete(status)

    override val isNew
        get() = IDBObject.isNew(status)

    override val isPersistent
        get() = IDBObject.isPersistent(status)

    override val imageId: Int
        get() = com.stho.nyota.R.drawable.target_red

    override val largeImageId: Int
        get() = com.stho.nyota.R.drawable.target_red

    override fun toString(): String =
        when {
            hasFriendlyName -> friendlyName
            else -> name
        }

    companion object {

        private fun toKey(name: String) =
            "TARGET:$name"

        fun isValidKey(key: String): Boolean =
            key.startsWith("TARGET:")

        fun nameFromKey(key: String): String =
            key.substring(7)

        fun createWithId(id: Long, name: String, friendlyName: String, ra: Double, decl: Double): Target {
            return Target(name, friendlyName, ra, decl).apply {
                this.id = id
            }
        }

        fun create(name: String, friendlyName: String, ra: Double, decl: Double): Target {
            return Target(name, friendlyName, ra, decl)
        }

        fun create(name: String, friendlyName: String, ascension: String, declination: String): Target {
            return Target.create(name, friendlyName, Hour.fromHour(ascension).angleInDegree, Degree.fromDegree(declination).angleInDegree)
        }

        fun createDefaultNeowise(): Target {
            return Target.create("C/2020 F3", "NEOWISE", "07h 02m 59", "+43° 54' 42")
        }
    }
}
