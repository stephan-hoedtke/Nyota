package com.stho.software.nyota.universe;

import com.stho.software.nyota.utilities.Average;
import com.stho.software.nyota.utilities.IconNameValueList;
import com.stho.software.nyota.utilities.Moment;
import com.stho.software.nyota.R;

import java.util.ArrayList;

/**
 * Created by shoedtke on 08.09.2016.
 */
public class Constellation extends AbstractElement {

    private final String name;
    private final int imageId;
    private final ArrayList<Star> stars = new ArrayList<>();
    private final ArrayList<Star[]> lines = new ArrayList<Star[]>();

    public String getName() { return name; }

    @Override
    public int getImageId() {
        return imageId;
    }

    public ArrayList<Star> getStars() { return stars; }
    public ArrayList<Star[]> getLines() { return lines; };

    Constellation(String name, int imageId) {
        this.name = name;
        this.imageId = imageId;
    }

    Constellation register(Star... stars) {
        for (Star star: stars) {
            if (!this.stars.contains(star))
                this.stars.add(star);
        }
        this.lines.add(stars);
        calculateAveragePosition();
        return this;
    }

    @Override
    public String toString() {
        return name;
    }

    private void calculateAveragePosition() {
        int length = stars.size();
        double[] decl = new double[length];
        double[] ra = new double[length];
        for (int i=0; i < length; i++) {
            Star star = stars.get(i);
            decl[i] = star.Decl;
            ra[i] = star.RA;
        }
        Decl = Average.getCircularAverage(decl, length);
        RA = Average.getCircularAverage(ra, length);
    }

    @Override
    public IconNameValueList getDetails(Moment moment) {
        IconNameValueList list = super.getDetails(moment);
        for (Star star : getStars()) {
            list.add(R.drawable.star, star.getName(), star.getPosition().toString());
        }
        return list;
    }
}

