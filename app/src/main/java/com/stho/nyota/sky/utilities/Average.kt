package com.stho.nyota.sky.utilities


/**
 * Calculate the qualified arithmetic average of the recent input angle values
 * Mind: the average of 355 and 5 is not 180, but 0 !
 */
class Average : IAverage {
    private var length = 0
    private var pos = 0
    private val array = DoubleArray(MAX_LENGTH)

    @get:Synchronized
    override var currentAngle = 0.0
        private set

    @Synchronized
    override fun setAngle(degree: Double) {
        registerNewValue(degree)
        takeNewAverage()
    }

    private fun takeNewAverage() {
        val newAverage = getAverage()
        if (Math.abs(Angle.getAngleDifference(newAverage, currentAngle)) > TOLERANCE) {
            currentAngle = newAverage
        }
    }

    private fun getAverage(): Double {
        return getCircularAverage(array, length)
    }

    @Synchronized
    private fun registerNewValue(degree: Double) {
        if (length > 0) {
            pos++
            if (pos >= MAX_LENGTH) pos = 0
        }
        if (length < MAX_LENGTH) length++
        array[pos] = degree
    }

    companion object {
        private const val TOLERANCE = 0.001
        private const val MAX_LENGTH = 20

        /**
         * Calculating the circular mean of a list of angles
         * See: Mean of circular quantities, Wikipedia, https://en.wikipedia.org/wiki/Mean_of_circular_quantities
         *
         * @param array of angles [0° to 360°]
         * @param length number of valid values in array
         * @return circular mean [0° to 360°]
         */
        fun getCircularAverage(array: DoubleArray, length: Int): Double {
            var sin = 0.0
            var cos = 0.0
            if (length > 0) {
                for (i in 0 until length) {
                    sin += Degree.sinus(array[i])
                    cos += Degree.cosines(array[i])
                }
                sin = sin / length
                cos = cos / length
            }
            return Degree.arcTan2(sin, cos)
        }
    }
}