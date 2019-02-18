package com.example.team31_personalbest;

import android.content.SharedPreferences;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;

import static android.content.Context.MODE_PRIVATE;

public class WalkRunStats {

    String speed;
    String date;
    String steps;
    String totalTime;

    /**
     * Contruct a walkRunStas object
     * @param speed avg speed of user
     * @param steps steps of user in a walk run activity
     */
    public WalkRunStats(String speed, String steps, String totalTime) {
        this.speed = speed;
        this.steps = steps;
        this.totalTime = totalTime;

        Date date = new Date();
        this.date = date.toString();
    }

    public void storeToSharePref() {
        SharedPreferences sharePref = MainActivity.mainActivity.getSharedPreferences("WalkRunStats", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharePref.edit();
        HashSet<String> set = new HashSet<String>();

        set.add("speed: " + speed);
        set.add("steps: " + steps);
        set.add("totalTime: " + totalTime);

        editor.putStringSet(date, set);
        editor.apply();
    }
}
