package com.stho.nyota.sky.universe

import com.stho.nyota.sky.utilities.Radian.fromDegrees
import kotlin.math.pow

/**
 * Created by shoedtke on 20.01.2017.
 */
class TLEParser {

    internal fun parseTLE(elements: String): TLE? {
        val pattern = "[\\r\\n]+".toRegex()
        val lines = elements.split(pattern).filter { it.isNotBlank() }
        return when {
            lines.size != 2 -> null
            else -> parseTLE(elements, lines[0], lines[1])
        }
    }

    private fun parseTLE(elements: String, line1: String, line2: String): TLE? {
        return when {
            !test(line1, '1') -> null
            !test(line2, '2') -> null
            else -> {
                /*
                    ISS
                    1 25544U 98067A   02256.70033192  .00045618  00000-0  57184-3 0  1499
                    2 25544  51.6396 328.6851 0018421 253.2171 244.7656 15.59086742 217834
                */
                val noradSatelliteNumber = parseInt(line1, 3, 7) // NORAD Satellite Number, 3-7, "25544"
                val internationalDesignator = parseString(line1, 10, 17) // International Designator , 10-17, "98067A"
                val year = parseInt(line1, 19, 20) // Epoch Year (Last two digits of year), 19-20, "08",
                val day = parseDouble(line1, 21, 32) // Epoch (Day of the year and fractional portion of the day), 21-32, "264.51782528"
                val epoch = getEpoch(year) + day
                val bstar = parseDoubleExp(line1, 54, 61) // BSTAR drag term (decimal point assumed), 54-61, "-11606-4"
                val xmo = parseAngle(line2, 44, 51) // Mean Anomaly [Degrees], 44-51, "325.0288"
                val xnodeo = parseAngle(line2, 18, 25) // Right Ascension of the Ascending Node [Degrees], 18-25, "247.4627"
                val omegao = parseAngle(line2, 35, 42) // Argument of Perigee [Degrees], 35-42, "130.5360"
                val xincl = parseAngle(line2, 9, 16) // Inclination [Degrees], 9-16, "51.6516"
                val eo = parseDouble(line2, 27, 33, "0.") // Eccentricity (decimal point assumed), 27-33, "0006703" --> 0.0006703
                val revolutionsPerDay = parseDouble(line2, 53, 63) //  	Mean Motion [Revs per day], 53-63, "15.72125391"
                val revolutionNumber = parseInt(line2, 64, 68) // Revolution Number of Epoch (Revs), 64-68, "56353"

                // Secondary Calculated Figures
                val xno = revolutionsPerDay * 2 * Math.PI / MINUTES_PER_DAY
                val meanDistanceFromEarth = EARTH_RADIUS * ((xke / xno).pow(2.0 / 3.0) - 1)

                return TLE(elements,
                        epoch, bstar, xincl, xnodeo, eo, omegao, xmo, xno,
                        noradSatelliteNumber, revolutionNumber, internationalDesignator, meanDistanceFromEarth, revolutionsPerDay)
            }
        }
    }

    private fun test(line: String, identifier: Char): Boolean {
        return when {
            !verifyChecksum(line) -> false
            line[0] != identifier -> false
            else -> true
        }
    }

    /// <summary>
    /// Get the Epoch of the specified year: 17 --> Epoch(2017)
    /// </summary>
    private fun getEpoch(year: Int): Double {
        var y = year
        val julianDay1900 = 2415019.5
        if (y < 57) y += 100 // Loop around Y2K
        return julianDay1900 + y * 365.0 + (y - 1) / 4
    }

    private fun parseAngle(line: String, a: Int, b: Int): Double {
        val degree = parseDouble(line, a, b)
        return fromDegrees(degree)
    }

    private fun parseDouble(line: String, a: Int, b: Int, prefix: String): Double {
        val substring = prefix + parseString(line, a, b)
        return substring.trim().toDouble()
    }

    private fun parseDouble(line: String, a: Int, b: Int): Double {
        val substring = parseString(line, a, b)
        return substring.trim().toDouble()
    }

    private fun parseInt(line: String, a: Int, b: Int): Int {
        val substring = parseString(line, a, b)
        return substring.trim().toInt()
    }

    private fun parseDoubleExp(line: String, a: Int, b: Int): Double {
        var substring = parseString(line, a, b)
        val prefix = if (substring[0] == '-') "-0." else "0."
        val suffix = if (substring[6] == '-') "E-" else "E"
        substring = prefix + substring.substring(1, 5) + suffix + substring.substring(7)
        return substring.toDouble()
    }

    /**
     * Substring using 1-based positions a and b ---> results into substring from start := a-1 until end(after) := b
     */
    private fun parseString(line: String, a: Int, b: Int): String {
        return line.substring(a - 1, b)
    }

    /// <summary>
    /// Calculates the checksum of all but the last digits modulo 10 and compares it with the last. (Letters, blanks, periods, plus signs = 0; minus signs = 1)
    /// </summary>
    private fun verifyChecksum(line: String): Boolean {
        var sum = 0
        if (line.length < 69) return false
        for (i in 0..67) {
            when (line[i]) {
                '1' -> sum += 1
                '2' -> sum += 2
                '3' -> sum += 3
                '4' -> sum += 4
                '5' -> sum += 5
                '6' -> sum += 6
                '7' -> sum += 7
                '8' -> sum += 8
                '9' -> sum += 9
                '-' -> sum += 1
            }
        }
        val checksum = ('0'.code + sum % 10).toChar()
        return (line[68] == checksum)
    }

    companion object {
        private const val MINUTES_PER_DAY = 1440.0
        private const val EARTH_RADIUS = 6378.135
        private const val xke = 0.0743669161331734132
    }
}