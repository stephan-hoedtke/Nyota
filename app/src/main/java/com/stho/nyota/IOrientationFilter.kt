package com.stho.nyota

import com.stho.nyota.sky.utilities.Orientation
import com.stho.nyota.sky.utilities.Quaternion

interface IOrientationFilter {
    fun onOrientationAnglesChanged(orientation: Quaternion)
    val currentOrientation: Orientation
}
