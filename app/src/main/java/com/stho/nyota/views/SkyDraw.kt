package com.stho.nyota.views

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.stho.nyota.settings.Settings
import com.stho.nyota.settings.ViewStyle
import com.stho.nyota.sky.universe.*
import com.stho.nyota.sky.utilities.Ten
import com.stho.nyota.sky.utilities.Topocentric
import com.stho.nyota.sky.utilities.projections.ISphereProjection
import com.stho.nyota.ui.sky.IViewOptions
import kotlin.math.abs
import kotlin.math.sqrt


class SkyDraw() {

    private var calculator: LuminosityCalculator? = null
    private val positions: HashMap<IElement, SkyPointF> = HashMap()
    private val luminos: HashMap<Star, Luminosity> = HashMap()
    private var colors: ISkyDrawColors = SkyDrawColors()

    lateinit var canvas: Canvas
    lateinit var center: Topocentric
    lateinit var options: IViewOptions
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
        calculator = null
        luminos.clear()
        positions.clear()
    }

    fun drawSun(sun: IElement, bitmap: Bitmap) =
        getPosition(sun)?.let {
            if (isOnScreen(it)) {
                drawImageAt(bitmap, it)
                if (options.displayPlanetNames) {
                    drawNameAt(sun.name, colors.forName, it)
                }
            }
        }

    fun drawMoon(moon: IElement, bitmap: Bitmap) =
        getPosition(moon)?.let {
            if (isOnScreen(it)) {
                drawImageAt(bitmap, it)
                if (options.displayPlanetNames) {
                    drawNameAt(moon.name, colors.forName, it)
                }
            }
        }

    fun drawPlanet(planet: IElement, bitmap: Bitmap) =
        getPosition(planet)?.let {
            if (isOnScreen(it)) {
                drawImageAt(bitmap, it)
                if (options.displayPlanetNames) {
                    drawNameAt(planet.name, colors.forName, it)
                }
            }
        }

    fun drawTarget(target: com.stho.nyota.sky.universe.Target, bitmap: Bitmap) =
        getPosition(target)?.let {
            if (isOnScreen(it)) {
                drawImageAt(bitmap, it)
                drawNameAt(target.name, colors.forName, it)
            }
        }

    fun drawNameOf(element: IElement) =
        getPosition(element)?.let {
            if (isOnScreen(it)) {
                drawNameAt(element.name, colors.forName, it)
            }
        }

    fun drawGalaxy(galaxy: Galaxy, referenceType: ReferenceType) =
        getPosition(galaxy)?.let {
            if (isOnScreen(it)) {
                val clr: IStarColors = colors.getStarColors(referenceType, options.style)

                drawCircleAt(5f, clr.forStar(StarColor.White), 255, it)

                when {
                    options.displayPlanetNames -> drawNameAt(galaxy.name, colors.forGalaxy, it)
                }
            }
        }

    fun drawStar(star: Star, referenceType: ReferenceType) =
        getPosition(star)?.let {
            if (isOnScreen(it) && star.isBrighterThan(options.magnitude)) {
                val luminosity = getLuminosity(star)
                val clr: IStarColors = colors.getStarColors(referenceType = referenceType, style = options.style)
                drawCircleAt(luminosity.radius, clr.forStar(starColor = star.color), luminosity.alpha, it)

                when {
                    canDrawStarName(star) -> drawNameAt(star.friendlyName, clr.forName, it)
                    canDrawStarSymbol(star) -> drawNameAt(star.symbol.greekSymbol, clr.forSymbol, it);
                }
            }
        }

    private fun canDrawStarName(star: Star): Boolean =
        when (options.style) {
            ViewStyle.Normal -> options.displayStarNames && star.hasFriendlyName
            ViewStyle.HintsOnly -> false
            ViewStyle.Plain -> false
        }

    private fun canDrawStarSymbol(star: Star): Boolean =
        when (options.style) {
            ViewStyle.Normal -> options.displaySymbols && star.hasSymbol
            ViewStyle.HintsOnly -> false
            ViewStyle.Plain -> false
        }

    fun drawConstellation(constellation: Constellation, referenceType: ReferenceType) {
        val clr = colors.getConstellationColors(referenceType, options.style)

        for (star: Star in constellation.stars) {
            drawStar(star, referenceType);
        }

        if (canDrawConstellationLines(referenceType)) {
            for (line in constellation.lines) {
                drawLine(line, clr.forLine);
            }
        }
        if (canDrawConstellationName(referenceType)) {
            drawName(constellation.position, constellation.name, clr.forName)
        }
    }

    private fun canDrawConstellationLines(referenceType: ReferenceType): Boolean =
        when (options.style) {
            ViewStyle.Normal -> options.displayConstellations || referenceType == ReferenceType.TippedConstellation || referenceType == ReferenceType.Reference
            ViewStyle.HintsOnly -> false
            ViewStyle.Plain -> false
        }

    private fun canDrawConstellationName(referenceType: ReferenceType): Boolean =
        options.displayConstellationNames

    fun drawZenit(zenit: Topocentric) =
        drawName(zenit, "Z", colors.visibleGridColor)

    fun drawNadir(nadir: Topocentric) =
        drawName(nadir, "R", colors.visibleGridColor)


    fun drawSatellite(satellite: Satellite, bitmap: Bitmap) =
        calculatePosition(satellite.position)?.let {
            if (isOnScreen(it)) {
                drawImageAt(bitmap, it)
            }
        }

    fun drawName(position: Topocentric, name: String) =
        drawName(position, name, colors.forName)

    fun drawElement(position: Topocentric, name: String, luminosity: Luminosity) =
        calculatePosition(position)?.let {
            if (isOnScreen(it)) {
                val clr = colors.getStarColors(referenceType = ReferenceType.Default, style = ViewStyle.Plain)
                val fm = clr.forName.fontMetrics
                val h = fm.descent - fm.ascent;
                drawNameAt(name, clr.forName, SkyPointF(it.x + h, it.y + h / 2))
                drawCircleAt(luminosity.radius, clr.forStar(starColor = StarColor.White), luminosity.alpha, it)
            }
        }

    fun drawSensitivityArea(position: Topocentric, sensitivityAngle: Double) =
        calculatePosition(position)?.let {
            if (isOnScreen(it)) {
                val azimuthSensitivity = sensitivityAngle / position.azimuthDistanceFactor
                path.reset();
                calculatePosition(position.azimuth + azimuthSensitivity, position.altitude + sensitivityAngle)?.apply { path.moveTo(this.x, this.y) }
                calculatePosition(position.azimuth + azimuthSensitivity, position.altitude - sensitivityAngle)?.apply { path.lineTo(this.x, this.y) }
                calculatePosition(position.azimuth - azimuthSensitivity, position.altitude - sensitivityAngle)?.apply { path.lineTo(this.x, this.y) }
                calculatePosition(position.azimuth - azimuthSensitivity, position.altitude + sensitivityAngle)?.apply { path.lineTo(this.x, this.y) }
                calculatePosition(position.azimuth + azimuthSensitivity, position.altitude + sensitivityAngle)?.apply { path.lineTo(this.x, this.y) }
                canvas.drawPath(path, colors.forSensitivity);
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

    fun drawLight() {
        val t = Topocentric(center.azimuth, 0.0)
        calculatePosition(t)?.also {
            if (isOnScreen(it)) {
                drawLightAbove(it)
            }
            else if (it.y > 0) {
                drawLightEverywhere()
            }
        } ?: if (center.altitude > 0) {
                drawLightEverywhere()
        }
    }

    private fun drawLightEverywhere() {
        val x = 0.5f * width
        val y = 0.5f * height
        canvas.drawRect(-x, -y, x, y, colors.forSky)
    }

    private fun drawLightAbove(it: SkyPointF) {
        val x = 0.5f * width
        val y = 0.5f * height
        canvas.drawRect(-x, -y, x, it.y, colors.forSky)
    }

    fun drawEcliptic(ecliptic: Collection<Topocentric>) {
        if (canDrawEcliptic()) {
            var previousPoint: SkyPointF? = null
            path.reset()
            ecliptic.forEach { p ->
                calculatePosition(p.azimuth, p.altitude)?.also {
                    if (isOnScreen(it)) {
                        if (previousPoint == null) {
                            path.moveTo(it.x, it.y)
                        } else {
                            val dx = it.x - (previousPoint?.x ?: 0f)
                            val dy = it.y - (previousPoint?.y ?: 0f)
                            if (abs(dx) > width / 2 || abs(dy) > height / 2) {
                                path.moveTo(it.x, it.y)
                            } else {
                                path.lineTo(it.x, it.y)
                            }
                        }
                        previousPoint = it
                    } else {
                        previousPoint = null
                    }
                }
            }
            canvas.drawPath(path, colors.forEcliptic)
        }
    }

    private fun canDrawEcliptic(): Boolean =
        when (options.style) {
            ViewStyle.Normal -> options.displayEcliptic
            ViewStyle.HintsOnly -> true
            ViewStyle.Plain -> false
        }


    fun drawHint(hint: Hint) {
        if (canDrawHint()) {
            when (hint.size) {
                1 -> {
                    getPosition(hint[0])?.also { a ->
                        if (isOnScreen(a)) {
                            drawNameAt(hint.toString(), colors.hintColors.forName, a)
                        }
                    }
                }
                2 -> {
                    getPosition(hint[0])?.also { a ->
                        getPosition(hint[1])?.also { b ->
                            if (isOnScreen(a) && isOnScreen(b)) {
                                drawArrow(a, b, colors.hintColors.forArrow)
                                drawNameAt(hint.toString(), colors.hintColors.forName, b)
                            }
                        }
                    }
                }
                3 -> {
                    getPosition(hint[0])?.also { a ->
                        getPosition(hint[1])?.also { b ->
                            getPosition(hint[2])?.also { c ->
                                if (isOnScreen(a) && isOnScreen(b) && isOnScreen(c)) {
                                    drawTriangle(a, b, c, colors.hintColors.forLine)
                                    val x = (a.x + b.x + c.x) / 3
                                    val y = (a.y + b.y + c.y) / 3
                                    drawNameAt(hint.toString(), colors.hintColors.forName, SkyPointF(x, y))
                                }
                            }
                        }
                    }
                }
                4 -> {
                    getPosition(hint[0])?.also { a ->
                        getPosition(hint[1])?.also { b ->
                            getPosition(hint[2])?.also { c ->
                                getPosition(hint[3])?.also { d ->
                                    if (isOnScreen(a) && isOnScreen(b) && isOnScreen(c) && isOnScreen(d)) {
                                        drawRectangle(a, b, c, d, colors.hintColors.forLine)
                                        val x = (a.x + b.x + c.x + d.x) / 4
                                        val y = (a.y + b.y + c.y + d.y) / 4
                                        drawNameAt(hint.toString(), colors.hintColors.forName, SkyPointF(x, y))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun canDrawHint(): Boolean =
        when (options.style) {
            ViewStyle.Normal -> options.displayHints
            ViewStyle.HintsOnly -> true
            ViewStyle.Plain -> false
        }

    private fun drawCircleAt(radius: Float, color: Paint, alpha: Int, p: SkyPointF) {
        canvas.drawCircle(p.x, p.y, radius, color.apply { this.alpha = alpha });
    }

    private fun drawArrow(from: SkyPointF, to: SkyPointF, color: Paint) {
        val dx = (to.x - from.x).toDouble()
        val dy = (to.y - from.y).toDouble()
        val l = sqrt(dx * dx + dy * dy)
        val c = 70 / l
        val a = 27 / l
        val b = 7 / l
        val p = from.plus(c * dx,c * dy)
        val q = to.minus(c * dx,c * dy)
        val s = q.minus(a * dx, a * dy).plus(b * dy, -b * dx)
        val t = q.minus(a * dx, a * dy).plus(-b * dy, b * dx)
        path.reset()
        path.moveTo(p.x, p.y)
        path.lineTo(q.x, q.y)
        path.lineTo(s.x, s.y)
        path.lineTo(t.x, t.y)
        path.lineTo(q.x, q.y)
        canvas.drawPath(path, color)
    }

    private fun drawTriangle(a: SkyPointF, b: SkyPointF, c: SkyPointF, color: Paint) {
        path.reset()
        path.moveTo(a.x, a.y)
        path.lineTo(b.x, b.y)
        path.lineTo(c.x, c.y)
        path.lineTo(a.x, a.y)
        canvas.drawPath(path, color)
    }

    private fun drawRectangle(a: SkyPointF, b: SkyPointF, c: SkyPointF, d: SkyPointF, color: Paint) {
        path.reset()
        path.moveTo(a.x, a.y)
        path.lineTo(b.x, b.y)
        path.lineTo(c.x, c.y)
        path.lineTo(d.x, d.y)
        path.lineTo(a.x, a.y)
        canvas.drawPath(path, color)
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
        luminos[star] ?: getLuminosityCalculator().calculate(star.magn).also { luminos[star] = it }

    private fun getLuminosityCalculator(): LuminosityCalculator =
        calculator ?: LuminosityCalculator.create(projection.zoomAngle, options).also { calculator = it }

    private fun getPosition(element: IElement): SkyPointF? =
        positions[element] ?: calculatePosition(element.position)?.also { positions[element] = it }

    private fun calculatePosition(topocentric: Topocentric?): SkyPointF? =
        topocentric?.let {
            projection.calculateZoomImagePoint(it.azimuth, it.altitude)
        }

    private fun calculatePosition(azimuth: Double, altitude: Double): SkyPointF? =
        projection.calculateZoomImagePoint(azimuth, altitude)
}