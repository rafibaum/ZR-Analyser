package com.rafibaum.zranalysis;

/**
 * Created by rafibaum on 16/10/16.
 */
public class Debug {
    private String message;
    private int time;
    private Type debugType;
    private Sphere team;

    public Debug(int time, Type debugType, Sphere team, String message) {
        this.time = time;
        this.debugType = debugType;
        this.message = message;
        this.team = team;
    }

    public String getMessage() {
        return message;
    }

    public int getTime() {
        return time;
    }

    public Type getDebugType() {
        return debugType;
    }

    public Sphere getTeam() {
        return team;
    }
}

enum Type {
    USER,
    SYSTEM
}