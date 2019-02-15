package com.example.team31_personalbest;

import android.app.Activity;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class FitnessServiceFactory {

    private static final String TAG = "[FitnessServiceFactory]";

    private static Map<String, BluePrint> blueprints = new HashMap<>();

    public static void put(String key, BluePrint bluePrint) {
        blueprints.put(key, bluePrint);
    }

    public static FitnessService create(String key, IStepActivity stepActivity) {
        Log.i(TAG, String.format("creating FitnessService with key %s", key));
        return blueprints.get(key).create(stepActivity);
    }

    public interface BluePrint {
        FitnessService create(IStepActivity stepActivity);
    }
}
