package com.rafibaum.zranalysis;

import java.io.File;

/**
 * Created by rafibaum on 16/10/16.
 */
public class Main {

    public static void main(String args[]) {
        String id = "1355729";
        File simFile = new File("/Users/rafibaum/Development/Java/ZRAnalysis/sims/sim" + id + ".json");
        Simulation sim = new Simulation(simFile);

        float previous;
        float current;
        float diff;
        for(int i = 1; i < sim.getBlueVelocities().length; i += 5) {
            previous = sim.getRedVelocities()[i-1].getMagnitude();
            current = sim.getRedVelocities()[i].getMagnitude();
            diff = current-previous;
            System.out.println(i + "," + diff);
        }
    }

}
