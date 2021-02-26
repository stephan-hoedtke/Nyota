
package com.stho.nyota.views

import com.stho.nyota.ui.sky.ISkyViewOptions
import com.stho.nyota.ui.sky.IViewOptions
import kotlin.math.log10
import kotlin.math.pow


data class Luminosity(val alpha: Int, val radius: Float)


class LuminosityCalculator(private val r0: Double, private val Mt: Double, private val rt: Double, private val gamma: Double) {

    private val r: Double = rt / r0
    private val r2: Double = r * r
    private val delta: Double = -2.5 * log10(r) / Mt

    fun calculate(magnitude: Double): Luminosity {
        val radius = when {
            magnitude < 0 -> r0
            magnitude > Mt -> rt
            else -> r0 * TEN.pow(-0.4 * delta * magnitude)
        }
        val A = when {
            magnitude < Mt -> 1.0
            else -> TEN.pow(-0.8 * delta  * magnitude) / r2
        }
        val alpha = 255.0 * A.pow(gamma)
        return Luminosity(alpha.toInt(), radius.toFloat())
    }

    companion object {
        private const val TEN: Double = 10.0

        fun create(zoomAngle: Double, options: IViewOptions): LuminosityCalculator =
            LuminosityCalculator(options.radius, targetMagnitude(zoomAngle).pow(options.lambda), 1.0, options.gamma)

        private fun targetMagnitude(zoomAngle: Double): Double =
            // 10 --> 10.0
            // 20 --> 5.0
            // 30 --> 4.0
            // 45 --> 3.0
            // 90 --> 2.0
            when {
                zoomAngle > 90.0 -> 2.0
                zoomAngle > 45.0 -> f(zoomAngle, 45.0, 90.0, 3.0, 2.0)
                zoomAngle > 30.0 -> f(zoomAngle, 30.0, 45.0, 4.0, 3.0)
                zoomAngle > 20.0 -> f(zoomAngle, 20.0, 30.0, 5.0, 4.0)
                zoomAngle > 10.0 -> f(zoomAngle, 10.0, 20.0, 10.0, 5.0)
                else -> f(zoomAngle, 0.0, 10.0, 20.0, 10.0)
            }

        private fun f(x: Double, x0: Double, x1: Double, f0: Double, f1: Double): Double =
            (f1 * (x - x0) + f0 * (x1 - x)) / (x1 - x0)

    }
}
