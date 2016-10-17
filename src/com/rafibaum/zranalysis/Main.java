package com.rafibaum.zranalysis;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by rafibaum on 16/10/16.
 */
public class Main {

    private static ArrayList<Simulation> sims;
    private static File simDir;

    public static void main(String args[]) {
        if(args.length == 0) {
            System.err.println("ERROR: First argument needs to be path to sim files");
            return;
        }
        simDir = new File(args[0]);



        Simulation sim = loadSim("1309730");
        System.out.println(sim.getBlueVelocity(53));
        //blue 40 to 64
        //red 50 to 74
        for(int i = 0; i < 25; i++) {
            System.out.println(i + "," + sim.getBlueVelocities()[i*5+40*5].getMagnitude() + "," + sim.getRedVelocities()[i*5+50*5].getMagnitude());
        }

    }






    public static Simulation loadSim(String simID) {
        return loadSim(new File(simDir, "sim" + simID + ".json"));
    }

    public static Simulation loadSim(File simFile) {
        return new Simulation(simFile);
    }

    public static void loadAllSims(File dir) {
        File[] simFiles = dir.listFiles();
        ArrayList<Simulation> sims = new ArrayList<Simulation>();
        for(int i = 0; i < simFiles.length; i++) {
            sims.add(new Simulation(simFiles[i]));
            if(i%20 == 0) {
                System.out.println(i*100/1723 + "%");
            }
        }
    }

}
