package com.example.team31_personalbest_ms2v2;
import android.content.SharedPreferences;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.junit.runner.RunWith;
import org.robolectric.android.controller.ActivityController;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class ResetHeightTests {
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
    public void testIfStepIsResetDifferentDay() {
        SharedPreferences sharePref = activity.getSharedPreferences("resetSteps", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharePref.edit();
        editor.clear();
        editor.apply();

        editor.putString("date", "02-01-2019");
        editor.apply();
        editor.putInt("steps", 100);
        controller.resume();

        assertEquals(sharePref.getInt("steps", -1), 0);
        editor.remove("date");
        editor.apply();
    }

}