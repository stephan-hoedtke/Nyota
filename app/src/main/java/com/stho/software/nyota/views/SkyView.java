package com.stho.software.nyota.views;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.stho.software.nyota.universe.AbstractSolarSystemElement;
import com.stho.software.nyota.universe.Constellation;
import com.stho.software.nyota.universe.IElement;
import com.stho.software.nyota.data.Settings;
import com.stho.software.nyota.universe.Special;
import com.stho.software.nyota.universe.Star;
import com.stho.software.nyota.utilities.Topocentric;
import com.stho.software.nyota.universe.Universe;

/**
 * Created by shoedtke on 07.09.2016.
 */
public class SkyView extends AbstractSkyView {

    private Universe universe;
    private IElement reference = null;

    public SkyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        universe = Universe.getInstance();
    }

    public void setReference(IElement reference) {
        this.reference = reference;
        center.azimuth = reference.getPosition().azimuth;
        center.altitude = reference.getPosition().altitude;
        updateUI();
    }

    @Override
    protected Topocentric getReference() {
        return reference.getPosition();
    }

    @Override
    protected void onLoadSettings(Settings settings) {
        super.onLoadSettings(settings);
        super.displayNames = settings.displayNames;
        super.displaySymbols = false;
        super.displayMagnitude = settings.displayMagnitude;
    }

    public void setCenter(double centerAzimuth, double centerAltitude) {
        if (centerAzimuth != center.azimuth || centerAltitude != center.altitude) {
            center.azimuth = centerAzimuth;
            center.altitude = centerAltitude;
            updateUI();
        }
    }

    @Override
    protected void onDrawElements(Canvas canvas, double zoom) {
        for (Special special : universe.specials) {
            super.drawName(canvas, zoom, special);
        }
        for (Constellation constellation: universe.constellations.values()) {
            super.drawConstellation(canvas, zoom, constellation);
            if (super.displayNames) {
                super.drawName(canvas, zoom, constellation);
            }
        }
        for (Star star : universe.vip) {
            super.drawStar(canvas, zoom, star);
            if (super.displayNames) {
                super.drawName(canvas, zoom, star);
            }
        }
        for (AbstractSolarSystemElement element : universe.solarSystem.elements) {
            if (element.isPlanet()) {
                super.drawPlanet(canvas, zoom, element);
                if (super.displayNames) {
                    super.drawName(canvas, zoom, element);
                }
            }
        }
        super.drawMoon(canvas, zoom, universe.solarSystem.moon);
        super.drawSun(canvas, zoom, universe.solarSystem.sun);
        super.drawName(canvas, zoom, universe.getZenit(), "Z");
    }
}

