package com.mardarcu.pixeldash;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.SerializationException;

import java.util.LinkedHashMap;

/**
 * Created by Owner on 10/2/2015.
 */
public class SaveStateManager {

    public int maxCorruptSaves = 1;
    boolean errorOccured = false;
    SaveState state;

    public void loadStateIntoMemory(SaveState state){
        this.state = state;
    }

    public  void writeSaveFile(SaveState state, boolean PRINT_CONTENTS){
        try {
            FileHandle file = Gdx.files.local("saves/save.pds");
            Json json = new Json();
            json.setElementType(SaveState.class, "preferences", LinkedHashMap.class);
            json.setElementType(SaveState.class, "progressData", LinkedHashMap.class);
            //String base64encodedString = Base64.getEncoder().encodeToString(json.toJson(state).getBytes("utf-8"));
            String base64encodedString = Base64Coder.encodeString(json.toJson(state), true);
            file.writeString(base64encodedString, false);
            if(PRINT_CONTENTS)
                System.out.println("Wrote save file with JSON and LIBGDX Base64 encryption:\n" + json.prettyPrint(state)+"\n");
            else
                System.out.println("Wrote save file");
        } catch (GdxRuntimeException e){
            e.printStackTrace();
        }
    }

    public SaveState readSaveFile(String filePath, boolean PRINT_CONTENTS){

        if(!Gdx.files.isLocalStorageAvailable()){
            System.err.println("Local Storage is unavailable.");
            return newSaveFile(false);
        }
        SaveState save = null;
        String contents = "";

        try{
            Json json = new Json();
            json.setElementType(SaveState.class, "preferences", LinkedHashMap.class);
            json.setElementType(SaveState.class, "progressData", LinkedHashMap.class);
            FileHandle file = Gdx.files.local("saves/" + filePath);
            contents = file.readString();

           // byte[] base64decodedBytes = Base64.getDecoder().decode(contents);
            String base64decodedString= Base64Coder.decodeString(contents, true);
            //String base64decodedString = new String(base64decodedBytes, "utf-8");
            save = json.fromJson(SaveState.class, base64decodedString);
            if(PRINT_CONTENTS)
                System.out.println("Read save file from JSON with Base64 decryption:\n" + json.prettyPrint(save)+"\n");
            else
                System.out.println("Read save file.");
        } catch (SerializationException e){
            e.printStackTrace();
            System.err.println("Save file is corrupted");
            //return newSaveFile(contents, SAVE_NEW_IF_NULL);
        } catch (IllegalArgumentException a){
            System.err.println("Could not decrypt save data with Base64");
            a.printStackTrace();
            //return newSaveFile(contents, SAVE_NEW_IF_NULL);
        } catch (GdxRuntimeException fnf){
            System.err.println("Save file could not be found in " + Gdx.files.getLocalStoragePath() + "saves\\" + filePath);
            //fnf.printStackTrace();
            //return newSaveFile(SAVE_NEW_IF_NULL);
        }

        return save;
    }

    public SaveState newSaveFile(boolean SAVE_NEW){
        SaveState state = new SaveState();
        if(SAVE_NEW)
            writeSaveFile(state, true);
        return state;
    }

    public SaveState newSaveFile(String contents, boolean SAVE_NEW){
        FileHandle file = Gdx.files.local("saves\\corrupts\\save.pds");
        file.writeString(contents, false);

        return newSaveFile(SAVE_NEW);
    }

    public boolean readBooleanPreference(SaveState state, String id){
        if(!state.preferences.containsKey(id))
            return false;
        boolean b = (Boolean) state.preferences.get(id);

        return b;
    }

    public int readLevelDataInteger(int set, int level, String id){
        if(!((Boolean) state.progressData.containsKey(String.valueOf(set)))){
            System.err.println("[readLevelDataInteger] Attempted to get Level data of invalid set: " + set);
            return 0;
        }

        LinkedHashMap setData =  (LinkedHashMap) state.progressData.get(String.valueOf(set));

        if(!((Boolean) setData.containsKey(String.valueOf(level)))){
            System.err.println("[readLevelDataInteger] Attempted to get Level data of invalid level: " + level);
            return 0;
        }

        LinkedHashMap levelData  = (LinkedHashMap) setData.get(String.valueOf(level));

        if(!((Boolean) levelData.containsKey(String.valueOf(id)))){
            System.err.println("[readLevelDataInteger] Attempted to get data from level data with invalid ID: " + id);

            return 0;
        }

        return (Integer) levelData.get(id);

        //return 0;
    }

