package com.stho.nyota.ui.cities

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.attachItemTouchHelper(itemTouchHelper: ItemTouchHelper) {
    itemTouchHelper.attachToRecyclerView(this)
}

fun RecyclerView.attachItemTouchHelper(callback: ItemTouchHelper.Callback) {
    ItemTouchHelper(callback).attachToRecyclerView(this)
}

fun RecyclerView.attachItemTouchHelper(adapter: ISwipeToDeleteAdapter) {
    ItemTouchHelper(SwipeToDelete(adapter)).attachToRecyclerView(this)
}
