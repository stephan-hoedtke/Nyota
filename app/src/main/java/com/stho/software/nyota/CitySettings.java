package com.stho.software.nyota;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.stho.software.nyota.utilities.City;
import com.stho.software.nyota.utilities.CityList;

/**
 * Created by shoedtke on 08.09.2016.
 */
public class CitySettings {

    private static final String KEY_CITIES = "CITIES";

    CityList cities = new CityList();

    void load(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            cities = CityList.deserialize(preferences.getString(KEY_CITIES, ""));
        }
        catch (Exception ex) {
            // ignore
        }
    }

    void save(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_CITIES, cities.serialize());
        editor.apply();
    }

    static City getSelectedCityOrDefault(Context context) {
        CityList cities = new CityList();
        try {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            cities = CityList.deserialize(preferences.getString(KEY_CITIES, ""));
        } catch (Exception ex) {
            // ignore
        }
        return cities.getSelectedCityOrDefault();
    }
}

