package com.stho.nyota.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.*
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import com.stho.nyota.ISkyViewListener
import com.stho.nyota.sky.universe.*
import com.stho.nyota.sky.utilities.SphereProjection
import com.stho.nyota.sky.utilities.Ten
import com.stho.nyota.sky.utilities.Topocentric
import com.stho.nyota.ui.sky.ISkyViewOptions
import com.stho.nyota.ui.sky.SkyViewOptions
import java.util.*
import kotlin.math.abs


class SkyDrawColors: ISkyDrawColors {

    override val gridColor: Paint = Paint().apply {
        color = Color.BLUE
        alpha = 200
        style = Paint.Style.FILL_AND_STROKE
        isAntiAlias = true
        textSize = 40f
    }

    override val starColor: Paint = Paint().apply {
        color = Color.WHITE
        alpha = 255
        style = Paint.Style.FILL_AND_STROKE
        isAntiAlias = true
    }

    override val bitmapColor: Paint = Paint().apply {
        color = Color.WHITE
        alpha = 255
        style = Paint.Style.FILL_AND_STROKE
        isAntiAlias = true
    }

    override val lineColor: Paint = Paint().apply {
        color = Color.YELLOW
        alpha = 120
        style = Paint.Style.STROKE
    }

    override val starSymbolColor: Paint = Paint().apply {
        color = Color.GRAY
        alpha = 200
        style = Paint.Style.FILL_AND_STROKE
        isAntiAlias = true
        textSize = 40f
    }

    override val starNameColor: Paint = Paint().apply {
        color = Color.rgb(253, 106, 2) // Orange
        alpha = 120
        style = Paint.Style.FILL_AND_STROKE
        isAntiAlias = true
        textSize = 40f
    }

    override val constellationNameColor: Paint = Paint().apply {
        color = Color.rgb(253, 106, 2) // Orange
        alpha = 120
        style = Paint.Style.FILL_AND_STROKE
        isAntiAlias = true
        textSize = 40f
    }

    override val planetNameColor: Paint  = Paint().apply {
        color = Color.rgb(253, 106, 2) // Orange
        alpha = 120
        style = Paint.Style.FILL_AND_STROKE
        isAntiAlias = true
        textSize = 40f
    }

    override val targetNameColor: Paint  = Paint().apply {
        color = Color.rgb(253, 106, 2) // Orange
        alpha = 120
        style = Paint.Style.FILL_AND_STROKE
        isAntiAlias = true
        textSize = 40f
    }

    override val specialNameColor: Paint  = Paint().apply {
        color = Color.rgb(253, 106, 2) // Orange
        alpha = 120
        style = Paint.Style.FILL_AND_STROKE
        isAntiAlias = true
        textSize = 40f
    }

    override val referenceColor: Paint  = Paint().apply {
        color = Color.rgb(230, 20, 20) // Red
        alpha = 120
        style = Paint.Style.FILL_AND_STROKE
        isAntiAlias = true
        textSize = 40f
    }
}

abstract class AbstractSkyView(context: Context?, attrs: AttributeSet?): View(context, attrs), View.OnDragListener {

    abstract val options: ISkyViewOptions

    private val bitmaps = HashMap<Int, Bitmap>()
    private var colors = SkyDrawColors()

    val path = Path()
    val center = Topocentric(0.0, 0.0)
    var scaleGestureDetector: ScaleGestureDetector? = null
    var gestureDetector: GestureDetector? = null
    private val projection = SphereProjection()
    private var draw = SkyDraw(projection)
    var isScrollingEnabled: Boolean = true
    var isScalingEnabled: Boolean = true

    private var listener: ISkyViewListener? = null

    init {
        onCreate(context)
    }

    fun registerListener(listener: ISkyViewListener?) {
        this.listener = listener
    }

    private fun onCreate(context: Context?) {
        scaleGestureDetector =
            ScaleGestureDetector(context, object : SimpleOnScaleGestureListener() {
                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    applyScale(detector.scaleFactor.toDouble())
                    return true
                }
            })
        gestureDetector = GestureDetector(context, object : SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                resetTransformation()
                return false
            }

