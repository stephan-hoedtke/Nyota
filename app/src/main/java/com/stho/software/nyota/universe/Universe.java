package com.stho.software.nyota.universe;

import android.content.Context;

import com.stho.software.nyota.utilities.City;
import com.stho.software.nyota.utilities.Moment;
import com.stho.software.nyota.data.Settings;
import com.stho.software.nyota.utilities.Topocentric;

import java.util.ArrayList;
import java.util.HashMap;


// Implemented as static singleton. There is just one universe!
public class Universe {

    // singleton pattern
    private final static Universe instance = new Universe();

    private Universe() {
        new UniverseInitializer(this).initialize();
    }

    /**
     * Assuming the initialization were done already or are not required for now
     * @return
     */
    public static synchronized Universe getInstance() { return instance; }

    public void setSatellite(String name, String tle) {
        Satellite satellite = instance.findSatelliteByName(name);
        satellite.tle.deserialize(tle);
    }

    /* Solar system and stars... */

    public final SolarSystem solarSystem = new SolarSystem();

    public final HashMap<String, Star> stars = new HashMap<String, Star>();

    public final HashMap<String, Constellation> constellations = new HashMap<String, Constellation>();

    public final HashMap<String, Satellite> satellites = new HashMap<>();

    public final ArrayList<Star> vip = new ArrayList<>();

    public final ArrayList<Special> specials = new ArrayList<>();

    // The moment (time + location

    private Moment moment;

    /**
     * Returns the moment (observer + UTC) for which the Universe was calculated last
     * @return
     */
    public Moment getMoment() { return moment; }

    /**
     * Returns the moment for the current time (keep the observer)
     * @return
     */
    public Moment getMomentForNow() { return moment.forNow(); }

    /**
     * forUTC the position (azimuth and altitude) for a given location and Local Sidereal Time (in angleInHours)
     * the phase, rise times etc will not be calculated (expensive)
     * @param moment time and location
     */
    public Universe updateFor(Moment moment) {
        return updateFor(moment, false);
    }

    public Universe updateForNow() {
        Moment newMoment = getMomentForNow();
        return updateFor(newMoment, true);
    }

    /**
     * forUTC the position (azimuth and altitude) for a given location and Local Sidereal Time (in angleInHours)
     * @param moment time and location
     * @param calculatePhase whether or not the phase, rise times etc shall be calculated (expensive)
     */
    public Universe updateFor(Moment moment, boolean calculatePhase) {

        this.moment = moment;

        // calculate RA + Decl
        solarSystem.update(moment.getUTC());

        // calculate azimuth + altitude
        solarSystem.updateAzimuthAltitude(moment);

        if (calculatePhase)
            solarSystem.updatePhase(moment);

        for (Star star : stars.values())
            star.updateAzimuthAltitude(moment);

        for (Satellite satellite : satellites.values())
            satellite.update(moment);

        for (Constellation constellation : constellations.values())
            constellation.updateAzimuthAltitude(moment);

        for (Special special : specials)
            special.updateAzimuthAltitude(moment);

        return this;
    }

    Star findStarByName(String name) {
        if (stars.containsKey(name)) {
            return stars.get(name);
        }
        return null;
    }

    public Topocentric getZenit() {
        return new Topocentric(0, 90);
    }

    Constellation findConstellationByName(String name) {
        if (constellations.containsKey(name)) {
            return constellations.get(name);
        }
        return null;
    }

    public Satellite findSatelliteByName(String name) {
        if (satellites.containsKey(name)) {
            return satellites.get(name);
        }
        return null;
    }

    public IElement getElementByName(String name) {

        // Solar system
        for (AbstractElement celestial : solarSystem.elements) {
            if (celestial.getName().equals(name))
                return celestial;
        }

        // Satellite?
        if (satellites.containsKey(name))
            return satellites.get(name);

        // Constellation?
        if (constellations.containsKey(name))
            return constellations.get(name);

        // Stars
        return findStarByName(name);
    }

    // may return null if there is no constellation that contains this star.
    public Constellation getConstellationByStar(Star star) {
        for (Constellation constellation : constellations.values()) {
            if (constellation.getStars().contains(star))
                return constellation;
        }
        return null;
    }
}

