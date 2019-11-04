package com.stho.software.nyota;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.stho.software.nyota.universe.IElement;
import com.stho.software.nyota.universe.Universe;
import com.stho.software.nyota.utilities.City;
import com.stho.software.nyota.utilities.Moment;
import com.stho.software.nyota.utilities.UTC;

public class Framework {

    static final int RQ_SETTINGS = 1001;
    static final int RQ_ELEMENT = 1002;
    static final int RQ_CITY = 1003;
    static final int RQ_TIME = 1004;

    private static final String KEY_UPDATE_LOCATION = "UPDATE_LOCATION";
    private static final String KEY_UPDATE_TIME = "UPDATE_TIME";
    private static final String KEY_ELEMENT = "ELEMENT";
    private static final String KEY_CITY = "CITY";
    private static final String KEY_TIME = "TIME";

    static void saveToInstantState(Bundle state, boolean updateLocationAutomatically, boolean updateTimeAutomatically, Moment moment) {
        state.putBoolean(KEY_UPDATE_LOCATION, updateLocationAutomatically);
        state.putBoolean(KEY_UPDATE_TIME, updateTimeAutomatically);
        state.putString(KEY_CITY, moment.getCity().serialize());
        state.putString(KEY_TIME, moment.getUTC().serialize());
    }

    static void preventFromSleeping(Activity activity) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public static boolean getUpdateLocationAutomatically(Bundle state) {
        return state.getBoolean(KEY_UPDATE_LOCATION, true);
    }

    public static boolean getUpdateTimeAutomatically(Bundle state) {
        return state.getBoolean(KEY_UPDATE_TIME, true);
    }

    public static City getCity(Bundle state) {
        return City.deserialize(state.getString(KEY_CITY));
    }

    public static UTC getTime(Bundle state) {
        return UTC.deserialize(state.getString(KEY_TIME));
    }

    static void openElementActivity(Activity activity, IElement element, Moment moment) {
        openActivity(activity, element, moment, ActivityFactory.getElementActivityClass(element), RQ_ELEMENT);
    }

    static void openPreferencesActivity(Activity activity) {
        Intent intent = new Intent(activity, SettingsActivity.class);
        activity.startActivityForResult(intent, RQ_SETTINGS);
    }

    static void openCitySelectionActivity(Activity activity, Moment moment) {
        openActivity(activity, moment.getCity(), CitySelectionActivity.class, RQ_CITY);
    }

    static void openCityActivity(Activity activity) {
        openActivity(activity, CityActivity.class, RQ_CITY);
    }

    static void openCityActivity(Activity activity, City city) {
        openActivity(activity, city, CityActivity.class, RQ_CITY);
    }

    static void openTimeSelectionActivity(Activity activity, Moment moment) {
        openActivity(activity, moment.getCity(), moment.getUTC(), TimeSelectionActivity.class, RQ_TIME);
    }

    static void openSkyViewActivity(Activity activity, IElement element, Moment moment) {
        openActivity(activity, element, moment, SkyViewActivity.class, RQ_ELEMENT);
    }

    static void openFinderActivity(Activity activity, IElement element, Moment moment) {
        openActivity(activity, element, moment, FinderActivity.class, RQ_ELEMENT);
    }

    static void openMapActivity(Activity activity, IElement element, Moment moment) {
        openActivity(activity, element, moment, ISSMapViewActivity.class, RQ_ELEMENT);
    }

    static void openTLELoaderActivity(Activity activity, IElement element, Moment moment) {
        openActivity(activity, element, moment, TLELoaderActivity.class, RQ_ELEMENT);
    }

    private static void openActivity(Activity activity, Class<?> cls) {
        Intent intent = new Intent(activity, cls);
        activity.startActivity(intent);
    }

    private static void openActivity(Activity activity, Class<?> cls, int requestCode) {
        Intent intent = new Intent(activity, cls);
        activity.startActivityForResult(intent, requestCode);
    }

    private static void openActivity(Activity activity, City city, Class<?> cls, int requestCode) {
        Intent intent = new Intent(activity, cls);
        intent.putExtra(KEY_CITY, city.serialize());
        activity.startActivityForResult(intent, requestCode);
    }

    private static void openActivity(Activity activity,City city, UTC utc, Class<?> cls, int requestCode) {
        Intent intent = new Intent(activity, cls);
        intent.putExtra(KEY_CITY, city.serialize());
        intent.putExtra(KEY_TIME, utc.serialize());
        activity.startActivityForResult(intent, requestCode);
    }

    private static void openActivity(Activity activity, IElement element, Moment moment, Class<?> cls, int requestCode) {
        Intent intent = new Intent(activity, cls);
        intent.putExtra(KEY_ELEMENT, element.getName());
        intent.putExtra(KEY_CITY, moment.getCity().serialize());
        intent.putExtra(KEY_TIME, moment.getUTC().serialize());
        activity.startActivityForResult(intent, requestCode);
    }

    static City getCity(Intent intent) {
        return City.deserialize(intent.getStringExtra(KEY_CITY));
    }

    static UTC getTime(Intent intent) {
        return UTC.deserialize(intent.getStringExtra(KEY_TIME));
    }

    static boolean getUpdateLocationAutomatically(Intent intent) {
        return intent.getBooleanExtra(KEY_UPDATE_LOCATION, false);
    }

    static boolean getUpdateTimeAutomatically(Intent intent) {
        return intent.getBooleanExtra(KEY_UPDATE_TIME, true);
    }

    static IElement getElement(Intent intent, Universe universe) {
        String name = intent.getStringExtra(KEY_ELEMENT);
        return universe.getElementByName(name);
    }

    @SuppressWarnings("UnusedReturnValue")
    static boolean finishActivity(Activity activity, City city) {
        if (city != null) {
            //Framework.toast(activity, "Return: City: " + city.toString());
            Intent intent = new Intent();
            intent.putExtra(KEY_CITY, city.serialize());
            intent.putExtra(KEY_UPDATE_LOCATION, city.isAutomatic());
            activity.setResult(Activity.RESULT_OK, intent);
            activity.finish();
            return true;
        }
        return false;
    }

    @SuppressWarnings("UnusedReturnValue")
    static boolean finishActivity(Activity activity, City city, UTC utc, boolean updateTimeAutomatically) {
        if (city != null && utc != null) {
            //Framework.toast(activity, "Return: City: " + city.toString() + " Time: " + utc.toString());
            Intent intent = new Intent();
            intent.putExtra(KEY_CITY, city.serialize());
            intent.putExtra(KEY_TIME, utc.serialize());
            intent.putExtra(KEY_UPDATE_LOCATION, city.isAutomatic());
            intent.putExtra(KEY_UPDATE_TIME, updateTimeAutomatically);
            activity.setResult(Activity.RESULT_OK, intent);
            activity.finish();
            return true;
        }
        return false;
    }

    static void toast(Activity activity, int stringId) {
        String text = activity.getResources().getString(stringId);
        toast(activity, text);
    }

    static void toast(Activity activity, String text) {
        Toast toast = Toast.makeText(activity, text, Toast.LENGTH_LONG);
        View view = toast.getView();
        view.setBackgroundColor(Color.TRANSPARENT);
        TextView textView = (TextView) view.findViewById(android.R.id.message);
        //textView.setShadowLayer(0, 0, 0, Color.TRANSPARENT);
        textView.setBackgroundColor(Color.YELLOW); // Color.TRANSPARENT);
        textView.setTextColor(Color.BLUE);
        toast.show();
    }
}