            override fun onScroll(
                e1: MotionEvent,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                applyScrolling(distanceX.toDouble(), distanceY.toDouble())
                return false // super.onScroll(e1, e2, distanceX, distanceY);
            }

            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                listener?.apply {
                    onSingleTapConfirmed(e)
                }
                return super.onSingleTapConfirmed(e)
            }
        })
    }

    open fun raiseOnChangeSkyCenter() {
        listener?.apply {
            onChangeSkyCenter()
        }
    }

    open fun applyScrolling(dx: Double, dy: Double) {
        if (isScrollingEnabled) {
            center.azimuth += projection.calculateAngle(dx)
            center.altitude -= projection.calculateAngle(dy)
            raiseOnChangeSkyCenter()
            invalidate()
        }
    }

    open fun applyScale(scaleFactor: Double) {
        if (isScalingEnabled) {
            options.applyScale(scaleFactor)
        }
    }

    open fun resetTransformation() {
        options.zoomAngle = SkyViewOptions.DEFAULT_ZOOM_ANGLE
        referencePosition?.apply {
            center.azimuth = azimuth
            center.altitude = altitude
        }
        raiseOnChangeSkyCenter()
        invalidate()
    }

    override fun onDrag(p0: View?, p1: DragEvent?): Boolean {
        return false
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        scaleGestureDetector?.onTouchEvent(event)
        gestureDetector?.onTouchEvent(event)
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.translate(width / 2f, height / 2f)

        projection.setZoom(options.zoomAngle, width)
        projection.setCenter(center.azimuth, center.altitude)

        draw.configure(canvas, width, height, center)
        draw.options = options
        draw.colors = colors

        if (options.drawGrid) {
            draw.drawGrid()
        }

        onDrawElements()
    }

    protected abstract fun onDrawElements()

    protected abstract val referencePosition: Topocentric?

    fun setCenter(position: Topocentric?) =
        position?.apply {
            setCenter(azimuth, altitude)
        }

    private fun setCenter(azimuth: Double, altitude: Double) {
        if (azimuth != center.azimuth || altitude != center.altitude) {
            center.azimuth = azimuth
            center.altitude = altitude
            invalidate()
        }
    }

    protected fun drawStar(star: Star) =
        draw.drawStar(star)

    protected fun drawSun(sun: IElement) =
        draw.drawSun(sun, getScaledBitmap(sun.imageId, 72, 72))

    protected fun drawMoon(moon: IElement) =
        draw.drawMoon(moon, getScaledBitmap(moon.imageId, 48, 48))

    protected fun drawPlanet(planet: IElement) =
        draw.drawPlanet(planet, getScaledBitmap(planet.imageId, 32, 32))

    protected fun drawTarget(target: com.stho.nyota.sky.universe.Target) =
        draw.drawTarget(target, getScaledBitmap(target.imageId, 16, 16))

    protected fun drawSpecial(special: SpecialElement) =
        draw.drawSpecial(special)

    protected fun drawConstellation(constellation: Constellation) =
        draw.drawConstellation(constellation)

    protected fun drawName(position: Topocentric?, name: String) =
        draw.drawName(position, name)

    protected fun drawStarAsReference(star: Star) =
        draw.drawStar(star, true)

    protected fun drawConstellationAsReference(constellation: Constellation) =
        draw.drawConstellation(constellation, true)
    /**
    get image from cache or create it
     */
    private fun getScaledBitmap(resourceId: Int, newWidth: Int, newHeight: Int): Bitmap =
        bitmaps[resourceId] ?: createScaledBitmapIntoCache(resourceId, newWidth, newHeight)

    private fun createScaledBitmapIntoCache(resourceId: Int, newWidth: Int, newHeight: Int): Bitmap =
        createScaledBitmap(resourceId, newWidth, newHeight).also {
            bitmaps[resourceId] = it
        }

    private fun createScaledBitmap(resourceId: Int, newWidth: Int, newHeight: Int): Bitmap =
        BitmapFactory.decodeResource(resources, resourceId).let {
            Bitmap.createScaledBitmap(it, newWidth, newHeight, false)
        }


}


