package com.stho.software.nyota.views;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.stho.software.nyota.data.Settings;
import com.stho.software.nyota.universe.Constellation;
import com.stho.software.nyota.utilities.Topocentric;

/**
 * Created by shoedtke on 23.09.2016.
 */
public class ConstellationView extends AbstractSkyView {

    protected Constellation constellation = null;

    // https://developer.android.com/training/gestures/scale
    public ConstellationView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setConstellation(Constellation constellation) {
        if (this.constellation != constellation) {
            this.constellation = constellation;
        }
        center.azimuth = constellation.getPosition().azimuth;
        center.altitude = constellation.getPosition().altitude;
    }

    @Override
    protected Topocentric getReference() {
        return constellation.getPosition();
    }

    @Override
    protected void onLoadSettings(Settings settings) {
        super.onLoadSettings(settings);
        super.displayNames = false;
        super.displaySymbols = settings.displaySymbols;
        super.displayMagnitude = settings.displayMagnitude;
    }

    @Override
    protected void onDrawElements(Canvas canvas, double zoom) {
        drawConstellation(canvas, zoom, constellation);
    }
}

