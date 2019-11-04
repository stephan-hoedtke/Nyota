package com.stho.software.nyota.universe;

import com.stho.software.nyota.universe.AbstractElement;

/**
 * Created by shoedtke on 31.08.2016.
 */
public class Star extends AbstractElement {

    private final String name;
    private final String symbol;
    private final double brightness;

    @Override
    public int getImageId() {
        return com.stho.software.nyota.R.mipmap.star;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getBrightness() {
        return brightness;
    }

    Star(String name, String symbol, double ra, double decl, double brightness) {
        this.name = name;
        this.symbol = symbol;
        this.RA = ra;
        this.Decl = decl;
        this.brightness = brightness;
    }

    @Override
    public String toString() {
        return name;
    }
}
