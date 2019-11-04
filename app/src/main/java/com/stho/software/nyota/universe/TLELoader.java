package com.stho.software.nyota.universe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Download Two Line Elements from the SpaceTrack web service for a specified NORAD satellite number
 * Reference:
 *      https://www.space-track.org/documentation#/howto
 */
public class TLELoader {

    /**
     * Download Two Line Elements from the SpaceTrack web service for a specified NORAD satellite number
     */
    public static String download(String satelliteNumber) throws Exception {

        String elements = null;
        HttpsURLConnection connection = null;

        try {
            manageCookies();

            URL url = new URL(Configuration.getLoginURL());
            connection = (HttpsURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            writeOutput(connection, Configuration.getLoginString().getBytes());
            consumeInput(connection);

            url = new URL(Configuration.getQueryURL(satelliteNumber));
            elements = getInput(url);

            url = new URL(Configuration.getLogoutURL());
            consumeInput(url);

        } finally {
            if (connection != null)
                connection.disconnect();
        }

        return elements;
    }

    private static void consumeInput(HttpsURLConnection connection) throws IOException {
        connection.getInputStream();
    }

    private static void consumeInput(URL url) throws IOException {
        url.openStream();
    }

    private static String getInput(URL url) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));

        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
            sb.append("\r\n");
        }

        return sb.toString();
    }


    private static void manageCookies() {
        CookieManager manager = new CookieManager();
        manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(manager);
    }

    private static void writeOutput(HttpsURLConnection connection, byte[] bytes) throws IOException {
        OutputStream os = connection.getOutputStream();
        os.write(bytes);
        os.flush();
    }

    private static class Configuration
    {
        private final static String USERNAME = "stephan.hoedtke@gmx.de";
        private final static String PASSWORD = "in.salutari.tuo";

        static String getLoginURL() {
            return "https://www.space-track.org/ajaxauth/login";
        }

        static String getLoginString() {
            return "identity=" + USERNAME + "&password=" + PASSWORD;
        }

        static String getQueryURL(String satelliteNumber) {
            return "https://www.space-track.org/basicspacedata/query/class/tle_latest/NORAD_CAT_ID/" + satelliteNumber + "/format/tle/ORDINAL/1/";
        }

        static String getLogoutURL() {
            return "https://www.space-track.org/ajaxauth/logout";
        }
    }
}