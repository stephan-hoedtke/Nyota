package com.stho.nyota.views

import android.graphics.*
import com.stho.nyota.sky.universe.*
import com.stho.nyota.sky.utilities.Ten
import com.stho.nyota.sky.utilities.Topocentric
import com.stho.nyota.sky.utilities.projections.ISphereProjection
import com.stho.nyota.ui.sky.ISkyViewOptions
import java.util.*
import kotlin.math.abs
import kotlin.math.pow


class SkyDraw() {

    private val positions: HashMap<IElement, SkyPointF> = HashMap()
    private val luminos: HashMap<Star, Luminosity> = HashMap()
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

    fun touch() {
        luminos.clear()
        positions.clear()
    }

    fun drawSun(sun: IElement, bitmap: Bitmap) =
        getPosition(sun)?.let {
            if (isOnScreen(it)) {
                val colors: IStarColors = colors.getStarColors(ReferenceType.Default)
                drawImageAt(bitmap, it)
                if (options.displayPlanetNames) {
                    drawNameAt(sun.name, colors.forName, it)
                }
            }
        }

    fun drawMoon(moon: IElement, bitmap: Bitmap) =
        getPosition(moon)?.let {
            if (isOnScreen(it)) {
                val colors: IStarColors = colors.getStarColors(ReferenceType.Default)
                drawImageAt(bitmap, it)
                if (options.displayPlanetNames) {
                    drawNameAt(moon.name, colors.forName, it)
                }
            }
        }

    fun drawPlanet(planet: IElement, bitmap: Bitmap) =
        getPosition(planet)?.let {
            if (isOnScreen(it)) {
                val colors: IStarColors = colors.getStarColors(ReferenceType.Default)
                drawImageAt(bitmap, it)
                if (options.displayPlanetNames) {
                    drawNameAt(planet.name, colors.forName, it)
                }
            }
        }

    fun drawTarget(target: com.stho.nyota.sky.universe.Target, bitmap: Bitmap) =
        getPosition(target)?.let {
            if (isOnScreen(it)) {
                val colors: IStarColors = colors.getStarColors(ReferenceType.Default)
                drawImageAt(bitmap, it)
                drawNameAt(target.name, colors.forName, it)
            }
        }

    fun drawSpecial(special: SpecialElement) =
        getPosition(special)?.let {
            if (isOnScreen(it)) {
                val colors: IStarColors = colors.getStarColors(ReferenceType.Default)
                drawNameAt(special.name, colors.forName, it)
            }
        }

    fun drawStar(star: Star, referenceType: ReferenceType = ReferenceType.Default) =
        getPosition(star)?.let {
            if (isOnScreen(it) && star.isBrighterThan(options.magnitude)) {
                val luminosity = getLuminosity(star)
                val colors: IStarColors = colors.getStarColors(referenceType)

                drawCircleAt(luminosity.radius, colors.forStar, luminosity.alpha, it)

                when {
                    canDrawStarName(star) -> drawNameAt(star.friendlyName, colors.forName, it)
                    canDrawStarSymbol(star) -> drawNameAt(star.symbol.greekSymbol, colors.forSymbol, it);
                }
            }
        }

    private fun canDrawStarName(star: Star): Boolean =
        options.displayStarNames && star.hasFriendlyName

    private fun canDrawStarSymbol(star: Star): Boolean =
        options.displaySymbols && star.hasSymbol

    fun drawConstellation(constellation: Constellation, referenceType: ReferenceType = ReferenceType.Default) {
        val colors = colors.getConstellationColors(referenceType)

        for (star: Star in constellation.stars) {
            drawStar(star, referenceType);
        }

        if (canDrawConstellationLines(referenceType)) {
            for (line in constellation.lines) {
                drawLine(line, colors.forLine);
            }
        }
        if (canDrawConstellationName(referenceType)) {
            drawName(constellation.position, constellation.name, colors.forName)
        }
    }

    private fun canDrawConstellationLines(referenceType: ReferenceType): Boolean =
        options.displayConstellations || referenceType == ReferenceType.TippedConstellation || referenceType == ReferenceType.Reference

    private fun canDrawConstellationName(referenceType: ReferenceType): Boolean =
        options.displayConstellationNames

    fun drawZenit(zenit: Topocentric) =
        drawName(zenit, "Z", colors.visibleGridColor)

    fun drawSatellite(satellite: Satellite, bitmap: Bitmap) =
        calculatePosition(satellite.position)?.let {
            if (isOnScreen(it)) {
                drawImageAt(bitmap, it)
            }
        }

    fun drawName(position: Topocentric, name: String) =
        drawName(position, name, colors.getStarColors(ReferenceType.Default).forName)

    fun drawElement(position: Topocentric, name: String, luminosity: Luminosity) =
        calculatePosition(position)?.let {
            if (isOnScreen(it)) {
                val colors = colors.getStarColors(ReferenceType.Default)
                val fm = colors.forName.fontMetrics
                val h = fm.descent - fm.ascent;
                drawNameAt(name, colors.forName, SkyPointF(it.x + h, it.y + h / 2))
                drawCircleAt(luminosity.radius, colors.forStar, luminosity.alpha, it)
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

    private fun drawCircleAt(radius: Float, color: Paint, alpha: Int, p: SkyPointF) {
        canvas.drawCircle(p.x, p.y, radius, color.apply { this.alpha = alpha });
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

    private fun isOnScreen(p: SkyPointF): Boolean =
        (abs(p.x) < width) && (abs(p.y) < height)

    private val gridCenter: Topocentric
        get() = Topocentric(Ten.nearest15(center.azimuth), Ten.nearest10(center.altitude))

    private fun getLuminosity(star: Star): Luminosity =
        luminos[star] ?: Luminosity.create(star.magn, options).also { luminos[star] = it }

    private fun getPosition(element: IElement): SkyPointF? =
        positions[element] ?: calculatePosition(element.position)?.also { positions[element] = it }

    private fun calculatePosition(topocentric: Topocentric?): SkyPointF? =
        topocentric?.let {
            projection.calculateZoomImagePoint(it.azimuth, it.altitude)
        }

    private fun calculatePosition(azimuth: Double, altitude: Double): SkyPointF? =
        projection.calculateZoomImagePoint(azimuth, altitude)


}