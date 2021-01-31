package com.stho.nyota.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Path
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.*
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import com.stho.nyota.ISkyViewListener
import com.stho.nyota.sky.universe.*
import com.stho.nyota.sky.utilities.Topocentric
import com.stho.nyota.sky.utilities.projections.ISphereProjection
import com.stho.nyota.sky.utilities.projections.SphereProjection
import com.stho.nyota.ui.sky.ISkyViewOptions
import com.stho.nyota.ui.sky.SkyViewOptions
import java.util.*


abstract class AbstractSkyView(context: Context?, attrs: AttributeSet?): View(context, attrs), View.OnDragListener {

    lateinit var options: ISkyViewOptions
        private set


    private val bitmaps = HashMap<Int, Bitmap>()
    private var projection: ISphereProjection = SphereProjection()
    private var draw: SkyDraw = SkyDraw()

    fun setOptions(options: ISkyViewOptions) {
        this.options = options
    }

    val path = Path()
    val center = Topocentric(0.0, 0.0)

    var scaleGestureDetector: ScaleGestureDetector? = null
    var gestureDetector: GestureDetector? = null

    var isScrollingEnabled: Boolean = true
    var isScalingEnabled: Boolean = true

    private var listener: ISkyViewListener? = null

    init {
        onCreate(context)
    }

    val sensitivityAngle: Double
        get() {
            val metrics: DisplayMetrics = resources.displayMetrics
            val dp: Double = metrics.density * 13.0
            return projection.sensibilityAngle * dp
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
                listener?.apply {
                    onDoubleTap() // shall call: SkyView.resetTransformation()
                }
                return false
            }

            override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
                applyScrolling(distanceX.toDouble(), distanceY.toDouble())
                return false // super.onScroll(e1, e2, distanceX, distanceY);
            }

            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                listener?.apply {
                    val p = SkyPointF.forMotionEvent(e, width, height)
                    projection.inverseZoomImagePoint(p)?.let {
                        onSingleTap(it);
                    }
                }
                return super.onSingleTapConfirmed(e)
            }
        })
    }

    open fun raiseOnChangeCenter() {
        listener?.apply {
            onChangeCenter()
        }
    }

    open fun raiseOnChangeZoom() {
        listener?.apply {
            onChangeZoom()
        }
    }

    open fun applyScrolling(dx: Double, dy: Double) {
        if (isScrollingEnabled) {
            center.azimuth += projection.calculateAngle(dx)
            center.altitude -= projection.calculateAngle(dy)
            raiseOnChangeCenter()
            invalidate()
        }
    }

    open fun applyScale(scaleFactor: Double) {
        if (isScalingEnabled) {
            options.applyScale(scaleFactor)
            raiseOnChangeZoom()
        }
    }

    open fun resetTransformation() {
        options.zoomAngle = SkyViewOptions.DEFAULT_ZOOM_ANGLE
        referencePosition?.apply {
            center.azimuth = azimuth
            center.altitude = altitude
        }
        raiseOnChangeCenter()
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

        ensureProjection()
        ensureDraw(canvas)

        if (options.displayGrid) {
            draw.drawGrid()
        }

        onDrawElements()
    }

    private fun ensureProjection() {
        if (projection.projection != options.sphereProjection) {
            projection = ISphereProjection.create(options.sphereProjection)
        }
        projection.setZoom(options.zoomAngle, width)
        projection.setCenter(center.azimuth, center.altitude)
    }

    private fun ensureDraw(canvas: Canvas) {
        draw.configure(canvas, width, height, center)
        draw.options = options
        draw.projection = projection
    }

    protected abstract fun onDrawElements()

    protected abstract val referencePosition: Topocentric?

    fun setCenter(position: Topocentric?) =
        position?.apply {
            setCenter(azimuth, altitude)
        }

    fun setCenter(azimuth: Double, altitude: Double) {
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

    protected fun drawZenit(zenit: Topocentric) =
        draw.drawZenit(zenit)

    protected fun drawStar(star: Star, referenceType: ReferenceType = ReferenceType.Reference) =
        draw.drawStar(star, referenceType)

    protected fun drawConstellation(constellation: Constellation, referenceType: ReferenceType = ReferenceType.Reference) =
        draw.drawConstellation(constellation, referenceType)

    protected fun drawSatellite(satellite: Satellite) =
        draw.drawSatellite(satellite, getScaledBitmap(satellite.imageId, 72, 72))

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



