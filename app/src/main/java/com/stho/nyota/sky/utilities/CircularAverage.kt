package com.stho.nyota.sky.utilities


/**
 * Calculate the qualified arithmetic average of the recent input angle values
 * Mind: the average of 355 and 5 is not 180, but 0 !
 */
object CircularAverage {

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
                sin += Degree.sin(array[i])
                cos += Degree.cos(array[i])
            }
            sin /= length
            cos /= length
        }
        return Degree.arcTan2(sin, cos)
    }
}