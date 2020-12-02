package com.stho.nyota.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.*
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import com.stho.nyota.ISkyViewListener
import com.stho.nyota.sky.universe.Constellation
import com.stho.nyota.sky.universe.IElement
import com.stho.nyota.sky.universe.Star
import com.stho.nyota.sky.universe.Target
import com.stho.nyota.sky.utilities.Degree
import com.stho.nyota.sky.utilities.SphereProjection
import com.stho.nyota.sky.utilities.Topocentric
import java.util.*
import kotlin.math.abs

abstract class AbstractSkyView(context: Context?, attrs: AttributeSet?): View(context, attrs), View.OnDragListener {
    val green = Paint()
    val white = Paint()
    val yellow = Paint()
    val gray = Paint()
    val blue = Paint()
    val orange = Paint()
    val path = Path()
    val positions = HashMap<Star, PointF>()
    val bitmaps = HashMap<Int, Bitmap>()
    var displayNames = true
    var displaySymbols = true
    var displayMagnitude = true
    val center = Topocentric(0.0, 0.0)
    var scaleGestureDetector: ScaleGestureDetector? = null
    var gestureDetector: GestureDetector? = null
    var zoomAngle = 45.0
        set(value) {
            field = value
            invalidate()
        }
    val projection = SphereProjection()
    var isScrollingEnabled: Boolean = true
    var isScalingEnabled: Boolean = true
    val drawGrid = true
    private var listener: ISkyViewListener? = null

    interface ISkyViewSettings {
        val displayNames: Boolean
        val displaySymbols: Boolean
        val displayMagnitude: Boolean
    }

    init {
        onCreate(context)
    }

    fun registerListener(listener: ISkyViewListener?) {
        this.listener = listener
    }

    fun loadSettings(settings: ISkyViewSettings) {
        displayNames = settings.displayNames
        displaySymbols = settings.displaySymbols
        displayMagnitude = settings.displayMagnitude
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
        yellow.color = Color.YELLOW
        yellow.alpha = 120
        yellow.style = Paint.Style.STROKE
        green.color = Color.GREEN
        green.alpha = 200
        green.style = Paint.Style.FILL_AND_STROKE
        green.isAntiAlias = true
        green.textSize = 50f
        white.color = Color.WHITE
        white.alpha = 255
        white.style = Paint.Style.FILL_AND_STROKE
        white.isAntiAlias = true
        gray.color = Color.GRAY
        gray.alpha = 200
        gray.style = Paint.Style.FILL_AND_STROKE
        gray.isAntiAlias = true
        gray.textSize = 40f
        blue.color = Color.BLUE
        blue.alpha = 200
        blue.style = Paint.Style.FILL_AND_STROKE
        blue.isAntiAlias = true
        blue.textSize = 40f
        orange.color = Color.rgb(253, 106, 2)
        orange.alpha = 120
        orange.style = Paint.Style.FILL_AND_STROKE
        orange.isAntiAlias = true
        orange.textSize = 40f
    }

    open fun raiseOnChangeSkyCenter() {
        listener?.apply {
            onChangeSkyCenter()
        }
    }

    open fun applyScrolling(dx: Double, dy: Double) {
        if (isScrollingEnabled) {
            val zoom: Double = getZoom()
            center.azimuth += Degree.arcTan2(dx, zoom)
            center.altitude -= Degree.arcTan2(dy, zoom)
            raiseOnChangeSkyCenter()
            invalidate()
        }
    }

    open fun applyScale(scaleFactor: Double) {
        if (isScalingEnabled) {
            val MIN_ZOOM_ANGLE = 0.5
            val MAX_ZOOM_ANGLE = 120.0
            zoomAngle = Math.max(
                MIN_ZOOM_ANGLE,
                Math.min(MAX_ZOOM_ANGLE, zoomAngle / scaleFactor)
            )
            invalidate()
        }
    }

