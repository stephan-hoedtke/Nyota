package com.stho.nyota.sky.utilities

interface ILocation {
    /*
       latitude in decimal degree
     */
    val latitude: Double

    /*
       longitude in decimal degree
     */
    val longitude: Double

    /*
       altitude in km
     */
    val altitude: Double
}