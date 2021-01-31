package com.stho.nyota.views

import android.graphics.*
import com.stho.nyota.sky.universe.*
import com.stho.nyota.sky.utilities.Ten
import com.stho.nyota.sky.utilities.Topocentric
import com.stho.nyota.sky.utilities.projections.ISphereProjection
import com.stho.nyota.ui.sky.ISkyViewOptions
import java.util.*
import kotlin.math.abs


class SkyDraw() {

    private val positions: HashMap<IElement, SkyPointF> = HashMap()
    private var colors: ISkyDrawColors = SkyDrawColors()

    lateinit var canvas: Canvas
    lateinit var center: Topocentric
    lateinit var options: ISkyViewOptions
    lateinit var projection: ISphereProjection

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
                    drawNameAt(sun.name, colors.nameColor, it)
                }
            }
        }

    fun drawMoon(moon: IElement, bitmap: Bitmap) =
        getPosition(moon)?.let {
            if (isOnScreen(it)) {
                drawImageAt(bitmap, it)
                if (options.displayPlanetNames) {
                    drawNameAt(moon.name, colors.nameColor, it)
                }
            }
        }

    fun drawPlanet(planet: IElement, bitmap: Bitmap) =
        getPosition(planet)?.let {
            if (isOnScreen(it)) {
                drawImageAt(bitmap, it)
                if (options.displayPlanetNames) {
                    drawNameAt(planet.name, colors.nameColor, it)
                }
            }
        }

    fun drawTarget(target: com.stho.nyota.sky.universe.Target, bitmap: Bitmap) =
        getPosition(target)?.let {
            if (isOnScreen(it)) {
                drawImageAt(bitmap, it)
                drawNameAt(target.name, colors.nameColor, it)
            }
        }

    fun drawSpecial(special: SpecialElement) =
        getPosition(special)?.let {
            if (isOnScreen(it)) {
                drawNameAt(special.name, colors.nameColor, it)
            }
        }

    fun drawStar(star: Star, referenceType: ReferenceType = ReferenceType.None) =
        getPosition(star)?.let {
            if (isOnScreen(it)) {
                when (referenceType) {
                    ReferenceType.Reference -> {
                        if (star.isBrighterThan(options.magnitude)) {
                            var r = 3f
                            if (options.displayMagnitude) {
                                colors.referenceStarColor.alpha = getStarAlpha(star.magn)
                                r = getStarSize(star.magn)
                            }
                            drawCircleAt(r, colors.referenceStarColor, it)
                            if (star.hasFriendlyName) {
                                drawNameAt(star.friendlyName, colors.referenceNameColor, it)
                            } else if (star.hasSymbol) {
                                drawNameAt(star.symbol.greekSymbol, colors.referenceSymbolColor, it);
                            }
                        }
                    }
                    ReferenceType.Tipped -> {
                        if (star.isBrighterThan(options.magnitude)) {
                            var r = 3f
                            if (options.displayMagnitude) {
                                colors.tippedStarColor.alpha = getStarAlpha(star.magn)
                                r = getStarSize(star.magn)
                            }
                            drawCircleAt(r, colors.tippedStarColor, it)
                            if (star.hasFriendlyName) {
                                drawNameAt(star.friendlyName, colors.tippedStarColor, it)
                            } else if (star.hasSymbol) {
                                drawNameAt(star.symbol.greekSymbol, colors.tippedStarColor, it);
                            }
                        }
                    }
                    ReferenceType.None -> {
                        if (star.isBrighterThan(options.magnitude)) {
                            var r = 3f
                            if (options.displayMagnitude) {
                                colors.starColor.alpha = getStarAlpha(star.magn)
                                r = getStarSize(star.magn)
                            }
                            drawCircleAt(r, colors.starColor, it)
                            if (options.displayStarNames && star.hasFriendlyName) {
                                drawNameAt(star.friendlyName, colors.nameColor, it)
                            } else if (options.displaySymbols && star.hasSymbol) {
                                drawNameAt(star.symbol.greekSymbol, colors.symbolColor, it);
                            }
                        }
                    }
                }
            }
        }

    fun drawConstellation(constellation: Constellation, referenceType: ReferenceType = ReferenceType.None) {
        when (referenceType) {
            ReferenceType.Reference -> {
                for (star: Star in constellation.stars) {
                    drawStar(star, referenceType);
                }
                for (line in constellation.lines) {
                    drawLine(line, colors.referenceLineColor);
                }
                drawName(constellation.position, constellation.name, colors.referenceNameColor)
            }
            ReferenceType.Tipped -> {
                for (star: Star in constellation.stars) {
                    drawStar(star, referenceType);
                }
                for (line in constellation.lines) {
                    drawLine(line, colors.tippedLineColor);
                }
                drawName(constellation.position, constellation.name, colors.tippedStarColor)
            }
            ReferenceType.None -> {
                for (star: Star in constellation.stars) {
                    drawStar(star, referenceType);
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
    }

    fun drawZenit(zenit: Topocentric) =
        drawName(zenit, "Z", colors.visibleGridColor)

    fun drawSatellite(satellite: Satellite, bitmap: Bitmap) =
        calculatePosition(satellite.position)?.let {
            if (isOnScreen(it)) {
                drawImageAt(bitmap, it)
            }
        }

    private fun drawName(position: Topocentric?, name: String, color: Paint) =
        calculatePosition(position)?.let {
            if (isOnScreen(it)) {
                drawNameAt(name, color, it)
            }
        }

    fun drawGrid() {
        val c = gridCenter
        for (x in 0..90 step 15) { // once per hour
            drawGridPointsUpwards(c.azimuth + x, c.altitude)
            drawGridPointsUpwards(c.azimuth - x, c.altitude)
            drawGridPointsDownwards(c.azimuth + x, c.altitude)
            drawGridPointsDownwards(c.azimuth - x, c.altitude)
        }
    }

    private fun drawCircleAt(r: Float, color: Paint, p: SkyPointF) {
        canvas.drawCircle(p.x, p.y, r, color);
    }

    private fun drawImageAt(bitmap: Bitmap, p: SkyPointF) {
        val dx: Int = bitmap.width / 2
        val dy: Int = bitmap.height / 2
        canvas.drawBitmap(bitmap, p.x - dx, p.y - dy, colors.bitmapColor)
    }

    private fun drawLine(line: Collection<Star>, color: Paint) {
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

    private fun drawNameAt(name: String, color: Paint, p: SkyPointF) {
        canvas.drawText(name, p.x + 10, p.y - 10, color)
    }

    private fun drawGridPointsUpwards(azimuth: Double, centerAltitude: Double) {
        for (y in 0..90 step 10) {
            val altitude: Double = centerAltitude + y
            calculatePosition(azimuth, altitude)?.let {
                if (isOnScreen(it)) {
                    canvas.drawCircle(it.x, it.y, 2f, if (altitude > 0) colors.visibleGridColor else colors.invisibleGridColor)
                } else {
                    return
                }
            }
        }
    }

    private fun drawGridPointsDownwards(azimuth: Double, centerAltitude: Double) {
        for (y in 0..90 step 10) {
            val altitude: Double = centerAltitude - y
            calculatePosition(azimuth, altitude)?.let {
                if (isOnScreen(it)) {
                    canvas.drawCircle(it.x, it.y, 2f, if (altitude > 0) colors.visibleGridColor else colors.invisibleGridColor)
                } else {
                    return
                }
            }
        }
    }

    private fun getStarAlpha(magnitude: Double) =
        when {
            magnitude > 9.0 -> 100
            magnitude > 8.0 -> 115
            magnitude > 7.0 -> 135
            magnitude > 6.0 -> 155
            magnitude > 5.0 -> 175
            magnitude > 4.0 -> 195
            magnitude > 3.0 -> 215
            magnitude > 2.0 -> 235
            else -> 255
        }

    private fun getStarSize(magnitude: Double): Float =
        when {
            magnitude > 6.0 -> 1f
            magnitude > 5.0 -> 2f
            magnitude > 3.0 -> 3f
            magnitude > 1.0 -> 4f
            magnitude > 0.0 -> 5f
            else -> 6f
        }

    private fun isOnScreen(p: SkyPointF): Boolean =
        (abs(p.x) < width) && (abs(p.y) < height)

    private val gridCenter: Topocentric
        get() = Topocentric(Ten.nearest15(center.azimuth), Ten.nearest10(center.altitude))

    private fun getPosition(element: IElement): SkyPointF? =
        positions[element] ?: calculatePosition(element.position)?.also { positions[element] = it }

    private fun calculatePosition(topocentric: Topocentric?): SkyPointF? =
        topocentric?.let {
            projection.calculateZoomImagePoint(it.azimuth, it.altitude)
        }

    private fun calculatePosition(azimuth: Double, altitude: Double): SkyPointF? =
        projection.calculateZoomImagePoint(azimuth, altitude)


}