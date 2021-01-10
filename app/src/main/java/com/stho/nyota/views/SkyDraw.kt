package com.stho.nyota.views

import android.graphics.*
import com.stho.nyota.sky.universe.*
import com.stho.nyota.sky.utilities.SphereProjection
import com.stho.nyota.sky.utilities.Ten
import com.stho.nyota.sky.utilities.Topocentric
import com.stho.nyota.ui.sky.ISkyViewOptions
import java.util.HashMap
import kotlin.math.abs

interface ISkyDrawColors {
    val gridColor: Paint // Blue
    val starColor: Paint // White
    val bitmapColor: Paint // White
    val lineColor: Paint // Yellow
    val starSymbolColor: Paint // Gray
    val starNameColor: Paint // Orange
    val constellationNameColor: Paint // Orange
    val planetNameColor: Paint // Orange
    val targetNameColor: Paint // Orange
    val specialNameColor: Paint // Orange
    val referenceColor: Paint // Rose // TODO Colors ...
}

class SkyDraw(val projection: SphereProjection) {

    private val positions = HashMap<IElement, PointF>()

    lateinit var canvas: Canvas
    lateinit var center: Topocentric
    lateinit var colors: ISkyDrawColors
    lateinit var options: ISkyViewOptions

    private val path = Path()

    var width: Int = 0
    var height: Int = 0

    fun configure(canvas: Canvas, width: Int, height: Int, center: Topocentric) {
        this.canvas = canvas
        this.width = width
        this.height = height
        this.center = center
        positions.clear()
    }

    fun drawSun(sun: IElement, bitmap: Bitmap) =
        getPosition(sun)?.let {
            if (isOnScreen(it)) {
                drawImageAt(bitmap, it)
                if (options.displayPlanetNames) {
                    drawNameAt(sun.name, colors.planetNameColor, it)
                }
            }
        }

    fun drawMoon(moon: IElement, bitmap: Bitmap) =
        getPosition(moon)?.let {
            if (isOnScreen(it)) {
                drawImageAt(bitmap, it)
                if (options.displayPlanetNames) {
                    drawNameAt(moon.name, colors.planetNameColor, it)
                }
            }
        }

    fun drawPlanet(planet: IElement, bitmap: Bitmap) =
        getPosition(planet)?.let {
            if (isOnScreen(it)) {
                drawImageAt(bitmap, it)
                if (options.displayPlanetNames) {
                    drawNameAt(planet.name, colors.planetNameColor, it)
                }
            }
        }

    fun drawTarget(target: com.stho.nyota.sky.universe.Target, bitmap: Bitmap) =
        getPosition(target)?.let {
            if (isOnScreen(it)) {
                drawImageAt(bitmap, it)
                drawNameAt(target.name, colors.targetNameColor, it)
            }
        }

    fun drawSpecial(special: SpecialElement) =
        getPosition(special)?.let {
            if (isOnScreen(it)) {
                drawNameAt(special.name, colors.specialNameColor, it)
            }
        }

    fun drawStar(star: Star, isReference: Boolean = false) =
        getPosition(star)?.let {
            if (isOnScreen(it)) {
                if (isReference) {
                    val r = 5f
                    drawCircleAt(r, colors.referenceColor, it)
                    if (options.displaySymbols) {
                        if (options.displaySymbols) {
                            drawNameAt(UniverseInitializer.greekSymbolToString(star.symbol), colors.referenceColor, it);
                        }
                        if (options.displayStarNames) {
                            drawNameAt(star.name, colors.referenceColor, it)
                        }
                    }
                } else {
                    if (star.isBrighterThan(options.magnitude)) {
                        var r = 4f
                        if (options.displayMagnitude) {
                            colors.starColor.alpha = getStarAlpha(star.magn)
                            r = getStarSize(star.magn)
                        }
                        drawCircleAt(r, colors.starColor, it)
                        if (options.displaySymbols) {
                            drawNameAt(UniverseInitializer.greekSymbolToString(star.symbol), colors.starSymbolColor, it);
                        }
                        if (options.displayStarNames) {
                            drawNameAt(star.name, colors.starNameColor, it)
                        }
                    }
                }
            }
        }

