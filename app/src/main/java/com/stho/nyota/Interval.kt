package com.stho.nyota

enum class Interval(private val friendlyName: String) {
    MINUTE("Minute"),
    HOUR("Hour"),
    DAY("Day"),
    MONTH("Month"),
    YEAR("Year");

    override fun toString(): String {
        return friendlyName
    }

    companion object {
        fun fromString(name: String?, default: Interval = HOUR): Interval {
            return values().find { it.name == name } ?: default
        }
    }
}

