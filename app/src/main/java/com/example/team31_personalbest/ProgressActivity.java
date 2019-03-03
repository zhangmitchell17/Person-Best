package com.example.team31_personalbest;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;


import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.example.team31_personalbest.Constants.*;

/**
 * This file defines the ProgressActivity class which is used to record
 * the weekly and daily progress of a user
 */
public class ProgressActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private final String[] dayNames = { "Sunday", "Monday", "Tuesday",
            "Wednesday", "Thursday", "Friday", "Saturday"};

    private final String[] dayAbbrev = { "Sun", "Mon", "Tues",
            "Wed", "Thu", "Fri", "Sat"};

    private static final String[] TIME_STEPS_MPH = {"Time", "Steps", "MPH"};

    private final int STEPS_IDX = 1;

    DataRetriever dr;
    BarChart barChart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        // making the barchart from the view
        barChart = findViewById(R.id.graphProgress);

        dr = new DataRetriever(this);
        dr.setup();

        /* running on a separate thread so that it doesn't stall the activity and crash */
        new Thread(new Runnable() {
            @Override
            public void run() {
               /*
                unplannedSteps contains the data from the past seven days
                */
                List<Bucket> unplannedSteps = dr.
                       retrieveAggregatedData(DataType.TYPE_STEP_COUNT_DELTA,
                               DataType.AGGREGATE_STEP_COUNT_DELTA, Calendar.DAY_OF_YEAR, DAYS_PER_WEEK-1);
                AggregateData ad = new AggregateData(unplannedSteps);
                List<Integer> ups = ad.toIntList();
                List<String> strList = ad.getDateList();
                String[] dateLabels = new String[strList.size()+1];
                dateLabels = strList.toArray(dateLabels);


                SharedPreferences sharedPrefs = MainActivity.mainActivity.getSharedPreferences("WalkRunStatsDate", MODE_PRIVATE);
                Map<String,?> keys = sharedPrefs.getAll();

                SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
                SimpleDateFormat day = new SimpleDateFormat("MM-dd-yyyy");
                Map<String, Integer> plannedStepsPerDay = new HashMap<>();

                /* populating plannedStepsPerDay */
                Log.i("SIZE_OF_SP", "SharedPref size : " + keys.size());
                /*
                 * for loop to loop through all entries of sharedpreferences
                 */
                for(Map.Entry<String,?> entry : keys.entrySet()){
                    Log.d("PROG_ACT_SDF","key in sharedprefss: " + entry.getKey());

                    HashSet<String> values = (HashSet<String>) entry.getValue();
                    /* try catch to see if the date is parseable */
                    try {
                        // seeing if the key is parseable
                        Date date = sdf.parse(entry.getKey());

                        // making another string out of it with another format
                        // specified by "day"
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(date);
                        String dayDate = day.format(cal.getTime());

                        // code to add steps to proper day
                        int count = plannedStepsPerDay.containsKey(dayDate) ?
                                plannedStepsPerDay.get(dayDate) :
                                0;

                        // variable to hold new steps for the planned walk/run
                        int moreSteps = 0;

                        // loop that iterates through the hash set given by the key
                        for(String s : values) {
                            int index = -1;
                            Log.i("VALUE_IN_VALUES", "values.s = " + s);
                            // each string in the hash set starts with either time
                            // steps, or mph, so iterate until we find which identifer it
                            // starts with
                            if (s.substring(0, s.indexOf(":")).equals("Steps")) {
                                moreSteps = Integer.parseInt(s.substring(s.indexOf(":") + 2));
                                Log.i("MORE_STEPS", "moreSteps = " + moreSteps);
                            }
                        }

                        plannedStepsPerDay.put(dayDate, count + moreSteps);

                    } catch (Exception e) {
                        Log.e("PROG_ACT_SDF","String cannot be parsed so its not a date");
                        e.printStackTrace();
                    }
                }


                // add todays data since retrieve the last weeks data is exclusive of today
                dateLabels[dateLabels.length-1] = (new SimpleDateFormat(MONTH_DAY_FMT)).format(new Date());
                ups.add(dr.retrieveTodaysSteps());

                /* post processing ps data */
                List<Integer> ps = new ArrayList<>();
                /*
                 * print all keys in plannedstepsperday
                 */
                Log.i("PROGRESS_KEYS",plannedStepsPerDay.keySet().toString());

                /* for dates that are in upsdatapoints, then check if they exist in maps, and
                 * then populate the corresponding
                 * things in ps
                 */
                Calendar cal = Calendar.getInstance();
                String key;

                /* assumes that for every day that unplanned steps are made, there
                 * are planned steps
                 */

                /*
                 * if plannedStepsPerDay has data for today, then add it to ps
                 */
                cal.setTimeInMillis(System.currentTimeMillis());
                key = day.format(cal.getTime());
                if(plannedStepsPerDay.containsKey(key)) {
                    Log.i("PROGRESS_VALUE", "plannedStepsPerDay.get(key): "+plannedStepsPerDay.get(key));
                    ps.add(plannedStepsPerDay.get(key));
                }

                Log.i("PROGRESS", "printing values in ps");
                for(Integer i : ps) {
                    Log.i("PROGRESS", "Value is " + i);
                }

                // filling in zeroes where we hvae no data
                int oldPSSize = ps.size();
                for(int j = 0; j < 7-oldPSSize; j++) {
                    ps.add(0);
                }
                int oldUPSSize = ups.size();
                for(int k = 0; k < 7-oldUPSSize; k++) {
                    ups.add(0);
                }

                Log.i("UPS_SIZE", "ups size is " + ups.size());
                Log.i("PS_SIZE", "ps size is " + ps.size());

                ProgressChart pc = new ProgressChart(barChart, ups, ps, ups.size(), dateLabels);
                pc.setup();
            }
        }).start();

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("HistoryAPI", "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("HistoryAPI", "onConnectionFailed");
    }

    public void onConnected(@Nullable Bundle bundle) {
        Log.e("HistoryAPI", "onConnected");

    }

    public Calendar getMostRecentSunday() {
        Calendar cal = Calendar.getInstance();
        while(cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            cal.add(Calendar.DAY_OF_YEAR, -1);
        }
        return cal;
    }

}