package com.example.team31_personalbest;

import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.TextView;
import com.example.team31_personalbest.MainActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;

import static android.content.Context.MODE_PRIVATE;
import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class WalkRunActivityTest {

    private WalkRunActivity walkRunActivity;
    private MainActivity mainActivity;
    private TextView timeDisplay;
    private TextView stepDisplay;
    private TextView speedDisplay;
    private ActivityController<MainActivity> controller;

    @Before
    public void setup() {
        mainActivity = Robolectric.setupActivity(MainActivity.class);
        timeDisplay = walkRunActivity.findViewById(R.id.textViewTimer);
        stepDisplay = walkRunActivity.findViewById(R.id.textViewSteps);
        speedDisplay = walkRunActivity.findViewById(R.id.textViewSpeed);
        controller = Robolectric.buildActivity(MainActivity.class);
        controller.create();
    }

    @Test
    public void testInitial() {
        assertEquals("00:00:00", timeDisplay.getText());
        assertEquals("0", stepDisplay.getText());
        assertEquals("0", speedDisplay.getText());
    }

}