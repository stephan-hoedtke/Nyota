package com.stho.software.nyota;

import android.app.AlertDialog;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.stho.software.nyota.utilities.City;
import com.stho.software.nyota.utilities.Formatter;

public class CityActivity extends Activity implements LocationListener {

    private static class ViewHolder {
        TextView name;
        TextView latitude;
        TextView longitude;
        TextView altitude;
        TextView timeZone;
        CheckBox automatic;
        Spinner listTimeZones;
    }

    private ViewHolder viewHolder = new ViewHolder();
    private Handler handler = new Handler();
    private City city = null;
    private final LocationListenerAdapter locationListenerAdapter = new LocationListenerAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);
        viewHolder.name = findViewById(R.id.editName);
        viewHolder.latitude = findViewById(R.id.editLatitude);
        viewHolder.longitude = findViewById(R.id.editLongitude);
        viewHolder.altitude = findViewById(R.id.editAltitude);
        viewHolder.timeZone = findViewById(R.id.editTimeZone);
        viewHolder.automatic = findViewById(R.id.checkBoxAutomatic);
        viewHolder.listTimeZones = findViewById(R.id.listTimeZones);

        city = Framework.getCity(getIntent());

        if (city == null)
            city = City.createNewDefaultBerlin();

        ArrayAdapter adapter=new ArrayAdapter<String>(CityActivity.this, android.R.layout.simple_spinner_item, City.getTimeZones());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        viewHolder.listTimeZones.setAdapter(adapter);

        findViewById(R.id.buttonAccept).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               try {
                   String name = viewHolder.name.getText().toString();
                   double latitude = Double.parseDouble(viewHolder.latitude.getText().toString());
                   double longitude = Double.parseDouble(viewHolder.longitude.getText().toString());
                   double altitude = Double.parseDouble(viewHolder.altitude.getText().toString());
                   String timeZone = viewHolder.timeZone.getText().toString();
                   boolean automatic = viewHolder.automatic.isChecked();
                   if (city == null) {
                       city = City.createNewDefaultBerlin();
                   }
                   city.setName(name);
                   city.setLocation(latitude, longitude, altitude);
                   city.setTimeZone(timeZone);
                   city.setAutomatic(automatic);
                   Framework.finishActivity(CityActivity.this, city);
               } catch (Exception ex) {
                   showError(ex.toString());
               }
           }
        });

        findViewById(R.id.buttonLocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableLocationListener();
            }
        });

        updateUI();
    }

    void updateUI() {
        if (city != null) {
            viewHolder.name.setText(city.getName());
            viewHolder.latitude.setText(Formatter.df3.format(city.getLatitude()));
            viewHolder.longitude.setText(Formatter.df3.format(city.getLongitude()));
            viewHolder.altitude.setText(Formatter.df3.format(city.getAltitude()));
            viewHolder.timeZone.setText(city.getTimeZone().getID());
            viewHolder.automatic.setChecked(city.isAutomatic());
        }
    }

    @Override
    protected void onPause() {
        disableLocationListener();
        super.onPause();
    }

    private void enableLocationListener() {
        locationListenerAdapter.enableLocationListener(this, true);
    }

    private void disableLocationListener() {
        locationListenerAdapter.disableLocationListener(this);
        handler.removeCallbacks(null);
    }

    private void showError(final String errorMessage) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder alert = new AlertDialog.Builder(CityActivity.this);
                alert.setTitle("Error");
                alert.setMessage(errorMessage);
                alert.setPositiveButton("OK",null);
                alert.show();
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        if (city != null) {
            if (city.isNear(location)) {
                if (locationListenerAdapter.isStable()) {
                    disableLocationListener(); // two times the same location --> once we have a stable location, we don't need it anymore.
                }
            }
            else {
                city.setLocation(location);
                locationListenerAdapter.reset();
            }
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}

