package com.stho.software.nyota;


import com.stho.software.nyota.universe.AbstractPlanet;
import com.stho.software.nyota.universe.AbstractSatellite;
import com.stho.software.nyota.universe.Constellation;
import com.stho.software.nyota.universe.IElement;
import com.stho.software.nyota.universe.Moon;

/**
 * Created by shoedtke on 23.09.2016.
 */
final class ActivityFactory {

    private ActivityFactory() {
        // not instantiable
    }

    static Class<?> getElementActivityClass(IElement element) {

        if (element instanceof Moon)
            return MoonAdapterActivity.class;

        else if (element instanceof AbstractPlanet)
            return PlanetActivity.class;

        else if (element instanceof AbstractSatellite)
            return SatelliteActivity.class;

        else if (element instanceof Constellation)
            return ConstellationActivity.class;

        else
            return ElementActivity.class;
    }
}

