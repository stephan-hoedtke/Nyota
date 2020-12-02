package com.stho.nyota.sky.utilities

interface IDBObject {

    enum class Status {
        NEW,
        DELETED,
        PERSISTENT,
        TO_DELETE,
    }

    var status: Status

    var id: Long

    val uniqueTransientId: Long

    fun markAsDeleted() {
        status = Status.TO_DELETE
    }

    fun markAsRestored() {
        status = if (id > 0) Status.PERSISTENT else Status.NEW
    }

    fun markAsNew() {
        status = Status.NEW
    }

    val isNew: Boolean

    val isPersistent: Boolean

    val isToDelete: Boolean

    companion object {
        fun isNew(status: Status) = (status == Status.NEW)
        fun isPersistent(status: Status) = (status == Status.PERSISTENT)
        fun isToDelete(status: Status) = (status == Status.TO_DELETE)
    }
}
