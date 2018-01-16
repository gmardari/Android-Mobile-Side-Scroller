package com.mardarcu.pixeldash;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

import sun.awt.image.ImageWatched;

/**
 * Created by Owner on 10/1/2015.
 */
public class SaveState {

    String targetVersion;
    LinkedHashMap preferences;
    LinkedHashMap<String, LinkedHashMap> progressData;
    int money;

    public SaveState(){

        targetVersion = PixelDash.APP_VERSION;
        preferences = new LinkedHashMap();
        preferences.put("debugScene2d", true);
        preferences.put("debugBox2d", true);
        preferences.put("retryLevel", false);

        progressData = new LinkedHashMap();
        LinkedHashMap<String, LinkedHashMap> normalSet = new LinkedHashMap();

        for(int i = 0; i < 30; i ++){
            LinkedHashMap levelData = new LinkedHashMap();
            levelData.put("passed", (i == 0) ? true : false);
            levelData.put("attempts", 0);
            levelData.put("wins", 0);
            normalSet.put(Integer.toString(i + 1), levelData);
        }

        progressData.put(Integer.toString(PlayScreen.NORMAL_SET), normalSet);

    }


    /*
    public boolean[] getPassedLevelData(int setName){
        System.out.println((Boolean) progressData.containsKey(String.valueOf(setName)));

        if(!((Boolean) progressData.containsKey(String.valueOf(setName)))){

            System.err.println("[getPassedLevelData] Attempted to get Level data of invalid set: " + setName);

            return null;
        }
        boolean[] array = new boolean[30];
        // get all levels from the 1 set
        LinkedHashMap setData =  (LinkedHashMap) progressData.get(String.valueOf(setName));

        for(int i = 0; i < array.length; i++){
            LinkedHashMap level  = (LinkedHashMap) setData.get(String.valueOf(i + 1));
            boolean b = (Boolean) level.get("passed");
            array[i] = b;
        }
       // return levelData;
        return array;
    }
    */

    /*
    public void passLevel(int set, int level){
        if(!progressData.containsKey(String.valueOf(set))){
            System.err.println("[PASS LEVEL] Attempted to get set: " + set);
            return;
        }
        LinkedHashMap setData =  (LinkedHashMap) progressData.get(String.valueOf(set));
        if(!setData.containsKey(String.valueOf(level))){
            System.err.println("[PASS LEVEL] Attempted to get level: " + level + " from set: " + set);
            return;
        }

        LinkedHashMap levelData = (LinkedHashMap) setData.get(String.valueOf(level));
        levelData.put("passed", true);
        //System.out.println("DOWN ON HOE level" +  "2" + (Boolean)levelData.get("passed"));
    }
    */
}
