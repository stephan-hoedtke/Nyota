package com.stho.nyota.sky.universe

import com.stho.nyota.sky.utilities.Degree
import com.stho.nyota.sky.utilities.Hour
import com.stho.nyota.sky.utilities.IDBObject

class Target(override val name: String, ra: Double, decl: Double) : AbstractElement(), IDBObject {

    init {
        RA = ra
        Decl = decl
    }

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
        get() = com.stho.nyota.R.drawable.target

    override val largeImageId: Int
        get() = com.stho.nyota.R.drawable.target

    override fun toString(): String {
        return name
    }

    companion object {

        fun create(id: Long, name: String, ra: Double, decl: Double): Target {
            return Target(name, ra, decl).apply {
                this.id = id
            }
        }

        fun create(name: String, ra: Double, decl: Double): Target {
            return Target(name, ra, decl)
        }

        fun create(name: String, ascension: String, declination: String): Target {
            return Target.create(name, Hour.fromHour(ascension).toDegree(), Degree.fromDegree(declination).toDegree())
        }

        fun createDefaultNeowise(): Target {
            return Target.create("C/2020 F3 (NEOWISE)", "07h 02m 59", "+43° 54' 42")
        }
    }
}
