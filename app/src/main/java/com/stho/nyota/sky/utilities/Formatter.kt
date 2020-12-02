package com.stho.nyota.sky.utilities

import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by shoedtke on 04.10.2016.
 */
object Formatter {
    private val formatDateTimeSecTimeZone = SimpleDateFormat("d MMM yyyy HH:mm:ss Z", Locale.ENGLISH)
    private val formatDateTimeSec = SimpleDateFormat("d MMM HH:mm:ss", Locale.ENGLISH)
    private val formatDateTimeTimeZone = SimpleDateFormat("d MMM yyyy HH:mm Z", Locale.ENGLISH)
    val formatDateTime = SimpleDateFormat("d MMM HH:mm", Locale.ENGLISH)
    private val formatTime = SimpleDateFormat("HH:mm", Locale.ENGLISH)
    private val formatTimeSec = SimpleDateFormat("HH:mm::ss", Locale.ENGLISH)
    private val formatDateTimeZone = SimpleDateFormat("d MMM yyyy Z", Locale.ENGLISH)
    val formatDate: SimpleDateFormat = SimpleDateFormat("d MMM yyyy", Locale.ENGLISH)
    private val formatTimeZone = SimpleDateFormat("Z", Locale.ENGLISH)
    val df0: DecimalFormat = DecimalFormat.getNumberInstance(Locale.ENGLISH) as DecimalFormat
    val df2: DecimalFormat = DecimalFormat.getNumberInstance(Locale.ENGLISH) as DecimalFormat
    val df3: DecimalFormat = DecimalFormat.getNumberInstance(Locale.ENGLISH) as DecimalFormat
    const val SPACE = "  "
    private const val UNICODE_THIN_SPACE = '\u2009'


    fun parseInt(s: String): Int {
        return s.trim().toInt()
    }

    fun parseDouble(s: String): Double {
        return s.trim().toDouble()
    }

    fun parseString(s: String): String {
        return s.trim()
    }

    fun parseTimeZone(s: String): TimeZone {
        return TimeZone.getTimeZone(s.trim())
    }

    fun toString(utc: UTC, timeZone: TimeZone, timeFormat: TimeFormat): String {
        return toString(utc.time, timeZone, timeFormat)
    }

    fun toString(calendar: Calendar, timeFormat: TimeFormat): String {
        return toString(calendar.time, calendar.timeZone, timeFormat)
    }

    fun toString(value: Double): String {
        return String.format(Locale.ENGLISH, "%.4f", value)
    }

    private const val HALF_A_MINUTE_IN_MILLISECONDS: Long = 29000

    private fun toNearestWholeMinute(utc: Date): Date {
        return Date(utc.time + HALF_A_MINUTE_IN_MILLISECONDS)
    }

    fun toString(utc: Date, timeZone: TimeZone, timeFormat: TimeFormat): String {
        return when (timeFormat) {
            TimeFormat.TIME -> {
                formatTime.timeZone = timeZone
                formatTime.format(utc)
            }
            TimeFormat.DATE -> {
                formatDate.timeZone = timeZone
                formatDate.format(utc)
            }
            TimeFormat.DATE_TIMEZONE -> {
                formatDateTimeZone.timeZone = timeZone
                formatDateTimeZone.format(utc)
            }
            TimeFormat.DATETIME -> {
                formatDateTime.timeZone = timeZone
                formatDateTime.format(toNearestWholeMinute(utc))
            }
            TimeFormat.DATETIME_TIMEZONE -> {
                formatDateTimeTimeZone.timeZone = timeZone
                formatDateTimeTimeZone.format(toNearestWholeMinute(utc))
            }
            TimeFormat.DATETIME_SEC -> {
                formatDateTimeSec.timeZone = timeZone
                formatDateTimeSec.format(utc)
            }
            TimeFormat.DATETIME_SEC_TIMEZONE -> {
                formatDateTimeSecTimeZone.timeZone = timeZone
                formatDateTimeSecTimeZone.format(utc)
            }
            TimeFormat.TIMEZONE -> {
                formatTimeZone.timeZone = timeZone
                formatTimeZone.format(utc)
            }
            TimeFormat.TIME_SEC -> {
                formatTimeSec.timeZone = timeZone
                formatTimeSec.format(utc)
            }
        }
    }

    enum class TimeFormat {
        DATE, DATE_TIMEZONE, TIME, DATETIME, DATETIME_TIMEZONE, DATETIME_SEC, DATETIME_SEC_TIMEZONE, TIMEZONE, TIME_SEC
    }

    init {
        df0.maximumFractionDigits = 0
        df0.minimumFractionDigits = 0
        df0.minimumIntegerDigits = 1
        df0.decimalFormatSymbols = df0.decimalFormatSymbols.also { it.groupingSeparator = UNICODE_THIN_SPACE }
        df2.minimumFractionDigits = 2
        df2.maximumFractionDigits = 2
        df2.minimumIntegerDigits = 1
        df3.minimumFractionDigits = 3
        df3.maximumFractionDigits = 3
        df3.minimumIntegerDigits = 1
    }
}