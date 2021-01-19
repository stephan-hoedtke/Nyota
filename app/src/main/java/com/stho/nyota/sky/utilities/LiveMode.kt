package com.stho.nyota.sky.utilities

enum class LiveMode {
    Off,
    Hints,
    MoveCenter;

    fun serialize(): String =
        toString()

    companion object {
        fun deserialize(value: String): LiveMode {
            return try {
                LiveMode.valueOf(value)
            } catch (ex: Exception) {
                LiveMode.Off
            }
        }
    }
}

