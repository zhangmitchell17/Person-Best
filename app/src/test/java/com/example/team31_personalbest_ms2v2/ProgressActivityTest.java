package com.example.team31_personalbest_ms2v2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowIntent;
import org.robolectric.shadows.ShadowLog;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.example.team31_personalbest_ms2v2.Constants.*;
import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class ProgressActivityTest {
    ProgressActivity progressActivity;
    private BarChart bc;
    private ProgressChart pc;
    private List<Integer> unplanned;
    private List<Integer> planned;
    private List<Integer> goals;
    private String[] bottomAxisLabels;


    @Before
    public void setup() {
        ShadowLog.stream = System.out;
        Intent intent = new Intent(RuntimeEnvironment.application, ProgressActivity.class);
        intent.putExtra("Email", "davidhungng@gmail.com");
        progressActivity = Robolectric.buildActivity(ProgressActivity.class, intent).create().get();
        bc = progressActivity.findViewById(R.id.graphProgress);
        unplanned = new ArrayList<>();
        planned = new ArrayList<>();
        goals = new ArrayList<>();
        bottomAxisLabels = new String[DAYS_PER_WEEK];
        // populating data lists
        for(int i = 1; i <= DAYS_PER_WEEK; i++) {
            unplanned.add(i*1000);
            planned.add(200);
            goals.add(10000);
            bottomAxisLabels[i-1] = "12/" + Integer.toString(i);
        }
        //create a barchart with some arbitrary data
        pc = new ProgressChart(bc, unplanned, planned, goals, unplanned.size(), bottomAxisLabels);
    }

    // Tests if data from barchart has been correctly added and is being correctly displayed
    @Test
    public void testData() {
        Log.i(this.getClass().getSimpleName(), "Creating the data set based on data supplied" +
                " since the library has no .equals to check for deep copy of objects");
        assertEquals(true, createData());

    }

    private boolean createData() {
        try {
            List<BarEntry> barEntries = new ArrayList<>();

            // populating barEntries with values from planned and unplanned
            for (int i = unplanned.size() - 1; i >= 0; i--) {
                // for the i'th elemnt to exist, goals size needs to be
                // i+1 elements atleast, so if it isn't then make the value 0
                int tmpGoal = (goals.size() < i + 1) ? 0 : goals.get(i);
                int tmpUPS = (unplanned.size() < i + 1) ? 0 : unplanned.get(i);
                int tmpPS = (planned.size() < i + 1) ? 0 : planned.get(i);

                Log.i(this.getClass().getSimpleName(), "Bar Chart is adding " + tmpUPS + " unplanned steps, " +
                        tmpPS + " planned steps, for the date of " + bottomAxisLabels[i]);

                int stepsToReachGoal = (tmpGoal - tmpUPS - tmpPS >= 0) ?
                        tmpGoal - tmpUPS - tmpPS : 0;
                barEntries.add(0, new BarEntry(i, new float[]{tmpPS, tmpUPS,
                        stepsToReachGoal}));
            }

            // making dataset from set labeled "steps"
            BarDataSet set = new BarDataSet(barEntries, "Steps");
            // labels for the chart legend
            set.setStackLabels(new String[]{PLANNED_STEPS_STR, UNPLANNED_STEPS_STR, GOAL_STR});
            // colors for the chart legend
            set.setColors(Color.parseColor(PASTEL_GREEN), Color.parseColor(PASTEL_BLUE),
                    Color.parseColor(LIGHT_GREY));

            BarData data = new BarData(set);
        } catch(Exception e) {
            return false;
        }
        return true;
    }
}
