package com.stho.nyota

import com.stho.nyota.sky.utilities.Topocentric

interface ISkyViewListener {
    fun onSingleTap(position: Topocentric)
    fun onDoubleTap()
    fun onChangeCenter()
    fun onChangeZoom()
}

