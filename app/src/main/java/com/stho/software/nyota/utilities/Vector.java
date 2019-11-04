package com.stho.software.nyota.utilities;

/**
 * Created by shoedtke on 20.01.2017.
 */
public class Vector {
    public double x;
    public double y;
    public double z;

    public Vector() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getLength() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public Vector minus(Vector v) {
        return new Vector(this.x - v.x, this.y - v.y, this.z - v.z);
    }
}
