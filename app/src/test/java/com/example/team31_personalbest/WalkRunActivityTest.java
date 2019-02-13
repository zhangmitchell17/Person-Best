package com.example.team31_personalbest;

import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class WalkRunActivityTest {

    private WalkRunActivity walkRunActivity;
    private TextView timeDisplay;
    private TextView stepDisplay;
    private TextView speedDisplay;

    @Before
    public void setup() {
        walkRunActivity = Robolectric.setupActivity(WalkRunActivity.class);
        timeDisplay = walkRunActivity.findViewById(R.id.textViewTimer);
        stepDisplay = walkRunActivity.findViewById(R.id.textViewSteps);
        speedDisplay = walkRunActivity.findViewById(R.id.textViewSpeed);
    }

    @Test
    public void testInitial() {
        assertEquals("00:00:00", timeDisplay.getText());
        assertEquals("0", stepDisplay.getText());
        assertEquals("0", speedDisplay.getText());
    }
}