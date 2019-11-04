package com.stho.software.nyota;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.stho.software.nyota.data.Settings;
import com.stho.software.nyota.universe.IElement;
import com.stho.software.nyota.universe.Universe;
import com.stho.software.nyota.utilities.Moment;
import com.stho.software.nyota.views.SkyView;

public class SkyViewActivity extends AbstractElementActivity implements SensorEventListener, ISkyActivity {

    private final static double ZOOM_IN = 1.10;
    private final static double ZOOM_OUT = 1 / ZOOM_IN;
    private final OrientationListenerAdapter orientationListenerAdapter = new OrientationListenerAdapter();
    private Handler handler = new Handler();
    private boolean isFrozen = true;
    private final ViewHolder holder = new ViewHolder();
    private final Settings settings = new Settings();

    private class ViewHolder {
        Button refresh;
        SkyView sky;
        TextView orientation;
        TextView horizontal;
        TextView center;
        ImageView freeze;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Framework.preventFromSleeping(this);
        settings.load(getBaseContext());
        setupViews();
        setupToolbar();
        updateUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sky_view_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // the "up"-button in the toolbar was pressed: take it as "back"...
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void setupViews() {
        setContentView(R.layout.activity_sky_view);
        View up = findViewById(R.id.up);
        if (up != null) {
            up.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.sky.applyScale(ZOOM_IN);
                }
            });
        }
        View down = findViewById(R.id.down);
        if (down != null) {
            down.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.sky.applyScale(ZOOM_OUT);
                }
            });
        }
        holder.sky = findViewById(R.id.skyView);
        holder.sky.setZoomAngle(60);
        holder.sky.register(this);
        holder.sky.setReference(getElement());
        holder.orientation = findViewById(R.id.orientation);
        holder.horizontal = findViewById(R.id.horizontal);
        holder.center = findViewById(R.id.center);
        holder.refresh = findViewById(R.id.refresh);
        holder.refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUpdateStatus(isFrozen);
            }
        });
        holder.freeze = findViewById(R.id.iconFreeze);
        holder.freeze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUpdateStatus(isFrozen);
            }
        });
        super.setupViews();
    }

    private void setUpdateStatus(boolean updateAutomatically) {
        if (updateAutomatically) {
            enableCompassListener();
            enableTimer();
            isFrozen = false;
            updateUI();
        } else {
            disableCompassListener();
            disableTimer();
            isFrozen = true;
            updateUI();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        enableCompassListener();
        enableTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        disableCompassListener();
        disableTimer();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        orientationListenerAdapter.onSensorChanged(event);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void enableCompassListener() {
        orientationListenerAdapter.enableCompassListener(this);
    }

    private void disableCompassListener() {
        orientationListenerAdapter.disableCompassListener(this);
    }

    private void enableTimer() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateUI();
                handler.postDelayed(this, 100);
            }
        }, 100);
    }

    private void disableTimer() {
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void updateUI(Moment moment, IElement element) {

        Universe.getInstance().updateForNow();

        holder.freeze.setImageResource(isFrozen ? R.drawable.frozen : R.drawable.melted);
        holder.refresh.setText(getResources().getString(isFrozen ? R.string.label_disabled : R.string.label_enabled));

        if (!isFrozen) {
            if (settings.flatFinder)
                holder.sky.setCenter(orientationListenerAdapter.getOrientation().getAzimuth(), orientationListenerAdapter.getOrientation().getDirection());
            else
                holder.sky.setCenter(orientationListenerAdapter.getOrientation().getAzimuth(), orientationListenerAdapter.getOrientation().getAltitude());
        }

        holder.orientation.setText(orientationListenerAdapter.getOrientation().toString(settings.flatFinder));
        holder.horizontal.setText(getElement().getPosition().toString());
        holder.center.setText(holder.sky.getCenter().toString());

        holder.sky.updateUI();
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        Framework.openFinderActivity(this, getElement(), getMoment());
        return false;
    }

    @Override
    public void onChangeSkyCenter() {
        updateUI();
    }
}



