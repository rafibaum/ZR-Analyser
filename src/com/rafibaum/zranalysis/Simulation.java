package com.rafibaum.zranalysis;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by rafibaum on 16/10/16.
 */
public class Simulation {

    private double blueScore, redScore;

    private XYZ[] bluePositions, redPositions;
    private XYZ[] blueVelocities, redVelocities;
    private int[] blueDropTimes, redDropTimes;

    private XYZ blueZone, redZone;

    private Debug[] debugs;

    private HashMap<Integer, XYZ[]> spartPositions;



    private String simID;

    public Simulation(File file) {
        simID = file.getName();
        simID = simID.substring(3, simID.length()-5);

        try {
            //Reading file into memory
            BufferedReader reader = new BufferedReader(new FileReader(file));
            reader.readLine();
            String jsonString = reader.readLine();

            //Creating higher JSON objects
            JsonParser fileParser = new JsonParser();
            JsonObject jsonBody = fileParser.parse(jsonString).getAsJsonObject();
            JsonArray satData = jsonBody.get("satData").getAsJsonArray();

            //Parsing blue positions/velocities
            JsonObject blueData = satData.get(0).getAsJsonObject();
            bluePositions = getPositions(blueData);
            blueVelocities = getVelocities(blueData);

            //Parsing red positions/velocities
            JsonObject redData = satData.get(1).getAsJsonObject();
            redPositions = getPositions(redData);
            redVelocities = getVelocities(redData);

            //Parsing debugs
            debugs = getDebugs(jsonBody.get("tTxt").getAsJsonArray(), blueData, redData);

            //Blue drop times
            JsonArray blueDropData = blueData.get("dU").getAsJsonArray().get(2).getAsJsonArray();
            int blueDrops = 0;
            blueDropTimes = new int[3];
            for(int i = 1; i < blueDropData.size(); i++) {
                if(blueDropData.get(i).isJsonNull()) continue;
                if(blueDropData.get(i).getAsInt() == 1) {
                    if(i == 1) {
                        blueDropTimes[blueDrops] = 0;
                    } else {
                        blueDropTimes[blueDrops] = i;
                    }
                    blueDrops++;
                }
            }

            //Red drop times
            JsonArray redDropData = redData.get("dU").getAsJsonArray().get(2).getAsJsonArray();
            int redDrops = 0;
            redDropTimes = new int[3];
            for(int i = 1; i < redDropData.size(); i++) {
                if(redDropData.get(i).isJsonNull()) continue;
                if(redDropData.get(i).getAsInt() == 1) {
                    if(i == 1) {
                        redDropTimes[redDrops] = 0;
                    } else {
                        redDropTimes[redDrops] = i;
                    }
                    redDrops++;
                }
            }

            //Blue score
            JsonArray blueScores = blueData.get("dF").getAsJsonArray().get(0).getAsJsonArray();
            blueScore = blueScores.get(blueScores.size()-1).getAsDouble();

            //Red score
            JsonArray redScores = redData.get("dF").getAsJsonArray().get(0).getAsJsonArray();
            redScore = redScores.get(redScores.size()-2).getAsDouble();

            //Sparts
            spartPositions = new HashMap<>();
            spartPositions.put(0, getSpartPositions(blueData, 0));
            spartPositions.put(1, getSpartPositions(redData, 0));
            spartPositions.put(2, getSpartPositions(blueData, 1));
            spartPositions.put(3, getSpartPositions(redData, 1));
            spartPositions.put(4, getSpartPositions(blueData, 2));
            spartPositions.put(5, getSpartPositions(redData, 2));

            //Zones
            JsonArray scoresMatrix = blueData.get("dF").getAsJsonArray();
            blueZone = new XYZ(
                    scoresMatrix.get(0).getAsJsonArray().get(0).getAsDouble(),
                    scoresMatrix.get(1).getAsJsonArray().get(0).getAsDouble(),
                    scoresMatrix.get(2).getAsJsonArray().get(0).getAsDouble());
            redZone = new XYZ(
                    scoresMatrix.get(3).getAsJsonArray().get(0).getAsDouble(),
                    scoresMatrix.get(4).getAsJsonArray().get(0).getAsDouble(),
                    scoresMatrix.get(5).getAsJsonArray().get(0).getAsDouble());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public XYZ[] getBluePositions() {
        return bluePositions;
    }

    public XYZ[] getRedPositions() {
        return redPositions;
    }

    public Debug[] getDebugs() {
        return debugs;
    }

    public XYZ getBluePosition(int time) {
        return bluePositions[time*5];
    }

    public XYZ getRedPosition(int time) {
        return redPositions[time*5];
    }

    public int[] getBlueDropTimes() {
        return blueDropTimes;
    }

    public int[] getRedDropTimes() {
        return redDropTimes;
    }

    public String getSimID() {
        return simID;
    }

    public HashMap<Integer, XYZ[]> getSpartPositions() {
        return spartPositions;
    }

    public XYZ[] getSpartPositions(int spart) {
        return spartPositions.get(spart);
    }

    public XYZ getSpartPosition(int spart, int time) {
        return spartPositions.get(spart)[time];
    }

    public double getBlueScore() {
        return blueScore;
    }

    public double getRedScore() {
        return redScore;
    }

    public XYZ getBlueZone() {
        return blueZone;
    }

    public XYZ getRedZone() {
        return redZone;
    }

    public XYZ[] getBlueVelocities() {
        return blueVelocities;
    }

    public XYZ[] getRedVelocities() {
        return redVelocities;
    }

    public XYZ getBlueVelocity(int time) {
        return blueVelocities[time*5];
    }

    public XYZ getRedVelocity(int time) {
        return redVelocities[time*5];
    }






    private XYZ[] getPositions(JsonObject sphereData) {
        return getXYZData(sphereData, "st", 0);
    }

    private XYZ[] getVelocities(JsonObject sphereData) {
        return getXYZData(sphereData, "st", 1);
    }

    private XYZ[] getSpartPositions(JsonObject sphereData, int index) {
        JsonArray spartPositionArray = sphereData.get("dS").getAsJsonArray();
        JsonArray spartXs = spartPositionArray.get(index*3).getAsJsonArray();
        JsonArray spartYs = spartPositionArray.get(index*3 + 1).getAsJsonArray();
        JsonArray spartZs = spartPositionArray.get(index*3 + 2).getAsJsonArray();

        XYZ[] spartPositions = new XYZ[spartXs.size()];
        for(int i = 1; i < spartXs.size()-1; i++) {
            spartPositions[i] = new XYZ(spartXs.get(i).getAsInt()/10000.0, spartYs.get(i).getAsInt()/10000.0, spartZs.get(i).getAsInt()/10000.0);
        }

        return spartPositions;
    }

    private XYZ[] getXYZData(JsonObject sphereData, String array, int offset) {
        JsonArray positionArray = sphereData.get(array).getAsJsonArray();
        JsonArray Xs = positionArray.get(offset*3).getAsJsonArray();
        JsonArray Ys = positionArray.get(offset*3 + 1).getAsJsonArray();
        JsonArray Zs = positionArray.get(offset*3 + 2).getAsJsonArray();

        XYZ[] positions = new XYZ[Xs.size()];
        for(int i = 0; i < Xs.size(); i++) {
            positions[i] = new XYZ(Xs.get(i).getAsDouble(), Ys.get(i).getAsDouble(), Zs.get(i).getAsDouble());
        }

        return positions;
    }

    private Debug[] getDebugs(JsonArray times, JsonObject... sphereData) {
        ArrayList<Debug> debugs = new ArrayList<>();
        debugs.ensureCapacity(times.size());

        //Iterates over sphere's individual data
        for(int t = 0; t < sphereData.length; t++) {
            JsonObject data = sphereData[t];
            JsonArray sphereDebugs = data.get("txt").getAsJsonArray();
            //Iterates over sphere's debugs
            for(int i = 0; i < times.size(); i++) {
                String debugLine = sphereDebugs.get(i).getAsString();
                //Iterates over debugs on same line
                if(debugLine == null || debugLine.equals("")) continue;
                for(String debug : debugLine.substring(2, debugLine.length()-2).split("\n<!")) {
                    if(debug == null || debug.equals("")) continue;

                    Type debugType;
                    if(debug.startsWith("GT")) {
                        debugType = Type.SYSTEM;
                    } else {
                        debugType = Type.USER;
                    }

                    int time = times.get(i).getAsInt();

                    Sphere team;
                    if(t == 0) {
                        team = Sphere.BLUE;
                    } else {
                        team = Sphere.RED;
                    }

                    String msg = debug.split(">: ", 2)[1];
                    debugs.add(new Debug(time, debugType, team, msg));
                }
            }
        }

        debugs.sort((Debug o1, Debug o2) -> {
                if(o1.getTime() > o2.getTime()) {
                    return 1;
                } else if(o1.getTime() == o2.getTime()) {
                    return 0;
                } else {
                    return -1;
                }
        });

        return debugs.toArray(new Debug[1]);
    }

}
