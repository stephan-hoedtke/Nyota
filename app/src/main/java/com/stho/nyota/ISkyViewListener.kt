package com.stho.nyota

import android.view.MotionEvent

interface ISkyViewListener {
    fun onSingleTapConfirmed(e: MotionEvent?): Boolean
    fun onChangeSkyCenter()
}

