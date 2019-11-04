package com.stho.software.nyota;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.stho.software.nyota.data.Settings;
import com.stho.software.nyota.universe.IElement;
import com.stho.software.nyota.universe.Universe;
import com.stho.software.nyota.utilities.Formatter;
import com.stho.software.nyota.utilities.Moment;
import com.stho.software.nyota.viewmodels.NyotaViewModel;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

abstract class AbstractElementActivity extends AppCompatActivity { // not enable the action appbar Activity {

    private class ViewHolder {
        TextView time;
        ImageView visibility;
        BottomAppBar appbar;
        FloatingActionButton showAppbarFab;
        ImageView hideButton;
        ImageView playButton;
        ImageView pauseButton;
        TextView timeInterval;
        ImageView downButton;
        ImageView upButton;
    }

    private final ViewHolder viewHolder = new ViewHolder();
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

    }

    protected void setupViews() {
        viewHolder.time = (TextView) findViewById(R.id.currentTime);
        viewHolder.visibility = (ImageView) findViewById(R.id.currentVisibility);
    }

    protected void updateUniverseUI(Universe universe) {

    }

    protected void updateUI() {
        Moment moment = getMoment();
        IElement element = getElement();
        displayTitle(moment, element);
        updateUI(moment, element);
    }

    private void updateTimeUI(boolean automatically) {
        viewHolder.playButton.setVisibility(automatically ? View.INVISIBLE : View.VISIBLE);
        viewHolder.pauseButton.setVisibility(automatically ? View.VISIBLE : View.INVISIBLE);
        //setUpdateListenerStatus(automatically);
    }

    abstract protected void updateUI(Moment moment, IElement element);

    protected void displayTitle(Moment moment, IElement element) {
        if (viewHolder.time != null)
            viewHolder.time.setText(Formatter.toString(moment.getUTC(), moment.getTimeZone(), Formatter.TimeFormat.TIME));

        if (viewHolder.visibility != null)
            viewHolder.visibility.setImageResource(element.getVisibility());

        setTitle(element.getName()
                + Formatter.SPACE
                + Formatter.toString(moment.getUTC(), moment.getTimeZone(), Formatter.TimeFormat.DATE_TIMEZONE));
    }

    private Universe getUniverse() {
        return viewModel.getUniverse().getValue();
    }

    protected IElement getElement() {
        return Framework.getElement(getIntent(), getUniverse());
    }

    private NyotaViewModel.TimeInterval getTimeInterval() {
        return viewModel.getTimeInterval().getValue();
    }

    private Moment getMoment() {
        return getUniverse().getMoment();
    }

    protected void setupToolbar() {
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
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

    private void updateTimeIntervalUI(NyotaViewModel.TimeInterval timeInterval) {
        viewHolder.timeInterval.setText(timeInterval.toString());
    }

    private void setupFloatingActionBar() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menuSettings:
                Framework.openPreferencesActivity(this);
                return true;

            case R.id.menuSkyView:
                Framework.openSkyViewActivity(AbstractElementActivity.this, getElement(), getMoment());
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Framework.RQ_SETTINGS:
                // Load from settings what needs to be loaded... (nothing for now);
                // whatever the resultCode is (settings is closed with 0)
                updateUI();
                break;

        }
    }
}




