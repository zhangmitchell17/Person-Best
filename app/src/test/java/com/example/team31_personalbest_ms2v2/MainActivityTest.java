package com.example.team31_personalbest_ms2v2;

import android.content.SharedPreferences;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;
import static org.junit.Assert.assertEquals;


@RunWith(RobolectricTestRunner.class)
public class MainActivityTest {
    MainActivity activity;
    ActivityController<MainActivity> controller;

    @Before
    public void init() {
        activity = Robolectric.setupActivity(MainActivity.class);
        controller = Robolectric.buildActivity(MainActivity.class);
        controller.create();
    }

    @Test
    public void testIfStepIsResetSameDay() {
        SharedPreferences sharePref = activity.getSharedPreferences("resetSteps", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharePref.edit();
        editor.putString("date", new SimpleDateFormat("MM-dd-yyyy").
                format(Calendar.getInstance().getTime()));
        editor.putInt("steps", 100);
        editor.apply();
        controller.resume();
        assertEquals(sharePref.getInt("steps", -1), 100);
    }

    @Test
    public void testIfSetStepCount() {
        SharedPreferences sharePref = activity.getSharedPreferences("setStepCount", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharePref.edit();
        editor.putString("date", new SimpleDateFormat("MM-dd-yyyy").
                format(Calendar.getInstance().getTime()));
        editor.putInt("steps", 100);
        editor.apply();
        controller.resume();
        assertEquals(sharePref.getInt("steps", -1), 100);
    }
}
