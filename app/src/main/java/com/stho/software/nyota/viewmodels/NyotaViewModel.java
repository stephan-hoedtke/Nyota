package com.stho.software.nyota.viewmodels;

import android.content.Context;
import android.os.Bundle;

import com.stho.software.nyota.Framework;
import com.stho.software.nyota.data.Settings;
import com.stho.software.nyota.utilities.City;
import com.stho.software.nyota.utilities.Moment;
import com.stho.software.nyota.universe.Universe;
import com.stho.software.nyota.utilities.UTC;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class NyotaViewModel extends ViewModel {

    public enum TimeInterval {
        YEAR,
        MONTH,
        DAY,
        HOUR,
        MINUTE,
    }

    private City city = null;
    private UTC utc = null;
    private final Universe universe = Universe.getInstance();
    private final MediatorLiveData<Universe> universeLiveData = new MediatorLiveData<>(); // reacts on changes of _moment and city
    private final MutableLiveData<Boolean> updateLocationAutomaticallyLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> updateTimeAutomaticallyLiveData = new MutableLiveData<>();
    private final MutableLiveData<TimeInterval> timeIntervalLiveData = new MutableLiveData<>();

    public LiveData<Universe> getUniverse() { return universeLiveData; }
    public LiveData<Moment> getMoment() { return Transformations.map(universeLiveData, universe -> universe.getMoment()); }
    public LiveData<Boolean> getUpdateLocationAutomatically() { return updateLocationAutomaticallyLiveData; }
    public LiveData<Boolean> getUpdateTimeAutomatically() { return updateTimeAutomaticallyLiveData; }
    public LiveData<TimeInterval> getTimeInterval() { return timeIntervalLiveData; }

    public void setUpdateTimeAutomatically(boolean automatically) { this.updateTimeAutomaticallyLiveData.setValue(automatically); }
    public void setUpdateLocationAutomatically(boolean automatically) { this.updateLocationAutomaticallyLiveData.setValue(automatically);}
    public void setTimeInterval(TimeInterval timeInterval) { this.timeIntervalLiveData.setValue(timeInterval); }

    public NyotaViewModel() {
        timeIntervalLiveData.setValue(TimeInterval.HOUR);
        updateLocationAutomaticallyLiveData.setValue(true);
        updateTimeAutomaticallyLiveData.setValue(true);
        setMoment(Moment.forNow(City.createNewDefaultBerlin()));
    }

    public void gotoNextMoment() {
        updateMomentBy(getTimeInterval().getValue(), +1);
    }

    public void gotoPreviousMoment() {
        updateMomentBy(getTimeInterval().getValue(), -1);
    }

    private void updateMomentBy(TimeInterval interval, double value) {
        updateTimeAutomaticallyLiveData.setValue(false);
        updateLocationAutomaticallyLiveData.setValue(false);
        switch (interval) {
            case MINUTE:
                setMoment(universe.getMoment().addMillis(60 * 1000));
                break;

            case HOUR:
                setMoment(universe.getMoment().addHours(value));
                break;

            case DAY:
                setMoment(universe.getMoment().addHours(24 * value));
                break;

            case MONTH:
                setMoment(universe.getMoment().addMonths((int)value));
                break;

            case YEAR:
                setMoment(universe.getMoment().addYears((int)value));
                break;
        }
    }

    /**
     * Provide city with timezone and UTC as _moment
     * @param moment
     */
    public void setMoment(Moment moment) {
        universe.updateFor(moment);
        universeLiveData.setValue(universe);
    }

    /**
     * Provide city with timezone and UTC as _moment
     * @param moment
     * @param calculatePhase
     */
    public void setMoment(Moment moment, boolean calculatePhase) {
        universe.updateFor(moment, calculatePhase);
        universeLiveData.setValue(universe);
    }
    /**
     * Provide city with timezone, use current time
     * @param city
     */
    public void setCity(City city) {
        if (city.isAutomatic()) {
            setMoment(Moment.forNow(city));
        } else {
            this.city = city;
            setMoment(Moment.forNow(city));
        }
    }

    public void setUTC(UTC utc) {
        this.utc = utc;
        setMoment(universe.getMoment().forUTC(utc));
    }



    /**
     * Keep city and timezone, but use current time
     */
    public void forNow() {
        setMoment(universe.getMomentForNow());
    }

    public void initializeUniverseFromSettings(Context context) {
        if (context != null) {
            Settings settings = new Settings();
            settings.load(context);

            universe.setSatellite("ISS", settings.issElements);

            if (settings.getUpdateLocationAutomatically()) {
                universe.updateFor(Moment.forNow(City.createNewDefaultBerlin()));
                updateLocationAutomaticallyLiveData.setValue(true);
                updateTimeAutomaticallyLiveData.setValue(true);
            } else if (settings.getUpdateTimeAutomatically()) {
                city = settings.city;
                if (city == null)
                    city = City.createNewDefaultBerlin();
                universe.updateFor(Moment.forNow(city));
                updateLocationAutomaticallyLiveData.setValue(false);
                updateTimeAutomaticallyLiveData.setValue(true);
            } else {
                if (city == null)
                    city = City.createNewDefaultBerlin();
                utc = settings.utc;
                if (utc == null)
                    utc = UTC.forNow();
                universe.updateFor(Moment.forUTC(city, utc));
                updateLocationAutomaticallyLiveData.setValue(false);
                updateTimeAutomaticallyLiveData.setValue(false);
            }
        }
    }

    public void initializeUniverseFromInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (Framework.getUpdateLocationAutomatically(savedInstanceState)) {
                universe.updateFor(Moment.forNow(City.createNewDefaultBerlin()));
                updateLocationAutomaticallyLiveData.setValue(true);
                updateTimeAutomaticallyLiveData.setValue(true);
            } else if (Framework.getUpdateTimeAutomatically(savedInstanceState)) {
                city = Framework.getCity(savedInstanceState);
                universe.updateFor(Moment.forNow(city));
                updateLocationAutomaticallyLiveData.setValue(false);
                updateTimeAutomaticallyLiveData.setValue(true);
            } else {
                city = Framework.getCity(savedInstanceState);
                utc = Framework.getTime(savedInstanceState);
                universe.updateFor(Moment.forUTC(city, utc));
                updateLocationAutomaticallyLiveData.setValue(false);
                updateTimeAutomaticallyLiveData.setValue(false);
            }
        }
    }
}



