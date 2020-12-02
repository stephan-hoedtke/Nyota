package com.stho.nyota.ui.cities

import android.view.View
import androidx.recyclerview.widget.RecyclerView

interface ISwipeToDeleteAdapter {
    fun getForegroundLayer(viewHolder: RecyclerView.ViewHolder): View
    fun getBackgroundColor(isCurrentlyActive: Boolean): Int
    fun delete(position: Int)
}