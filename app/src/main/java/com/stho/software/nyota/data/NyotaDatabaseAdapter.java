package com.stho.software.nyota.data;

import com.stho.software.nyota.universe.Constellation;
import com.stho.software.nyota.universe.Star;

import java.util.ArrayList;

public class NyotaDatabaseAdapter {

    NyotaDatabaseHandler handler;

    ArrayList<Star> getStars() {
        ArrayList<Star> stars = new ArrayList<>();
        return stars;
    }

    ArrayList<Constellation> getConstellations() {
        ArrayList<Constellation> constellations = new ArrayList<>();
        return constellations;
    }


    void writeStar(String name, double rightAscension, double declination, double brightness) {

    }
}
