package com.stho.nyota.ui.cities


import android.graphics.Canvas
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

/*
    See:
    https://medium.com/@zackcosborn/step-by-step-recyclerview-swipe-to-delete-and-undo-7bbae1fce27e
    https://www.androidhive.info/2017/09/android-recyclerview-swipe-delete-undo-using-itemtouchhelper/
*/

class SwipeToDelete(private val adapter: ISwipeToDeleteAdapter) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        if (direction == ItemTouchHelper.LEFT || direction == ItemTouchHelper.RIGHT) {
            adapter.delete(position)
        }
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (viewHolder != null) {
            val foregroundLayer = adapter.getForegroundLayer(viewHolder)
            getDefaultUIUtil().onSelected(foregroundLayer)
        }
    }

    override fun onChildDrawOver(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        val foregroundLayer = adapter.getForegroundLayer(viewHolder).apply { setBackgroundColor(adapter.getBackgroundColor(isCurrentlyActive)) }
        getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundLayer, dX, dY, actionState, isCurrentlyActive)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        val foregroundLayer = adapter.getForegroundLayer(viewHolder)
        getDefaultUIUtil().clearView(foregroundLayer)
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        val foregroundLayer = adapter.getForegroundLayer(viewHolder)
        getDefaultUIUtil().onDraw(c, recyclerView, foregroundLayer, dX, dY, actionState, isCurrentlyActive)
    }
}

