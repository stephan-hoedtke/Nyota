package com.stho.software.nyota;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.ui.AppBarConfiguration;

import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.stho.software.nyota.data.Settings;
import com.stho.software.nyota.universe.IElement;
import com.stho.software.nyota.universe.Universe;
import com.stho.software.nyota.utilities.City;
import com.stho.software.nyota.utilities.Moment;
import com.stho.software.nyota.utilities.UTC;
import com.stho.software.nyota.viewmodels.NyotaViewModel;

import java.util.ArrayList;


public final class MainActivity extends AppCompatActivity implements LocationListener, SensorEventListener {

    private static class ViewHolder {
        private ElementsListAdapter adapter;
        private BasicsViewAdapter basicsAdapter;
        private ListView elementsListView;
        BottomAppBar appbar;
        FloatingActionButton showAppbarFab;
        ImageView hideButton;
        ImageView playButton;
        ImageView pauseButton;
        TextView timeInterval;
        ImageView downButton;
        ImageView upButton;
    }

    private Moment cacheMoment = null;
    private final Handler handler = new Handler();
    private final ArrayList<IElement> elements = new ArrayList<>();
    private final ViewHolder viewHolder = new ViewHolder();
    private final LocationListenerAdapter locationListenerAdapter = new LocationListenerAdapter();
    private final OrientationListenerAdapter orientationListenerAdapter = new OrientationListenerAdapter();
    private final Settings settings = new Settings();
    private NyotaViewModel viewModel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings.load(this);
        viewModel = ViewModelProviders.of(this).get(NyotaViewModel.class);
        viewModel.getUniverse().observe(this, universe -> updateUniverseUI(universe));
        viewModel.getUpdateTimeAutomatically().observe(this, automatically -> updateTimeUI(automatically));
        viewModel.initializeUniverseFromSettings(this);
        viewModel.initializeUniverseFromInstanceState(savedInstanceState);
        Framework.preventFromSleeping(this);
        setupViews();
        setupElements();
        setupToolbar();
        setupAppbar();
        setupNavigation();
    }

    /**
     *  https://developer.android.com/training/appbar/setting-up
     */
    private void setupToolbar() {
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private Universe getUniverse() {
        return viewModel.getUniverse().getValue();
    }

    private NyotaViewModel.TimeInterval getTimeInterval() {
        return viewModel.getTimeInterval().getValue();
    }

    private Moment getMoment() {
        return getUniverse().getMoment();
    }

    private boolean getUpdateLocationAutomatically() {
        return viewModel.getUpdateLocationAutomatically().getValue();
    }

    private boolean getUpdateTimeAutomatically() {
        return viewModel.getUpdateTimeAutomatically().getValue();
    }


    private void setupElements() {
        final Universe universe = getUniverse();
        elements.add(universe.findSatelliteByName("ISS"));
        elements.add(universe.solarSystem.moon);
        elements.add(universe.solarSystem.sun);
        elements.add(universe.solarSystem.mercury);
        elements.add(universe.solarSystem.venus);
        elements.add(universe.solarSystem.mars);
        elements.add(universe.solarSystem.jupiter);
        elements.add(universe.solarSystem.saturn);
        elements.add(universe.solarSystem.uranus);
        elements.add(universe.solarSystem.neptune);
        elements.addAll(universe.constellations.values());
        elements.addAll(universe.vip);
    }

    private void setUpdateListenerStatus(boolean updateAutomatically) {
        if (updateAutomatically) {
            enableCompassListener();
            enableTimer();
        }
        else {
            disableCompassListener();
            disableTimer();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final Universe universe = getUniverse();
        switch (item.getItemId()) {

            case R.id.menuSettings:
                Framework.openPreferencesActivity(this);
                return true;

            case R.id.menuVersion:
                Framework.toast(MainActivity.this, BuildConfig.VERSION_NAME);
                return true;

            case R.id.menuSkyView:
                Framework.openSkyViewActivity(MainActivity.this, universe.getElementByName("Moon"), universe.getMoment());
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getUpdateLocationAutomatically()) {
            enableLocationListener();
            enableCompassListener();
        }
        enableTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        disableLocationListener();
        disableCompassListener();
        disableTimer();
    }

    @Override
    public void onLocationChanged(android.location.Location location) {
        /*
         Update local variable: cacheMoment only. Do not update the universe here (avoid too frequent updates).
         */
        if (getUpdateLocationAutomatically()) {
            Moment moment = (cacheMoment == null) ? getUniverse().getMoment() : cacheMoment;
            City city = moment.getCity();
            if (city.isNear(location)) {
                if (locationListenerAdapter.isStable()) {
                    disableLocationListener(); // two times the same location --> once we have a stable location, we don't need it anymore.
                }
            } else {
                city.setLocation(location);
                cacheMoment = Moment.forNow(city);
                locationListenerAdapter.reset();
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        orientationListenerAdapter.onSensorChanged(event);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Framework.saveToInstantState(outState, getUpdateLocationAutomatically(), getUpdateTimeAutomatically(), getUniverse().getMoment());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        viewModel.initializeUniverseFromInstanceState(savedInstanceState);
    }

    private void setupViews() {
        setContentView(R.layout.activity_main);
        viewHolder.basicsAdapter = new BasicsViewAdapter(this, R.id.basicsContainer);
        viewHolder.basicsAdapter.clear();
        viewHolder.adapter = new ElementsListAdapter(this, elements);
        viewHolder.elementsListView = (ListView)findViewById(R.id.currentElements);
        viewHolder.elementsListView.setAdapter(viewHolder.adapter);
        viewHolder.elementsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                IElement element = (IElement) viewHolder.elementsListView.getItemAtPosition(position);
                Framework.openElementActivity(MainActivity.this, element, getMoment());
            }
        });
    }

    private void setupAppbar() {
        viewHolder.appbar = findViewById(R.id.appbar);
        viewHolder.appbar.setVisibility(View.INVISIBLE);
        viewHolder.hideButton = findViewById(R.id.hide);
        viewHolder.hideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.appbar.setVisibility(View.INVISIBLE);
                viewHolder.showAppbarFab.setVisibility(View.VISIBLE);
            }
        });
        viewHolder.playButton = findViewById(R.id.play);
        viewHolder.playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.playButton.setVisibility(View.GONE);
                viewModel.setUpdateTimeAutomatically(true);
                viewHolder.pauseButton.setVisibility(View.VISIBLE);
            }
        });
        viewHolder.pauseButton = findViewById(R.id.pause);
        viewHolder.pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.pauseButton.setVisibility(View.GONE);
                viewModel.setUpdateLocationAutomatically(false);
                viewModel.setUpdateTimeAutomatically(false);
                viewHolder.playButton.setVisibility(View.VISIBLE);
            }
        });
        viewHolder.showAppbarFab = findViewById(R.id.showAppbarFab);
        viewHolder.showAppbarFab.setVisibility(View.VISIBLE);
        viewHolder.showAppbarFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.showAppbarFab.setVisibility(View.INVISIBLE);
                viewHolder.appbar.setVisibility(View.VISIBLE);
            }
        });
        viewHolder.timeInterval = findViewById(R.id.timeInterval);
        viewHolder.downButton = findViewById(R.id.down);
        viewHolder.downButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.gotoPreviousMoment();
            }
        });
        viewHolder.upButton = findViewById(R.id.up);
        viewHolder.upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.gotoNextMoment();
            }
        });
        registerForContextMenu(viewHolder.timeInterval);
        viewModel.getTimeInterval().observe(this, timeInterval -> updateTimeIntervalUI(timeInterval));

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_minute:
                viewModel.setTimeInterval(NyotaViewModel.TimeInterval.MINUTE);
                return true;

            case R.id.menu_hour:
                viewModel.setTimeInterval(NyotaViewModel.TimeInterval.HOUR);
                return true;

            case R.id.menu_day:
                viewModel.setTimeInterval(NyotaViewModel.TimeInterval.DAY);
                return true;

            case R.id.menu_month:
                viewModel.setTimeInterval(NyotaViewModel.TimeInterval.MONTH);
                return true;

            case R.id.menu_year:
                viewModel.setTimeInterval(NyotaViewModel.TimeInterval.YEAR);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.time_interval_menu, menu);
        menu.setHeaderTitle("Time Interval");
    }

    private void setupNavigation() {
        // TODO: Navigation: scroll between Fragments...
    }

    private final static String SPACE = "  ";

    private void updateUI() {
        updateUniverseUI(getUniverse());
        updateTimeIntervalUI(getTimeInterval());
    }

    private void updateUniverseUI(Universe universe) {
        updateBasics(universe);
        updateList();
    }

    private void updateTimeIntervalUI(NyotaViewModel.TimeInterval timeInterval) {
        viewHolder.timeInterval.setText(timeInterval.toString());
    }

    private void updateTimeUI(boolean automatically) {
        viewHolder.playButton.setVisibility(automatically ? View.INVISIBLE : View.VISIBLE);
        viewHolder.pauseButton.setVisibility(automatically ? View.VISIBLE : View.INVISIBLE);
        setUpdateListenerStatus(automatically);
    }

    private void updateBasics(final Universe universe) {
        final Moment moment = universe.getMoment();
        boolean prepare = viewHolder.basicsAdapter.prepare(4);
        viewHolder.basicsAdapter.update(0, R.drawable.navi, moment.getCity().toString());
        viewHolder.basicsAdapter.update(1, R.drawable.time, moment.toString());
        viewHolder.basicsAdapter.update(2, R.drawable.compass, orientationListenerAdapter.getOrientation().toString());
        if (prepare) {
            viewHolder.basicsAdapter.getViewHolder(0).value.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Framework.openCitySelectionActivity(MainActivity.this, getMoment());
                }
            });
            viewHolder.basicsAdapter.getViewHolder(1).value.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Framework.openTimeSelectionActivity(MainActivity.this, getMoment());
                }
            });
        }
    }

    private void updateList() {
        viewHolder.adapter.notifyDataSetChanged();
    }

    private void enableLocationListener() {
        locationListenerAdapter.enableLocationListener(this, false);
    }

    private void enableCompassListener() {
        orientationListenerAdapter.enableCompassListener(this);
    }

    private void enableTimer() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateUniverseForNow();
                handler.postDelayed(this, 2000); // every two seconds
            }
        }, 100);
    }

    private void updateUniverseForNow() {
        if (getUpdateTimeAutomatically()) {
            if (cacheMoment == null) {
                viewModel.forNow();
            } else {
                viewModel.setMoment(cacheMoment.forNow());
                cacheMoment = null;
            }
        }
    }

    private void disableLocationListener() {
        locationListenerAdapter.disableLocationListener(this);
    }

    private void disableCompassListener() {
        locationListenerAdapter.disableLocationListener(this);
    }

    private void disableTimer() {
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        City city = null;
        UTC utc = null;
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Framework.RQ_SETTINGS:
                    // Load from settings what needs to be loaded... (nothing for now);
                    updateUI();
                    break;

                case Framework.RQ_ELEMENT:
                    // just returning, nothing to be loaded.
                    updateUI();
                    break;

                case Framework.RQ_CITY:
                    city = Framework.getCity(data);
                    if (city != null) {
                        viewModel.setUpdateLocationAutomatically(false);
                        viewModel.setMoment(Moment.forNow(city));
                        viewModel.setUpdateLocationAutomatically(Framework.getUpdateLocationAutomatically(data));
                    }
                    break;

                case Framework.RQ_TIME:
                    city = Framework.getCity(data);
                    utc = Framework.getTime(data);
                    if (city != null && utc != null) {
                        viewModel.setUpdateLocationAutomatically(false);
                        viewModel.setUpdateTimeAutomatically(false);
                        viewModel.setMoment(Moment.forUTC(city, utc));
                        viewModel.setUpdateLocationAutomatically(Framework.getUpdateLocationAutomatically(data));
                        viewModel.setUpdateTimeAutomatically(Framework.getUpdateTimeAutomatically(data));
                    }
                    break;
            }
        }
    }
}


