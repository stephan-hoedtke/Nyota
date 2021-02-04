package com.stho.nyota.views

import android.graphics.Color
import android.graphics.Paint
import kotlin.math.ln

interface IStarColors {
    val forStar: Paint
    val forName: Paint
    val forSymbol: Paint
}

interface IConstellationColors {
    val forName: Paint
    val forLine: Paint
}

interface ISkyDrawColors {
    val visibleGridColor: Paint // Dark Green
    val invisibleGridColor: Paint // Dark Blue
    val bitmapColor: Paint // White
    val forGalaxy: Paint // Gray
    val forSensitivity: Paint // Gray

    fun getStarColors(referenceType: ReferenceType): IStarColors
    fun getConstellationColors(referenceType: ReferenceType): IConstellationColors
}


data class StarColors(override val forStar: Paint, override val forName: Paint, override val forSymbol: Paint) : IStarColors
data class ConstellationColors(override val forName: Paint, override val forLine: Paint) : IConstellationColors


class SkyDrawColors: ISkyDrawColors {

    private val defaultStarColors: IStarColors = StarColors(forStar = txt(white, 255), forName = txt(gray, 170), forSymbol = txt(gray, 200))
    private val referenceStarColors: IStarColors = StarColors(forStar = txt(orange, 255), forName = txt(orange, 210), forSymbol = txt(orange, 210))
    private val tippedStarColors: IStarColors = StarColors(forStar = txt(red, 200), forName = txt(red, 200), forSymbol = txt(red, 200))
    private val tippedConstellationStarColors: IStarColors = StarColors(forStar = txt(green, 200), forName = txt(green, 200), forSymbol = txt(green, 200))

    private val defaultConstellationColors: IConstellationColors = ConstellationColors(forName =  txt(yellow, 210), forLine = lnl(yellow, 170))
    private val referenceConstellationColors: IConstellationColors = ConstellationColors(forName =  txt(orange, 210), forLine = lnl(orange, 170))
    private val tippedConstellationColors: IConstellationColors = ConstellationColors(forName =  txt(green, 210), forLine = lnl(green, 210))

    override val visibleGridColor: Paint = txt(green, 110)
    override val invisibleGridColor: Paint = txt(blue, 140)
    override val bitmapColor: Paint = txt(white, 255)
    override val forGalaxy: Paint = txt(gray, 150)
    override val forSensitivity: Paint = lnl(gray, alpha = 150)

    override fun getStarColors(referenceType: ReferenceType): IStarColors =
        when (referenceType) {
            ReferenceType.Default -> defaultStarColors
            ReferenceType.Reference -> referenceStarColors
            ReferenceType.TippedStar -> tippedStarColors
            ReferenceType.TippedConstellation -> tippedConstellationStarColors
        }

    override fun getConstellationColors(referenceType: ReferenceType): IConstellationColors =
        when (referenceType) {
            ReferenceType.Default -> defaultConstellationColors
            ReferenceType.Reference -> referenceConstellationColors
            ReferenceType.TippedStar, ReferenceType.TippedConstellation -> tippedConstellationColors
        }

    companion object {

        private val blue: Int = Color.rgb(4, 52, 224) // BLUE
        private val gray: Int = Color.GRAY
        private val green: Int =  Color.rgb(4, 224, 52) // GREEN
        private val orange: Int = Color.rgb(253, 106, 2) // Orange
        private val red: Int = Color.rgb(253, 45, 15) // Red
        private val white: Int = Color.WHITE
        private val yellow: Int = Color.YELLOW

        /**
         * Paint to be used for text (with stroke and fill)
         */
        private fun txt(color: Int, alpha: Int): Paint =
            Paint().apply {
                this.color = color
                this.alpha = alpha
                this.style = Paint.Style.FILL_AND_STROKE
                this.isAntiAlias = true
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
                this.isAntiAlias = true
                this.textSize = 40f
            }



    }
}
