package com.stho.software.nyota.universe;

import com.stho.software.nyota.utilities.Degree;
import com.stho.software.nyota.utilities.Formatter;
import com.stho.software.nyota.utilities.IconNameValueList;
import com.stho.software.nyota.utilities.Moment;

/**
 * Created by shoedtke on 31.08.2016.
 */
public abstract class AbstractPlanet extends AbstractSolarSystemElement {

    @Override
    public boolean isPlanet() {
        return true;
    }

    public void calculatePhase(Sun sun) {
        elongation = Degree.arcCos((sun.R * sun.R + R * R - r * r) / (2 * sun.R * R));
        FV = Degree.arcCos((r * r + R * R - sun.R * sun.R) / (2 * r * R));
        phase  =  (1 + Degree.cos(FV)) / 2;
    }

    public abstract void calculateMagnitude();

    @Override
    public IconNameValueList getDetails(Moment moment) {
        IconNameValueList details = super.getDetails(moment);

        if (position.prevSetTime != null)
            details.add(com.stho.software.nyota.R.drawable.sunset, "Set", position.prevSetTime, moment.getTimeZone());

        if (position.riseTime != null)
            details.add(com.stho.software.nyota.R.drawable.sunrise, "Rise", position.riseTime, moment.getTimeZone());

        if (position.setTime != null)
            details.add(com.stho.software.nyota.R.drawable.sunset, "Set ", position.setTime, moment.getTimeZone());

        if (position.nextRiseTime != null)
            details.add(com.stho.software.nyota.R.drawable.sunrise, "Rise", position.nextRiseTime, moment.getTimeZone());

        details.add(com.stho.software.nyota.R.drawable.star, "FV", Degree.fromDegree(FV));
        details.add(com.stho.software.nyota.R.drawable.star, "Phase", Formatter.df3.format(phase));
        details.add(com.stho.software.nyota.R.drawable.star, "Phase angle", Formatter.df0.format(phaseAngle));
        details.add(com.stho.software.nyota.R.drawable.star, "Magnitude", Formatter.df2.format(magn));
        details.add(com.stho.software.nyota.R.drawable.star, "Parallax", Formatter.df3.format(parallacticAngle));
        details.add(com.stho.software.nyota.R.drawable.star, "In south", position.inSouth, moment.getTimeZone());
        details.add(com.stho.software.nyota.R.drawable.star, "Culmination angle", Degree.fromDegree(position.culmination));

        return details;
    }

    protected double getHeightFor(Moment moment) {
        AbstractPlanet planet = getPlanetFor(moment);
        return planet.getHeight();
    }

    private double getHeight() {
        return position.altitude - H0();
    }

    protected double H0() {
         return -0.583;
    }

    protected abstract AbstractPlanet getPlanetFor(Moment moment);
}
