package com.stho.nyota

import android.view.MotionEvent

interface ISkyViewListener {
    fun onSingleTapConfirmed(e: MotionEvent?)
    fun onChangeSkyCenter()
}

