package com.stho.nyota.views

import android.graphics.Color
import android.graphics.Paint


interface ISkyDrawColors {
    val gridColor: Paint // Blue
    val starColor: Paint // White
    val bitmapColor: Paint // White
    val lineColor: Paint // Yellow
    val nameColor: Paint // Orange
    val symbolColor: Paint // Gray
    val constellationNameColor: Paint // Orange
    val referenceColor: Paint
    val referenceLineColor: Paint
    val referenceNameColor: Paint
    val referenceSymbolColor: Paint
}

class SkyDrawColors: ISkyDrawColors {

    override val gridColor: Paint = Paint().apply {
        color = Color.rgb(2, 106, 253) // BRIGHT BLUE
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
        alpha = 170
        style = Paint.Style.STROKE
    }

    override val symbolColor: Paint = Paint().apply {
        color = Color.GRAY
        alpha = 200
        style = Paint.Style.FILL_AND_STROKE
        isAntiAlias = true
        textSize = 40f
    }

    override val nameColor: Paint = Paint().apply {
        color = Color.rgb(253, 106, 2) // Orange
        alpha = 170
        style = Paint.Style.FILL_AND_STROKE
        isAntiAlias = true
        textSize = 40f
    }

    override val constellationNameColor: Paint = Paint().apply {
        color = Color.rgb(242, 210 , 34) // Dark Yellow
        alpha = 170
        style = Paint.Style.FILL_AND_STROKE
        isAntiAlias = true
        textSize = 40f
    }

    override val referenceColor: Paint  = Paint().apply {
        color = Color.rgb(253, 45, 15) // Red
        alpha = 255
        style = Paint.Style.FILL_AND_STROKE
        isAntiAlias = true
        textSize = 40f
    }

    override val referenceLineColor: Paint = Paint().apply {
        color = Color.rgb(253, 106, 2) // Orange
        alpha = 120
        style = Paint.Style.STROKE
        isAntiAlias = true
        textSize = 40f
    }

    override val referenceNameColor: Paint  = Paint().apply {
        color = Color.rgb(182, 60, 2) // Orange
        alpha = 120
        style = Paint.Style.FILL_AND_STROKE
        isAntiAlias = true
        textSize = 40f
    }

    override val referenceSymbolColor: Paint  = Paint().apply {
        color = Color.rgb(253, 106, 2) // Orange
        alpha = 200
        style = Paint.Style.FILL_AND_STROKE
        isAntiAlias = true
        textSize = 40f
    }
}