    open fun resetTransformation() {
        zoomAngle = 45.0
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

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas != null) {
            canvas.translate(width / 2f, height / 2f)
            val zoom: Double = getZoom()
            projection.setCenter(center.azimuth, center.altitude)
            positions.clear()
            if (drawGrid) {
                drawGrid(canvas, width, height, zoom)
            }
            onDrawElements(canvas, zoom)
        }
    }

    protected abstract fun onDrawElements(canvas: Canvas, zoom: Double)

    protected abstract val referencePosition: Topocentric?

    fun setCenter(position: Topocentric?) {
        position?.apply {
            setCenter(azimuth, altitude)
        }
    }

    fun setCenter(azimuth: Double, altitude: Double) {
        if (azimuth != center.azimuth || altitude != center.altitude) {
            center.azimuth = azimuth
            center.altitude = altitude
            invalidate()
        }
    }

    protected fun getZoom(): Double {
        val w = width
        return 0.5 * w / Degree.tangent(0.5 * zoomAngle)
    }

    protected fun drawStar(canvas: Canvas, zoom: Double, star: Star) {
        calculatePosition(zoom, star)
        drawStar(canvas, star, white)
    }

    protected fun drawConstellation(canvas: Canvas, zoom: Double, constellation: Constellation) {
        calculatePositions(zoom, constellation)
        drawConstellation(canvas, constellation)
    }

    protected fun drawSun(canvas: Canvas, zoom: Double, sun: IElement) {
        val p = getPosition(zoom, sun.position)
        if (p != null && isOnScreen(p)) {
            val bm: Bitmap = getScaledBitmap(sun.imageId, 32, 32)
            canvas.drawBitmap(bm, p.x - 16, p.y - 16, white)
        }
    }

    protected fun drawMoon(canvas: Canvas, zoom: Double, moon: IElement) {
        val p = getPosition(zoom, moon.position)
        if (p != null && isOnScreen(p)) {
            val bm: Bitmap = getScaledBitmap(moon.imageId, 48, 48)
            canvas.drawBitmap(bm, p.x - 24, p.y - 24, white)
        }
    }

    protected fun drawPlanet(canvas: Canvas, zoom: Double, element: IElement) {
        val p = getPosition(zoom, element.position)
        if (p != null && isOnScreen(p)) {
            val bm: Bitmap = getScaledBitmap(element.imageId, 16, 16)
            canvas.drawBitmap(bm, p.x - 8, p.y - 8, white)
        }
    }

    protected fun drawName(canvas: Canvas, zoom: Double, element: IElement) {
        drawName(canvas, zoom, element.position, element.name)
    }

    protected fun drawName(canvas: Canvas, zoom: Double, position: Topocentric?, name: String?) {
        val p = getPosition(zoom, position)
        if (p != null && isOnScreen(p)) {
            canvas.drawText(name!!, p.x + 10, p.y - 10, orange)
        }
    }

    protected fun drawTarget(canvas: Canvas, zoom: Double, target: Target) {
        val p = getPosition(zoom, target.position)
        if (p != null && isOnScreen(p)) {
            val bm: Bitmap = getScaledBitmap(target.imageId, 64, 64)
            canvas.drawBitmap(bm, p.x - 32, p.y - 32, white)
            canvas.drawText(target.name, p.x + 40, p.y - 10, orange)
        }

    }

    private fun getScaledBitmap(resourceId: Int, newWidth: Int, newHeight: Int): Bitmap {
        return getScaledBitmapFromCache(resourceId) ?: createScaledBitmapIntoCache(resourceId, newWidth, newHeight)
    }

    private fun getScaledBitmapFromCache(resourceId: Int): Bitmap? {
        return if (bitmaps.containsKey(resourceId)) {
            bitmaps[resourceId]
        } else {
            null
        }
    }

    private fun createScaledBitmapIntoCache(resourceId: Int, newWidth: Int, newHeight: Int): Bitmap {
        val bm= createScaledBitmap(resourceId, newWidth, newHeight)
        bitmaps[resourceId] = bm
        return bm
    }

    private fun createScaledBitmap(resourceId: Int, newWidth: Int, newHeight: Int): Bitmap {
        val bm = BitmapFactory.decodeResource(resources, resourceId)
        return Bitmap.createScaledBitmap(bm, newWidth, newHeight, false)
    }

    private fun calculatePosition(zoom: Double, star: Star) {
        val p = getPosition(zoom, star.position)
        if (p != null) {
            positions[star] = p
        }
    }

    private fun calculatePositions(zoom: Double, constellation: Constellation) {
        for (star in constellation.stars) {
            val p = getPosition(zoom, star.position)
            if (p != null) {
                positions[star] = p
            }
        }
    }

    private fun getPosition(zoom: Double, topocentric: Topocentric?): PointF? {
        return getPosition(zoom, topocentric!!.azimuth, topocentric.altitude)
    }

    private fun getPosition(zoom: Double, azimuth: Double, altitude: Double): PointF? {
        val p = projection.getImagePoint(azimuth, altitude)
        if (p != null) {
            val x = (zoom * p.x).toFloat()
            val y = (zoom * p.y).toFloat()
            return PointF(x, -y)
        }
        return null
    }

    private fun drawConstellation(canvas: Canvas, constellation: Constellation) {
        for (star: Star in constellation.stars) {
            drawStar(canvas, star, green);
        }
        for (line in constellation.lines) {
            drawLine(canvas, line);
        }
    }

     private fun drawStar(canvas: Canvas, star: Star, color: Paint) {
        val p = positions.get(star)
        if (p != null && isOnScreen(p)) {
            var r = 4f
            if (displayMagnitude) {
                r = applyMagnitude(color, star.brightness);
            }
            canvas.drawCircle(p.x, p.y, r, color);
            if (displaySymbols) {
                canvas.drawText(star.symbol, p.x, p.y, gray);
            }
        }
    }

    private fun applyMagnitude(color: Paint, magnitude: Double): Float {
        when {
            (magnitude > 5.0) -> {
                color.alpha = 100
                return 3f
            }
            (magnitude > 3.0) -> {
                color.alpha = 150
                return 4f
            }
            (magnitude > 1.0) -> {
                color.alpha = 200
                return 5f
            }
            else -> {
                color.alpha = 255
                return 6f
            }
        }
    }

    private fun drawLine(canvas: Canvas, line: Array<out Star>) {
        var first: Boolean = true;
        path.reset();
        for (star: Star in line) {
            val p= positions.get(star)
            if (p != null && isOnScreen(p)) {
                if (first) {
                    path.moveTo(p.x, p.y);
                    first = false;
                } else {
                    path.lineTo(p.x, p.y);
                }
            } else {
                first = true;
            }
        }
        canvas.drawPath(path, yellow);
    }

    private fun drawGrid(canvas: Canvas, w: Int, h: Int, zoom: Double) {
        if (center.altitude > 0) {
            for (x in 0 until 180 step 10) {
                val azimuth: Double = center.azimuth + x
                for (y in Companion.downwards) {
                    val altitude: Double = 0.0 + y
                    val p: PointF? = getPosition(zoom, azimuth, altitude)
                    if (p != null) {
                        if (p.x > w || p.y > h) {
                            break
                        }
                        if (isOnScreen(p)) {
                            canvas.drawCircle(p.x, p.y, 2f, blue)
                            canvas.drawCircle(-p.x, p.y, 2f, blue)
                        }
                    }
                }
            }
        } else {
            for (x in 0 until 180 step 10) {
                val azimuth: Double = center.azimuth + x
                for (y in Companion.upwards) {
                    val altitude: Double = 0.0 + y
                    val p: PointF? = getPosition(zoom, azimuth, altitude)
                    if (p != null) {
                        if (p.x > w || p.y < -h) {
                            break;
                        }
                        if (isOnScreen(p)) {
                            canvas.drawCircle(p.x, p.y, 2f, blue);
                            canvas.drawCircle(-p.x, p.y, 2f, blue);
                        }
                    }
                }
            }
        }
    }

    private fun isOnScreen(p: PointF): Boolean {
        return (abs(p.x) < width) && (abs(p.y) < height);
    }

    companion object {
        private val downwards = listOf(85, 80, 75, 70, 65, 60, 55, 50, 45, 40, 35, 30, 25, 20, 15, 10, 5, 0, -5, -10, -15, -20, -25, -30, -35, -40, -45, -50, -55, -60, -65, -70, -75, -80, -85)
        private val upwards = listOf(-85, -80, -75, -70, -65, -60, -55, -50, -45, -40, -35, -30, -25, -20, -15, -10, -5, -0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85)
    }
}


