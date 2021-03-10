package com.stho.nyota.views

import android.graphics.Color
import android.graphics.Paint
import androidx.annotation.ColorInt
import com.stho.nyota.settings.ViewStyle
import com.stho.nyota.sky.universe.StarColor

interface IStarColors {
    val forName: Paint
    val forSymbol: Paint
    fun forStar(starColor: StarColor): Paint
}

interface IConstellationColors {
    val forName: Paint
    val forLine: Paint
}

interface IHintColors {
    val forName: Paint
    val forLine: Paint
    val forArrow: Paint
}

interface ISkyDrawColors {
    val forName: Paint
    val forSky: Paint // Dark Blue, nearly Black, just a bit of light
    val forEcliptic: Paint // Yellow
    val visibleGridColor: Paint // Dark Green
    val invisibleGridColor: Paint // Dark Blue
    val bitmapColor: Paint // White
    val forGalaxy: Paint // Gray
    val forSensitivity: Paint // Gray
    val hintColors: IHintColors
    fun getStarColors(referenceType: ReferenceType, style: ViewStyle): IStarColors
    fun getConstellationColors(referenceType: ReferenceType, style: ViewStyle): IConstellationColors
}


data class ConstellationColors(override val forName: Paint, override val forLine: Paint) : IConstellationColors
data class HintColors(override val forName: Paint, override val forLine: Paint, override val forArrow: Paint) : IHintColors
data class StarColors(private val forStar: Paint, override val forName: Paint, override val forSymbol: Paint) : IStarColors {
    override fun forStar(starColor: StarColor): Paint =
        forStar
}
data class SpectralStarColors(override val forName: Paint, override val forSymbol: Paint) : IStarColors {

    override fun forStar(starColor: StarColor): Paint =
        when (starColor) {
            StarColor.Blue -> blue
            StarColor.DeepBlueWhite -> deepBlueWhite
            StarColor.BlueWhite -> blueWhite
            StarColor.White -> white
            StarColor.YellowishWhite -> yellowishWhite
            StarColor.PaleYellowOrange -> paleYellowOrange
            StarColor.LightOrangeRed -> lightOrangeRed
        }

    private val blue: Paint = clr(0xFF99CCFF.toInt())
    private val deepBlueWhite = clr(0xFFCAD4E3.toInt())
    private val blueWhite = clr(0xFFD5E0FF.toInt())
    private val white = clr(0xFFF9F5FF.toInt())
    private val yellowishWhite = clr(0xFFFFEDE3.toInt())
    private val paleYellowOrange = clr(0xFFFFDAB5.toInt())
    private val lightOrangeRed = clr(0xFFFFB56C.toInt())

    companion object {

         private fun clr(@ColorInt color: Int): Paint =
            Paint().apply {
                this.color = color
                this.alpha = 255
                this.style = Paint.Style.FILL_AND_STROKE
                this.isAntiAlias = true
                this.strokeWidth = 1f
                this.textSize = 40f
            }
    }
}

class SkyDrawColors: ISkyDrawColors {

    private val starColors: HashMap<StarColor, Paint> =
        HashMap<StarColor, Paint>().apply {
            put(StarColor.Blue, txt(0xFF92B5FF.toInt()))
            put(StarColor.DeepBlueWhite, txt(0xFFA2C0FF.toInt()))
            put(StarColor.BlueWhite, txt(0xFFD5E0FF.toInt()))
            put(StarColor.White, txt(0xFFF9F5FF.toInt()))
            put(StarColor.YellowishWhite, txt(0xFFFFEDE3.toInt()))
            put(StarColor.PaleYellowOrange, txt(0xFFFFDAB5.toInt()))
            put(StarColor.LightOrangeRed, txt(0xFFFFB56C.toInt()))
        }

    private val defaultStarColors: IStarColors = SpectralStarColors(forName = txt(gray, 170), forSymbol = txt(gray, 200))
    private val referenceStarColors: IStarColors = StarColors(forStar = txt(orange, 255), forName = txt(orange, 210), forSymbol = txt(orange, 210))
    private val tippedStarColors: IStarColors = StarColors(forStar = txt(red, 255), forName = txt(red, 200), forSymbol = txt(red, 200))
    private val tippedConstellationStarColors: IStarColors = StarColors(forStar = txt(green, 255), forName = txt(green, 200), forSymbol = txt(green, 200))

