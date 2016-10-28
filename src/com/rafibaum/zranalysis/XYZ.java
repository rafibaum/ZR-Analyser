package com.rafibaum.zranalysis;

/**
 * Created by rafibaum on 16/10/16.
 */
public class XYZ {

    private double x, y, z;

    public XYZ(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public double getMagnitude() {
        return Math.sqrt(x*x + y*y + z*z);
    }

    @Override
    public String toString() {
        return x + ", " + y + ", " + z;
    }
}
