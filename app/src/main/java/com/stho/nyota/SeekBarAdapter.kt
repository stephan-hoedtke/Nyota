package com.stho.nyota

import android.widget.SeekBar

open class SeekBarAdapter(private val listener: ((Int) -> Unit)): SeekBar.OnSeekBarChangeListener {
    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        listener.invoke(progress)
    }
    override fun onStartTrackingTouch(arg0: SeekBar) {}
    override fun onStopTrackingTouch(seekBar: SeekBar) {}
}