    fun drawConstellation(constellation: Constellation, isReference: Boolean = false) {
        if (isReference) {
            for (star: Star in constellation.stars) {
                drawStar(star, true);
            }
            for (line in constellation.lines) {
                drawLine(line, colors.referenceColor);
            }
            if (options.displayConstellationNames) {
                drawName(constellation.position, constellation.name, colors.referenceColor)
            }
        } else {
            for (star: Star in constellation.stars) {
                drawStar(star, false);
            }
            if (options.displayConstellations) {
                for (line in constellation.lines) {
                    drawLine(line, colors.lineColor);
                }
            }
            if (options.displayConstellationNames) {
                drawName(constellation.position, constellation.name, colors.constellationNameColor)
            }
        }
    }

    fun drawName(position: Topocentric?, name: String) =
        drawName(position, name, colors.specialNameColor)

    private fun drawName(position: Topocentric?, name: String, color: Paint) =
        calculatePosition(position)?.let {
            if (isOnScreen(it)) {
                drawNameAt(name, color, it)
            }
        }

    fun drawGrid() {
        val c = gridCenter
        for (x in 0 until 180 step 10) {
            drawGridPointsUpwards(c, x, colors)
            drawGridPointsDownwards(c, x, colors)
        }
    }

    private fun drawCircleAt(r: Float, color: Paint, p: PointF) {
        canvas.drawCircle(p.x, p.y, r, color);
    }

    private fun drawImageAt(bitmap: Bitmap, p: PointF) {
        val dx: Int = bitmap.width / 2
        val dy: Int = bitmap.height / 2
        canvas.drawBitmap(bitmap, p.x - dx, p.y - dy, colors.bitmapColor)
    }

    private fun drawLine(line: Array<out Star>, color: Paint) {
        path.reset();
        var first = true;
        for (star: Star in line) {
            getPosition(star)?.let {
                if (isOnScreen(it)) {
                    if (first) {
                        first = false;
                        path.moveTo(it.x, it.y);
                    } else {
                        path.lineTo(it.x, it.y);
                    }
                } else {
                    first = true;
                }
            }
        }
        canvas.drawPath(path, color);
    }

    private fun drawNameAt(name: String, color: Paint, p: PointF) {
        canvas.drawText(name, p.x + 10, p.y - 10, color)
    }

    private fun drawGridPointsUpwards(c: Topocentric, x: Int, colors: ISkyDrawColors) {
        val azimuth = c.azimuth + x
        for (y in 0 until 90 step 5) {
            val altitude: Double = c.altitude + y
            calculatePosition(azimuth, altitude)?.let {
                if (isOnScreen(it)) {
                    canvas.drawCircle(it.x, it.y, 2f, colors.gridColor)
                    canvas.drawCircle(-it.x, it.y, 2f, colors.gridColor)
                } else {
                    return
                }
            }
        }
    }

    private fun drawGridPointsDownwards(c: Topocentric, x: Int, colors: ISkyDrawColors) {
        val azimuth = c.azimuth + x
        for (y in 0 until 90 step 5) {
            val altitude: Double = c.altitude - y
            calculatePosition(azimuth, altitude)?.let {
                if (isOnScreen(it)) {
                    canvas.drawCircle(it.x, it.y, 2f, colors.gridColor);
                    canvas.drawCircle(-it.x, it.y, 2f, colors.gridColor);
                } else {
                    return
                }
            }
        }
    }

    private fun getStarAlpha(magnitude: Double) =
        when {
            magnitude > 6.0 -> 100
            magnitude > 5.0 -> 150
            magnitude > 4.0 -> 200
            else -> 255
        }

    private fun getStarSize(magnitude: Double): Float =
        when {
            magnitude > 3.0 -> 2f
            magnitude > 2.0 -> 3f
            magnitude > 1.0 -> 4f
            magnitude > 0.0 -> 5f
            else -> 6f
        }

    private fun isOnScreen(p: PointF): Boolean =
        (abs(p.x) < width) && (abs(p.y) < height)

    private val gridCenter: Topocentric
        get() = Topocentric(Ten.nearestTen(center.azimuth), Ten.nearestTen(center.altitude))

    private fun getPosition(element: IElement): PointF? =
        positions[element] ?: calculatePosition(element.position)?.also { positions[element] = it }

    private fun calculatePosition(topocentric: Topocentric?): PointF? =
        topocentric?.let {
            projection.calculateZoomImagePoint(it.azimuth, it.altitude)
        }

    private fun calculatePosition(azimuth: Double, altitude: Double): PointF? =
        projection.calculateZoomImagePoint(azimuth, altitude)


}