    public int readLevelDataInteger(int set, int level, String id, int NEW_VALUE_IF_NULL, boolean SAVE_FILE){
        if(!((Boolean) state.progressData.containsKey(String.valueOf(set)))){
            System.err.println("[readLevelDataInteger] Attempted to get Level data of invalid set: " + set);
            return 0;
        }

        LinkedHashMap setData =  (LinkedHashMap) state.progressData.get(String.valueOf(set));

        if(!((Boolean) setData.containsKey(String.valueOf(level)))){
            System.err.println("[readLevelDataInteger] Attempted to get Level data of invalid level: " + level);
            return 0;
        }

        LinkedHashMap levelData  = (LinkedHashMap) setData.get(String.valueOf(level));

        if(!((Boolean) levelData.containsKey(String.valueOf(id)))){
            System.err.println("[readLevelDataInteger] Attempted to get data from level data with invalid ID: " + id);
            levelData.put(id, NEW_VALUE_IF_NULL);
            System.err.println("[readLevelDataInteger] created new ID \"" + id + "\" with value (" + NEW_VALUE_IF_NULL +  ") at set " + set + " level " + level);
            if(SAVE_FILE)
                writeSaveFile(state, false);
            return NEW_VALUE_IF_NULL;
        }

        return (Integer) levelData.get(id);

        //return 0;
    }

    public void writeLevelDataInteger(int set, int level, String id, int value){
        if(!((Boolean) state.progressData.containsKey(String.valueOf(set)))){
            System.err.println("[writeLevelDataInteger] Attempted to get Level data of invalid set: " + set);
            return;
        }

        LinkedHashMap setData =  (LinkedHashMap) state.progressData.get(String.valueOf(set));

        if(!((Boolean) setData.containsKey(String.valueOf(level)))){
            System.err.println("[writeDataInteger] Attempted to get Level data of invalid level: " + level);
            return;
        }

        LinkedHashMap levelData  = (LinkedHashMap) setData.get(String.valueOf(level));

        if(!((Boolean) levelData.containsKey(String.valueOf(id)))){
            System.err.println("[writeDataInteger] Attempted to get data from level data with invalid ID: " + id);

            return;
        }

        levelData.put(id, value);

        //return 0;
    }

    public boolean[] getPassedLevelData(int setName){
        //System.out.println((Boolean) state.progressData.containsKey(String.valueOf(setName)));

        if(!((Boolean) state.progressData.containsKey(String.valueOf(setName)))){

            System.err.println("[getPassedLevelData] Attempted to get Level data of invalid set: " + setName);

            return null;
        }
        boolean[] array = new boolean[30];
        // get all levels from the 1 set
        LinkedHashMap setData =  (LinkedHashMap) state.progressData.get(String.valueOf(setName));
        //get the level #s
        /*
        Iterator ir = setData.keySet().iterator();
        ArrayList<Boolean> levelData = new ArrayList<Boolean>();
        while(ir.hasNext()){
            String i = (String) ir.next();
            //int i  = (Integer) ir.next();
            LinkedHashMap level  = (LinkedHashMap) setData.get(i);
            boolean b = (Boolean) level.get("passed");
            //System.out.println(i + " " + b);
            levelData.add(b);
        }
        */
        for(int i = 0; i < array.length; i++){
            LinkedHashMap level  = (LinkedHashMap) setData.get(String.valueOf(i + 1));
            boolean b = (Boolean) level.get("passed");
            array[i] = b;
        }
        // return levelData;
        return array;
    }

    public void passLevel(int set, int level){
        if(!state.progressData.containsKey(String.valueOf(set))){
            System.err.println("[PASS LEVEL] Attempted to get set: " + set);
            return;
        }
        LinkedHashMap setData =  (LinkedHashMap) state.progressData.get(String.valueOf(set));
        if(!setData.containsKey(String.valueOf(level))){
            System.err.println("[PASS LEVEL] Attempted to get level: " + level + " from set: " + set);
            return;
        }

        LinkedHashMap levelData = (LinkedHashMap) setData.get(String.valueOf(level));
        levelData.put("passed", true);
        //System.out.println("DOWN ON HOE level" +  "2" + (Boolean)levelData.get("passed"));
    }
}
