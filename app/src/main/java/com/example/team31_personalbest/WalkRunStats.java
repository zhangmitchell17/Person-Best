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
    String dayOfWeek;
    String monthDayYear;

    /**
     * Contruct a walkRunStas object
     * @param speed avg speed of user
     * @param steps steps of user in a walk run activity
     */
    public WalkRunStats(String speed, String steps, String totalTime) {
        this.speed = speed;
        this.steps = steps;
        this.totalTime = totalTime;

        Date day = new Date();
        this.date = day.toString();
        // Sun
        this.dayOfWeek = date.substring(0, date.indexOf(" "));
        int indexOfEnd = date.indexOf(" ", date.indexOf(" ", date.indexOf(" ") + 1) +1);
        // Feb 17 2019
        this.monthDayYear = date.substring(date.indexOf(" ") + 1, indexOfEnd) + " " + date.substring(date.length() - 4);

    }

    public void storeToSharePref() {
        SharedPreferences sharePref = MainActivity.mainActivity.getSharedPreferences("WalkRunStats", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharePref.edit();
        HashSet<String> set = new HashSet<String>();

        set.add("Time: " + totalTime);
        set.add("Steps: " + steps);
        set.add("MPH: " + speed);
        set.add("dayOfWeek: " + dayOfWeek);
        set.add("monthDayYear: " + monthDayYear);

        editor.putStringSet(date, set);
        editor.apply();
    }
}
