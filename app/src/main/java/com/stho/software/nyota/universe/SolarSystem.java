package com.stho.software.nyota.universe;

/**
 * Created by shoedtke on 31.08.2016.
 */
public class SolarSystem extends AbstractSolarSystem implements ISolarSystem {

    public SolarSystem() {
        sun = new Sun();
        moon = new Moon();
        mercury = new Mercury();
        venus = new Venus();
        mars = new Mars();
        jupiter = new Jupiter();
        saturn = new Saturn();
        uranus = new Uranus();
        neptune = new Neptune();

        elements = new AbstractSolarSystemElement[]{
            sun,
            moon,
            mercury,
            venus,
            mars,
            jupiter,
            saturn,
            uranus,
            neptune
        };
    }
}


