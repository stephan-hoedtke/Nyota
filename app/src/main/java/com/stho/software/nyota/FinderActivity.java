package com.stho.software.nyota;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.stho.software.nyota.data.Settings;
import com.stho.software.nyota.universe.IElement;
import com.stho.software.nyota.universe.Universe;
import com.stho.software.nyota.utilities.Moment;
import com.stho.software.nyota.views.DirectionPointerView;
import com.stho.software.nyota.views.HorizonView;

public final class FinderActivity extends AbstractElementActivity implements SensorEventListener, View.OnClickListener {

    private static class ViewHolder {
        private DirectionPointerView direction;
        private HorizonView horizon;
        private TextView orientation;
        private TextView horizontal;
        private Button refresh;
    }

    private final ViewHolder holder = new ViewHolder();
    private final Settings settings = new Settings();
    private final OrientationListenerAdapter orientationListenerAdapter = new OrientationListenerAdapter();
    private final Handler handler = new Handler();
    private boolean refreshing = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeUniverse();
        Framework.preventFromSleeping(this);
        settings.load(getBaseContext());
        setupViews();
        updateUI();
    }

    private void initializeUniverse() {
        Universe.getInstance().updateForNow();
    }

    @Override
    protected void setupViews() {
        setContentView(R.layout.activity_view_finder);

        holder.direction = findViewById(R.id.directionView);
        holder.direction.setOnClickListener(this);
        holder.horizon = findViewById(R.id.horizonView);
        holder.horizon.setFlat(settings.flatFinder);
        holder.horizon.setOnClickListener(this);
        holder.orientation = findViewById(R.id.orientation);
        holder.horizontal = findViewById(R.id.horizontal);
        holder.refresh = findViewById(R.id.refresh);
        holder.refresh.setOnClickListener(this);
        holder.refresh.setText(getResources().getString(refreshing ? R.string.label_enabled : R.string.label_disabled));

        super.setupViews();
    }

    @Override
    public void onClick(View v) {
        refreshing = !refreshing;
        holder.refresh.setText(getResources().getString(refreshing ? R.string.label_enabled : R.string.label_disabled));
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
                Universe.getInstance().updateForNow();
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

        holder.orientation.setText(orientationListenerAdapter.getOrientation().toString(settings.flatFinder));
        holder.horizontal.setText(element.getPosition().toString());

        // The compass will be refreshing only if wanted...
        if (refreshing)
            holder.direction.setDirection(element.getPosition(), orientationListenerAdapter.getOrientation());

        // The artificial horizon will refresh anyway...
        holder.horizon.setDirection(element.getPosition(), orientationListenerAdapter.getOrientation());
    }
}
