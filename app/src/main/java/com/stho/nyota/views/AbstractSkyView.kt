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
import com.stho.nyota.settings.Settings
import com.stho.nyota.sky.universe.*
import com.stho.nyota.sky.utilities.Degree
import com.stho.nyota.sky.utilities.Topocentric
import com.stho.nyota.sky.utilities.projections.ISphereProjection
import com.stho.nyota.sky.utilities.projections.SphereProjection
import com.stho.nyota.ui.sky.IViewOptions
import java.util.*


abstract class AbstractSkyView(context: Context?, attrs: AttributeSet?): View(context, attrs), View.OnDragListener {

    lateinit var options: IViewOptions
        private set


    private val bitmaps = HashMap<Int, Bitmap>()
    private var projection: ISphereProjection = SphereProjection()
    private var draw: SkyDraw = SkyDraw()

    fun setOptions(options: IViewOptions) {
        this.options = options
        this.draw.touch()
        invalidate()
    }

    fun touch() {
        this.draw.touch()
        invalidate()
    }

    val path = Path()
    val center = Topocentric(0.0, 0.0)
    var zoomAngle: Double = Settings.DEFAULT_ZOOM_ANGLE
        set(value) {
            val validZoomAngle = value.coerceIn(Settings.MIN_ZOOM_ANGLE, Settings.MAX_ZOOM_ANGLE)
            if (field != validZoomAngle) {
                field = validZoomAngle
                touch()
            }
        }

    var scaleGestureDetector: ScaleGestureDetector? = null
    var gestureDetector: GestureDetector? = null
    var tippedPosition: Topocentric? = null

    private var listener: ISkyViewListener? = null

    init {
        onCreate(context)
    }

    val sensitivityAngle: Double
        get() {
            val metrics: DisplayMetrics = resources.displayMetrics
            val dp: Double = metrics.density * 17.0
            return projection.sensitivityAngle * dp
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
                        tippedPosition = it
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
        center.azimuth = Degree.normalize(center.azimuth + projection.calculateAngle(dx))
        center.altitude = (center.altitude - projection.calculateAngle(dy)).coerceIn(-90.0, 90.0)
        tippedPosition = null
        raiseOnChangeCenter()
        invalidate()
    }

    open fun applyScale(scaleFactor: Double) {
        zoomAngle /= scaleFactor
        tippedPosition = null
        raiseOnChangeZoom()
    }

    open fun resetTransformation() {
        tippedPosition = null
        zoomAngle = Settings.DEFAULT_ZOOM_ANGLE
        raiseOnChangeZoom()
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
        projection.setZoom(zoomAngle, width)
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

    protected fun drawLight() =
        draw.drawLight()

    protected fun drawEcliptic(ecliptic: Collection<Topocentric>) =
        draw.drawEcliptic(ecliptic)

    protected fun drawSun(sun: IElement) =
        draw.drawSun(sun, getScaledBitmap(sun.imageId, 72, 72))

    protected fun drawMoon(moon: IElement) =
        draw.drawMoon(moon, getScaledBitmap(moon.imageId, 48, 48))

    protected fun drawPlanet(planet: IElement) =
        draw.drawPlanet(planet, getScaledBitmap(planet.imageId, 32, 32))

    protected fun drawTarget(target: com.stho.nyota.sky.universe.Target) =
        draw.drawTarget(target, getScaledBitmap(target.imageId, 16, 16))

    protected fun drawNameOf(element: IElement) =
        draw.drawNameOf(element)

    protected fun drawGalaxy(galaxy: Galaxy, referenceType: ReferenceType) =
        draw.drawGalaxy(galaxy, referenceType)

    protected fun drawZenit(zenit: Topocentric) =
        draw.drawZenit(zenit)

    protected fun drawNadir(nadir: Topocentric) =
        draw.drawNadir(nadir)

    protected fun drawStar(star: Star, referenceType: ReferenceType) =
        draw.drawStar(star, referenceType)

    protected fun drawConstellation(constellation: Constellation, referenceType: ReferenceType) =
        draw.drawConstellation(constellation, referenceType)

    protected fun drawSatellite(satellite: Satellite) =
        draw.drawSatellite(satellite, getScaledBitmap(satellite.imageId, 72, 72))

    protected fun drawName(position: Topocentric, name: String) =
        draw.drawName(position, name)

    protected fun drawHint(hint: Hint) =
        draw.drawHint(hint)

    protected fun drawElement(position: Topocentric, name: String, luminosity: Luminosity) =
        draw.drawElement(position, name, luminosity)

    fun drawSensitivityArea(position: Topocentric) =
        draw.drawSensitivityArea(position, sensitivityAngle)

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



