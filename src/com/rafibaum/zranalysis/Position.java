package com.rafibaum.zranalysis;

/**
 * Created by rafibaum on 16/10/16.
 */
public class Position {

    private float x, y, z;

    public Position(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public float getMagnitude() {
        return (float) Math.sqrt(x*x + y*y + z*z);
    }
}
