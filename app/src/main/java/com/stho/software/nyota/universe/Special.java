package com.stho.software.nyota.universe;

import com.stho.software.nyota.universe.AbstractElement;

public class Special extends AbstractElement {

    private final String name;

    @Override
    public int getImageId() {
        return com.stho.software.nyota.R.mipmap.star;
    }

    @Override
    public String getName() {
        return name;
    }

    Special(String name, double ra, double decl) {
        this.name = name;
        this.RA = ra;
        this.Decl = decl;
    }
}
