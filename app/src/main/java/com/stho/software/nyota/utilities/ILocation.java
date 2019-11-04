package com.stho.software.nyota.utilities;

public interface ILocation {
    /*
       latitude in decimal degree
     */
    double getLatitude();

    /*
       longitude in decimal degree
     */
    double getLongitude();

    /*
       altitude in km
     */
    double getAltitude();
}
