package com.stho.software.nyota.universe;

import com.stho.software.nyota.utilities.Degree;
import com.stho.software.nyota.utilities.Hour;

abstract class AbstractUniverseInitializer {
    /**
     * Singleton
     */
    protected Universe universe;

    AbstractUniverseInitializer(Universe universe) {
        this.universe = universe;
    }

    Star newStar(String name, String symbol, String ascension, String declination, double brightness) {
        return newStar(name, symbol, Hour.fromHour(ascension), Degree.fromDegree(declination), brightness);
    }

    private Star newStar(String name, String symbol, Hour ascension, Degree declination, double brightness) {
        if (universe.stars.containsKey(name)) {
            return universe.stars.get(name);
        }
        else {
            Star star = new Star(name, symbol, ascension.toDegree(), declination.toDegree(), brightness);
            universe.stars.put(star.getName(), star);
            return star;
        }
    }

    Constellation newConstellation(String name, int imageId) {
        Constellation constellation = new Constellation(name, imageId);
        universe.constellations.put(name, constellation);
        return constellation;
    }

    @SuppressWarnings({"UnusedReturnValue", "SameParameterValue"})
    Satellite newSatellite(String name, String elements) {
        Satellite satellite = new Satellite(name, elements);
        universe.satellites.put(name, satellite);
        return satellite;
    }

    @SuppressWarnings({"UnusedReturnValue", "SameParameterValue"})
    Special newSpecialElement(String name, Hour ascension, Degree declination) {
        Special special = new Special(name, ascension.toDegree(), declination.toDegree());
        universe.specials.add(special);
        return special;
    }

    void asVIP(Star star) {
        if (!universe.vip.contains(star))
            universe.vip.add(star);
    }
}
