package com.stho.software.nyota.utilities;

import java.util.ArrayList;
import java.util.TimeZone;

/**
 * Created by shoedtke on 29.09.2016.
 */
public class IconNameValueList extends ArrayList<IIconNameValue> {

    public boolean add(int imageId, String name, String value) {
        return this.add(new IconNameValue(imageId, name, value));
    }

    public boolean add(int imageId, String name, Degree degree) {
        return this.add(new IconNameValue(imageId, name, degree.toString()));
    }

    public boolean add(int imageId, String name, Hour hour) {
        return this.add(new IconNameValue(imageId, name, hour.toString()));
    }

    public boolean add(int imageId, String name, UTC utc, TimeZone timeZone) {
        return this.add(new IconNameValue(imageId, name, Formatter.toString(utc, timeZone, Formatter.TimeFormat.DATETIME)));
    }
}
