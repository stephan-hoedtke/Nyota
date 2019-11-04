package com.stho.software.nyota.utilities;

import java.util.ArrayList;
import java.util.TimeZone;

/**
 * Created by shoedtke on 29.09.2016.
 */
public class IconValueList extends ArrayList<IIconValue> {

    public boolean add(int imageId, String value) {
        return this.add(new IconValue(imageId, value));
    }

    public boolean add(int imageId, Degree degree) {
        return this.add(new IconValue(imageId, degree.toString()));
    }

    public boolean add(int imageId, Hour hour) {
        return this.add(new IconValue(imageId, hour.toString()));
    }

    public boolean add(int imageId, UTC utc, TimeZone timeZone) {
        return this.add(new IconValue(imageId, Formatter.toString(utc, timeZone, Formatter.TimeFormat.DATETIME_SEC)));
    }
}
