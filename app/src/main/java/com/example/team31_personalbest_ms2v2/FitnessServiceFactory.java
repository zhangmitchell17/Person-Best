package com.example.team31_personalbest_ms2v2;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * This file is for the FitnessServiceFactory class
 */
public class FitnessServiceFactory {

    private static final String TAG = "[FitnessServiceFactory]";

    private static Map<String, BluePrint> blueprints = new HashMap<>();

    public static void put(String key, BluePrint bluePrint) {
        blueprints.put(key, bluePrint);
        if (blueprints.containsKey(key)) {
            Log.i(TAG,String.format("FitnessService now contains the key %s", key));
        }
        else {
            Log.e(TAG,String.format("FitnessService unable to add the key %s", key));
        }
    }

    public static FitnessService create(String key, IStepActivity stepActivity) {
        Log.i(TAG, String.format("creating FitnessService with key %s", key));
        if (!blueprints.containsKey(key)) {
            Log.e(TAG,String.format("FitnessService has does not have the key %s", key));
        }
        return blueprints.get(key).create(stepActivity);
    }

    public interface BluePrint {
        FitnessService create(IStepActivity stepActivity);
    }
}
