package com.stho.nyota

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration

class RecyclerViewItemDivider(context: Context) : DividerItemDecoration(context, DividerItemDecoration.VERTICAL) {
    init {
        ContextCompat.getDrawable(context, R.drawable.divider)?.also {
            setDrawable(it)
        }
    }
}
