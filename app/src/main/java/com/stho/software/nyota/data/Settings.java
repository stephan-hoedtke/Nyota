package com.stho.software.nyota.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.stho.software.nyota.utilities.City;
import com.stho.software.nyota.utilities.UTC;

/**
 * Created by shoedtke on 08.09.2016.
 */
public class Settings {

    private static final String KEY_FLAT_FINDER = "FLAT_FINDER";
    private static final String KEY_DISPLAY_NAMES = "DISPLAY_NAMES";
    private static final String KEY_DISPLAY_SYMBOLS = "DISPLAY_SYMBOLS";
    private static final String KEY_DISPLAY_MAGNITUDE = "DISPLAY_MAGNITUDE";
    private static final String KEY_ISS_ELEMENTS = "ISS_ELEMENTS";
    private static final String KEY_CITY = "CITY";
    private static final String KEY_TIME = "TIME";
    private static final String KEY_FLAGS = "FLAGS";
    private static final int FLAG_UPDATE_LOCATION_AUTOMATICALLY = 0x01;
    private static final int FLAG_UPDATE_TIME_AUTOMATICALLY = 0x02;


    public boolean flatFinder = true;
    public boolean displayNames = true;
    public boolean displaySymbols = true;
    public boolean displayMagnitude = true;
    public String issElements = "";
    public City city = null;
    public UTC utc = null;
    public boolean showPositionInfo = true;
    public boolean showOrientationInfo = true;
    public boolean showCamera = false;
    public boolean showSky = true;
    public boolean showPlanetInfo = true;

    private int flags = 0;

    public void load(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            flatFinder = preferences.getBoolean(KEY_FLAT_FINDER, flatFinder);
            displayNames = preferences.getBoolean(KEY_DISPLAY_NAMES, displayNames);
            displaySymbols = preferences.getBoolean(KEY_DISPLAY_SYMBOLS, displaySymbols);
            displayMagnitude = preferences.getBoolean(KEY_DISPLAY_MAGNITUDE, displayMagnitude);
            issElements = preferences.getString(KEY_ISS_ELEMENTS, issElements);
            city = City.deserialize(preferences.getString(KEY_CITY, ""));
            utc = UTC.deserialize(preferences.getString(KEY_TIME, ""));
            flags = preferences.getInt(KEY_FLAGS, 0);
        }
        catch (Exception ex) {
            // ignore
        }
    }

    public void save(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_FLAT_FINDER, flatFinder);
        editor.putBoolean(KEY_DISPLAY_NAMES, displayNames);
        editor.putBoolean(KEY_DISPLAY_SYMBOLS, displaySymbols);
        editor.putBoolean(KEY_DISPLAY_MAGNITUDE, displayMagnitude);
        editor.putString(KEY_ISS_ELEMENTS, issElements);
        editor.putString(KEY_CITY, (city == null) ? "" : city.serialize());
        editor.putString(KEY_TIME, (utc != null) ? "" : utc.serialize());
        editor.putInt(KEY_FLAGS, flags);
        editor.apply();
    }

    void setMoment(City city, UTC utc, boolean updateLocationAutomatically, boolean updateTimeAutomatically) {
        this.city = city;
        this.utc = utc;
        this.flags = Settings.setFlags(this.flags, updateLocationAutomatically, updateTimeAutomatically);
    }

    public boolean getUpdateLocationAutomatically() {
        return (flags & FLAG_UPDATE_LOCATION_AUTOMATICALLY) == FLAG_UPDATE_LOCATION_AUTOMATICALLY;
    }

    public boolean getUpdateTimeAutomatically() {
        return (flags & FLAG_UPDATE_TIME_AUTOMATICALLY) == FLAG_UPDATE_TIME_AUTOMATICALLY;
    }

    private static int setFlags(int flags, boolean updateLocationAutomatically, boolean updateTimeAutomatically) {
        if (updateLocationAutomatically) {
            flags |= FLAG_UPDATE_LOCATION_AUTOMATICALLY;
        } else {
            flags &= ~FLAG_UPDATE_LOCATION_AUTOMATICALLY;
        }
        if (updateTimeAutomatically) {
            flags |= FLAG_UPDATE_TIME_AUTOMATICALLY;
        } else {
            flags &= ~FLAG_UPDATE_TIME_AUTOMATICALLY;
        }
        return flags;
    }
}

