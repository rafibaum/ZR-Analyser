package com.rafibaum.zranalysis;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by rafibaum on 16/10/16.
 */
public class Simulation {

    private Position[] bluePositions, blueVelocities;
    private Position[] redPositions, redVelocities;
    private Debug[] debugs;
    private int[] blueDropTimes;
    private int[] redDropTimes;

    public Simulation(File file) {
        try {
            //Reading file into memory
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String firstLine = reader.readLine();
            String jsonString;
            if(firstLine.startsWith("{\"baseSeeds")) {
                jsonString = firstLine;
            } else {
                jsonString = reader.readLine();
            }

            //Creating higher JSON objects
            JsonParser fileParser = new JsonParser();
            JsonObject jsonBody = fileParser.parse(jsonString).getAsJsonObject();
            JsonArray satData = jsonBody.get("satData").getAsJsonArray();

            //Parsing blue positions
            JsonObject blueData = satData.get(0).getAsJsonObject();
            bluePositions = getPositions(blueData);
            blueVelocities = getVelocities(blueData);

            //Parsing red positions
            JsonObject redData = satData.get(1).getAsJsonObject();
            redPositions = getPositions(redData);
            redVelocities = getVelocities(redData);

            //Parsing debugs
            debugs = getDebugs(jsonBody.get("tTxt").getAsJsonArray(), blueData, redData);

            //Blue drop times
            blueDropTimes = new int[3];
            redDropTimes = new int[3];
            int blueDrops = 0;
            int redDrops = 0;
            for(Debug d : debugs) {
                if(d.getDebugType() != Type.SYSTEM) continue;
                if(d.getMessage().equals("SPS has been placed")) {
                    if(d.getTeam() == Sphere.BLUE) {
                        blueDropTimes[blueDrops] = d.getTime();
                        blueDrops++;
                    } else {
                        redDropTimes[redDrops] = d.getTime();
                        redDrops++;
                    }
                }
            }
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Position[] getBluePositions() {
        return bluePositions;
    }

    public Position[] getRedPositions() {
        return redPositions;
    }

    public Debug[] getDebugs() {
        return debugs;
    }

    public Position getBluePosition(int time) {
        return bluePositions[time*5];
    }

    public Position getRedPosition(int time) {
        return redPositions[time*5];
    }

    public int[] getBlueDropTimes() {
        return blueDropTimes;
    }

    public int[] getRedDropTimes() {
        return redDropTimes;
    }

    public Position[] getBlueVelocities() {
        return blueVelocities;
    }

    public Position[] getRedVelocities() {
        return redVelocities;
    }




    private static Position[] getPositions(JsonObject sphereData) {
        return getXYZs(sphereData, 0);
    }

    private static Position[] getVelocities(JsonObject sphereData) {
        return getXYZs(sphereData, 3);
    }

    private static Position[] getXYZs(JsonObject sphereData, int offset) {
        JsonArray spherePositionArray = sphereData.get("st").getAsJsonArray();
        JsonArray sphereXs = spherePositionArray.get(offset).getAsJsonArray();
        JsonArray sphereYs = spherePositionArray.get(1+offset).getAsJsonArray();
        JsonArray sphereZs = spherePositionArray.get(2+offset).getAsJsonArray();

        //Storing sphere positions
        Position[] spherePositions = new Position[sphereXs.size()];
        for(int i = 0; i < sphereXs.size(); i++) {
            spherePositions[i] = new Position(sphereXs.get(i).getAsFloat(), sphereYs.get(i).getAsFloat(), sphereZs.get(i).getAsFloat());
        }

        return spherePositions;
    }

    private static Debug[] getDebugs(JsonArray times, JsonObject... sphereData) {
        ArrayList<Debug> debugs = new ArrayList<Debug>();
        debugs.ensureCapacity(times.size());

        //Iterates over sphere's individual data
        for(int t = 0; t < sphereData.length; t++) {
            JsonObject data = sphereData[t];
            JsonArray sphereDebugs = data.get("txt").getAsJsonArray();
            //Iterates over sphere's debugs
            for(int i = 0; i < times.size(); i++) {
                String debugLine = sphereDebugs.get(i).getAsString();
                //Iterates over debugs on same line
                for(String debug : debugLine.split("\n")) {
                    if(debug == null || debug.equals("")) continue;

                    Type debugType;
                    if(debug.startsWith("<!GT")) {
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
                    String msg;
                    if(debug.startsWith(": ")) {
                        msg = debug.substring(2);
                    } else {
                        msg = debug.split(">: ", 2)[1];
                    }
                    debugs.add(new Debug(time, debugType, team, msg));
                }
            }
        }

        debugs.sort(new Comparator<Debug>() {
            @Override
            public int compare(Debug o1, Debug o2) {
                if(o1.getTime() > o2.getTime()) {
                    return 1;
                } else if(o1.getTime() == o2.getTime()) {
                    return 0;
                } else {
                    return -1;
                }
            }
        });

        return debugs.toArray(new Debug[1]);
    }

}
