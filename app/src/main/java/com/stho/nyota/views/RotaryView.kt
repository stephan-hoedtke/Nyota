package com.stho.nyota.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView
import com.stho.nyota.R

class RotaryView : AppCompatImageView {
    interface OnRotateListener {
        fun onRotate(delta: Double)
    }

    interface OnDoubleTapListener {
        fun onDoubleTap()
    }

    private var rotateListener: OnRotateListener? = null
    private var doubleTapListener: OnDoubleTapListener? = null
    var simpleRotaryDragMode: Boolean
    private var previousAngle = 0.0
    private var gestureDetector: GestureDetector? = null

    constructor(context: Context) : super(context) {
        simpleRotaryDragMode = false
        setupGestureDetector()
        setImageResource(R.drawable.magnetic_compass_ring)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        simpleRotaryDragMode = false
        setupGestureDetector()
        setImageResource(R.drawable.magnetic_compass_ring)
    }

    private fun setupGestureDetector() {
        gestureDetector = GestureDetector(context, object : SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                this@RotaryView.onDoubleTap()
                return super.onDoubleTap(e)
            }
        })
    }

    fun rotate(delta: Double) {
        var angle = rotation
        angle += delta.toFloat()
        rotation = angle
        onRotate(delta)
    }

    private fun onRotate(delta: Double) {
        if (rotateListener != null) rotateListener!!.onRotate(delta)
    }

    private fun onDoubleTap() {
        if (doubleTapListener != null) doubleTapListener!!.onDoubleTap()
    }

    fun setOnRotateListener(listener: OnRotateListener?) {
        rotateListener = listener
    }

    fun setOnDoubleTapListener(listener: OnDoubleTapListener?) {
        doubleTapListener = listener
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector!!.onTouchEvent(event)
        return if (simpleRotaryDragMode) {
            onTouchEventSimpleMode(event)
        } else {
            onTouchEventComplexMode(event)
        }
    }

    fun onTouchEventSimpleMode(event: MotionEvent): Boolean {
        // The rotation follows the finger position directly. Wherever you tap the pointer will point to.
        if (event.action == MotionEvent.ACTION_MOVE) {
            val delta =
                ensureAngleRange(getAngle(event.x, event.y))
            rotate(delta)
        }
        return true
    }

    fun onTouchEventComplexMode(event: MotionEvent): Boolean {
        // The rotations changes as the finger changes. You can move the pointer from other positions by swiping.
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> previousAngle =
                rotation + getAngle(event.x, event.y)
            MotionEvent.ACTION_MOVE -> {
                val alpha = rotation + getAngle(event.x, event.y)
                val delta =
                    ensureAngleRange(alpha - previousAngle)
                previousAngle = alpha
                rotate(delta)
            }
        }
        return true
    }

    private fun getAngle(x: Float, y: Float): Double {
        val cx = (width shr 1.toFloat().toInt()).toFloat()
        val cy = (height shr 1.toFloat().toInt()).toFloat()
        return Math.atan2(
            y - cy.toDouble(),
            x - cx.toDouble()
        ) * 180 / Math.PI + 90
    }

    companion object {
        private fun ensureAngleRange(delta: Double): Double {
            var w = delta
            while (w > 180) {
                w -= 360.0
            }
            while (w < -180) {
                w += 360.0
            }
            return w
        }
    }
}