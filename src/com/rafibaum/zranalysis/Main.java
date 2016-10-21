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

        loadAllSims(simDir);

        double maxScore = 0.0;
        String id = "";
        for(Simulation sim : sims) {
            if(sim.getBlueScore() > maxScore) {
                maxScore = sim.getBlueScore();
                id = sim.getSimID();
            }
            if(sim.getRedScore() > maxScore) {
                maxScore = sim.getRedScore();
                id = sim.getSimID();
            }
        }

        System.out.println(maxScore + ": " + id);
    }






    public static Simulation loadSim(String simID) {
        return loadSim(new File(simDir, "sim" + simID + ".json"));
    }

    public static Simulation loadSim(File simFile) {
        return new Simulation(simFile);
    }

    public static void loadAllSims(File dir) {
        File[] simFiles = dir.listFiles();
        sims = new ArrayList<Simulation>();
        for(int i = 0; i < simFiles.length; i++) {
            sims.add(new Simulation(simFiles[i]));
            if(i%20 == 0) {
                System.out.println(i*100/simFiles.length + "%");
            }
        }
    }

}
