package com.example.team31_personalbest;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import android.content.SharedPreferences;
import android.os.Handler;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static android.content.Context.MODE_PRIVATE;
import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class PastWalksTest {
    private PastWalksActivity pastWalksActivity;
    private TextView sundayTime;
    private TextView mondayTime;
    private TextView tuesdayTime;
    private TextView wednesdayTime;
    private TextView thursdayTime;
    private TextView fridayTime;
    private TextView saturdayTime;
    private TextView sundaySteps;
    private TextView mondaySteps;
    private TextView tuesdaySteps;
    private TextView wednesdaySteps;
    private TextView thursdaySteps;
    private TextView fridaySteps;
    private TextView saturdaySteps;
    private TextView sundaySpeed;
    private TextView mondaySpeed;
    private TextView tuesdaySpeed;
    private TextView wednesdaySpeed;
    private TextView thursdaySpeed;
    private TextView fridaySpeed;
    private TextView saturdaySpeed;

    @Before
    public void setup() {
        pastWalksActivity = Robolectric.setupActivity(PastWalksActivity.class);
        sundayTime = pastWalksActivity.findViewById(R.id.sundayTime);
        sundaySteps = pastWalksActivity.findViewById(R.id.sundaySteps);
        sundaySpeed = pastWalksActivity.findViewById(R.id.sundayMPH);
        mondayTime = pastWalksActivity.findViewById(R.id.mondayTime);
        mondaySteps = pastWalksActivity.findViewById(R.id.mondaySteps);
        mondaySpeed = pastWalksActivity.findViewById(R.id.mondayMPH);
        tuesdayTime = pastWalksActivity.findViewById(R.id.tuesdayTime);
        tuesdaySteps = pastWalksActivity.findViewById(R.id.tuesdaySteps);
        tuesdaySpeed = pastWalksActivity.findViewById(R.id.tuesdayMPH);
        wednesdayTime = pastWalksActivity.findViewById(R.id.wednesdayTime);
        wednesdaySteps = pastWalksActivity.findViewById(R.id.wednesdaySteps);
        wednesdaySpeed = pastWalksActivity.findViewById(R.id.wednesdayMPH);
        thursdayTime = pastWalksActivity.findViewById(R.id.thursdayTime);
        thursdaySteps = pastWalksActivity.findViewById(R.id.thursdaySteps);
        thursdaySpeed = pastWalksActivity.findViewById(R.id.thursdayMPH);
        fridayTime = pastWalksActivity.findViewById(R.id.fridayTime);
        fridaySteps = pastWalksActivity.findViewById(R.id.fridaySteps);
        fridaySpeed = pastWalksActivity.findViewById(R.id.fridayMPH);
        saturdayTime = pastWalksActivity.findViewById(R.id.saturdayTime);
        saturdaySteps = pastWalksActivity.findViewById(R.id.saturdaySteps);
        saturdaySpeed = pastWalksActivity.findViewById(R.id.saturdayMPH);
    }

    // Tests if basic layout exists
    @Test
    public void testInitial() {}

    // Tests if height is revealed after inputting : - )
    @Test
    public void testUpdateWalks() {
    }
}
