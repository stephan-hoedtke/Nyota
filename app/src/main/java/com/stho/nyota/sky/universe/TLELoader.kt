package com.stho.nyota.sky.universe

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.CookieHandler
import java.net.CookieManager
import java.net.CookiePolicy
import java.net.URL
import javax.net.ssl.HttpsURLConnection

/**
 * Download Two Line Elements from the SpaceTrack web service for a specified NORAD satellite number
 * Reference:
 * https://www.space-track.org/documentation#/howto
 */
class TLELoader {
    /**
     * Download Two Line Elements from the SpaceTrack web service for a specified NORAD satellite number
     */
    @Throws(Exception::class)
    fun download(satelliteNumber: Int): String? {
        val elements: String?
        var connection: HttpsURLConnection? = null
        try {
            manageCookies()
            var url = URL(Configuration.loginURL)
            connection = url.openConnection() as HttpsURLConnection
            connection.doOutput = true
            connection.requestMethod = "POST"
            writeOutput(connection, Configuration.loginString.toByteArray())
            consumeInput(connection)
            url = URL(Configuration.getQueryURL(satelliteNumber))
            elements = getInput(url)
            url = URL(Configuration.logoutURL)
            consumeInput(url)
        } finally {
            connection?.disconnect()
        }
        return elements
    }

    @Throws(IOException::class)
    private fun consumeInput(connection: HttpsURLConnection?) {
        connection!!.inputStream
    }

    @Throws(IOException::class)
    private fun consumeInput(url: URL) {
        url.openStream()
    }

    @Throws(IOException::class)
    private fun getInput(url: URL): String {
        val sb = StringBuilder()
        val br = BufferedReader(InputStreamReader(url.openStream()))
        var line: String?
        while (br.readLine().also { line = it } != null) {
            sb.append(line)
            sb.append("\r\n")
        }
        return sb.toString()
    }

    private fun manageCookies() {
        val manager = CookieManager()
        manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)
        CookieHandler.setDefault(manager)
    }

    @Throws(IOException::class)
    private fun writeOutput(connection: HttpsURLConnection?, bytes: ByteArray) {
        val os = connection!!.outputStream
        os.write(bytes)
        os.flush()
    }

    private object Configuration {
        private const val USERNAME = "stephan.hoedtke@gmx.de"
        private const val PASSWORD = "in.salutari.tuo"
        val loginURL: String
            get() = "https://www.space-track.org/ajaxauth/login"

        val loginString: String
            get() = "identity=$USERNAME&password=$PASSWORD"

        fun getQueryURL(satelliteNumber: Int): String {
            return "https://www.space-track.org/basicspacedata/query/class/tle_latest/NORAD_CAT_ID/$satelliteNumber/format/tle/ORDINAL/1/"
        }

        val logoutURL: String
            get() = "https://www.space-track.org/ajaxauth/logout"
    }
}