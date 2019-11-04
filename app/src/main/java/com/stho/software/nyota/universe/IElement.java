package com.stho.software.nyota.universe;

import com.stho.software.nyota.utilities.IconNameValueList;
import com.stho.software.nyota.utilities.IconValueList;
import com.stho.software.nyota.utilities.Moment;
import com.stho.software.nyota.utilities.Topocentric;

/**
 * Common properties of
 */
public interface IElement {
    String getName();
    int getImageId();
    int getLargeImageId();
    IconValueList getBasics(Moment moment);
    IconNameValueList getDetails(Moment moment);
    Topocentric getPosition(); // Azimuth + Altitude for a local observer
    int getVisibility(); // Returns the visibilityIcon
}