    private val defaultConstellationColors: IConstellationColors = ConstellationColors(forName =  txt(yellow, 210), forLine = lnl(yellow, 170))
    private val referenceConstellationColors: IConstellationColors = ConstellationColors(forName =  txt(orange, 210), forLine = lnl(orange, 170))
    private val tippedConstellationColors: IConstellationColors = ConstellationColors(forName =  txt(green, 210), forLine = lnl(green, 210))

    override val forName: Paint = txt(gray, 170)
    override val forSky: Paint = txt(blue, alpha = 30)
    override val forEcliptic: Paint = lnl(gray, 180)
    override val hintColors: IHintColors = HintColors(forArrow = txt(fire, alpha = 255), forName = txt(fire, alpha = 130), forLine = lnl(green, alpha = 130))
    override val visibleGridColor: Paint = txt(green, 110)
    override val invisibleGridColor: Paint = txt(blue, 140)
    override val bitmapColor: Paint = txt(white, 255)
    override val forGalaxy: Paint = txt(gray, 150)
    override val forSensitivity: Paint = lnl(gray, alpha = 150)

    override fun getStarColors(referenceType: ReferenceType, style: ViewStyle): IStarColors =
        when (style) {
            ViewStyle.Plain, ViewStyle.HintsOnly -> when (referenceType) {
                ReferenceType.Default -> defaultStarColors
                ReferenceType.Reference -> defaultStarColors
                ReferenceType.TippedStar -> tippedStarColors
                ReferenceType.TippedConstellation -> tippedConstellationStarColors
            }
            else -> when (referenceType) {
                ReferenceType.Default -> defaultStarColors
                ReferenceType.Reference -> referenceStarColors
                ReferenceType.TippedStar -> tippedStarColors
                ReferenceType.TippedConstellation -> tippedConstellationStarColors
            }
        }

    override fun getConstellationColors(referenceType: ReferenceType, style: ViewStyle): IConstellationColors =
        when (style) {
            ViewStyle.Plain, ViewStyle.HintsOnly -> when (referenceType) {
                ReferenceType.Default -> defaultConstellationColors
                ReferenceType.Reference -> defaultConstellationColors
                ReferenceType.TippedStar, ReferenceType.TippedConstellation -> tippedConstellationColors
            }
            else -> when (referenceType) {
                ReferenceType.Default -> defaultConstellationColors
                ReferenceType.Reference -> referenceConstellationColors
                ReferenceType.TippedStar, ReferenceType.TippedConstellation -> tippedConstellationColors
            }
        }

    companion object {

        private val blue: Int = Color.rgb(4, 52, 224) // BLUE
        private val gray: Int = Color.GRAY
        private val green: Int =  Color.rgb(4, 224, 52) // GREEN
        private val orange: Int = Color.rgb(253, 106, 2) // Orange
        private val red: Int = Color.rgb(253, 45, 15) // Red
        private val fire: Int = Color.rgb(255, 39, 0) // Fire Red
        private val white: Int = Color.WHITE
        private val yellow: Int = Color.YELLOW

        /**
         * Paint to be used for text (with stroke and fill)
         */
        private fun txt(@ColorInt color: Int, alpha: Int = 255): Paint =
            Paint().apply {
                this.color = color
                this.alpha = alpha
                this.style = Paint.Style.FILL_AND_STROKE
                this.isAntiAlias = true
                this.strokeWidth = 1f
                this.textSize = 40f
            }

        /**
         * Paint to be used for lines (only stoke, no fill)
         */
        private fun lnl(color: Int, alpha: Int): Paint =
            Paint().apply {
                this.color = color
                this.alpha = alpha
                this.style = Paint.Style.STROKE
                this.strokeWidth = 1f
                this.isAntiAlias = true
                this.textSize = 40f
            }

    }
}
