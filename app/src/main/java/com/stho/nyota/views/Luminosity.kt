package com.stho.nyota.views

import com.stho.nyota.ui.sky.ISkyViewOptions
import kotlin.math.pow

data class Luminosity(val alpha: Int, val radius: Float) {
    companion object {

        private const val TEN: Double = 10.0

        /**
         * Alpha = Alpha_Vega * 10.0 ^ [-0.4 * M * (1 - 2 alpha) / gamma]
         *
         * r(L) = L / LVega ^ alpha, where L = LVega * 10 ^ -0.4 * M
         */

        fun create(magnitude: Double, options: ISkyViewOptions): Luminosity  {
            val alpha = 255 * TEN.pow(0.4 * magnitude * (2 * options.alpha - 1) / options.gamma)
            val L = TEN.pow(-0.4 * magnitude)
            val radius = options.radius * L.pow(options.alpha)
            val P = Math.PI * radius * radius * alpha // TODO: remove
            return Luminosity(alpha.coerceIn(50.0, 255.0).toInt(), radius.coerceIn(1.0, 10.0).toFloat())
        }
    }
}
