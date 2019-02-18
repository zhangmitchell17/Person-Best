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
    List<Integer> ups;
    List<DataPoint> upsDataPoints;
    List<BarEntry> stepVals;
    BarChart barChart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        // making the barchart from the view
        barChart = findViewById(R.id.graphProgress);

        XAxis xAxis = barChart.getXAxis();

        // Making the x axis labeled by day
        xAxis.setValueFormatter(new IndexAxisValueFormatter(dayAbbrev));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawLabels(true);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setAxisMinimum(-1);
        xAxis.setAxisMaximum(7);

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f);
        barChart.getAxisRight().setEnabled(false);

        stepVals = new ArrayList<>();

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
                               DataType.AGGREGATE_STEP_COUNT_DELTA);
                ups = new ArrayList();
                upsDataPoints = new ArrayList<>();
                DateFormat dateFormat = DateFormat.getDateInstance();
                DateFormat timeFormat = DateFormat.getTimeInstance();

                /*
                 * only append once sunday is reached since we don't necessary want the past weeks
                 * data, but the data for this week
                 */
                boolean afterSunday = false;
                boolean dataForFirstDay = false;
                Calendar calendar = Calendar.getInstance();

                /*
                 * each nested object only has one object in it besides unplannedSteps itself
                 * just iterating over to access the innermost object which is Field
                 */
                for(Bucket b : unplannedSteps) {
                    for(DataSet ds : b.getDataSets()) {
                        Log.e("History", "Data returned for Data type: " + ds.getDataType().getName());
                        for (DataPoint dp : ds.getDataPoints()) {
                            calendar.setTimeInMillis(dp.getStartTime(TimeUnit.MILLISECONDS));
                            if(!afterSunday && calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                                afterSunday = true;
                            }
                            if(afterSunday) {
                                upsDataPoints.add(dp);
                            }
                            for(Field field: dp.getDataType().getFields()) {
                                /*
                                 * if we haven't reached sunday yet and the day is sunday,
                                 * set the corresponding boolean to true
                                 */

                                if(afterSunday) {
                                    Log.e("History", "Data point:");
                                    Log.e("History", "\tType: " + dp.getDataType().getName());
                                    Log.e("History", "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
                                    Log.e("History", "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
                                    Log.d("UPS VALUE", dp.getValue(field).asInt() + " " + field.getName());
                                    ups.add(dp.getValue(field).asInt());

                                }
                            }
                        }
                    }
                }

                /*
                 * TODO write code to get planned walks run and replace ps with a proper array
                 */
                SharedPreferences sharedPrefs = MainActivity.mainActivity.getSharedPreferences("WalkRunStats", MODE_PRIVATE);
                Map<String,?> keys = sharedPrefs.getAll();

                SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
                SimpleDateFormat day = new SimpleDateFormat("MM-dd-yyyy");
                Map<String, Integer> plannedStepsPerDay = new HashMap<>();

                /* populating plannedStepsPerDay */
                Log.i("SIZE_OF_SP", "SharedPref size : " + keys.size());
                for(Map.Entry<String,?> entry : keys.entrySet()){
                    Log.d("PROG_ACT_SDF","key in sharedprefss: " + entry.getKey());

                    HashSet<String> values = (HashSet<String>) entry.getValue();
                    try {
                        Calendar cal = Calendar.getInstance();
                        Date date = sdf.parse(entry.getKey());
                        cal.setTime(date);
                        String dayDate = day.format(cal.getTime());

                        /* code to add steps to proper day */
                        int count = plannedStepsPerDay.containsKey(dayDate) ?
                                plannedStepsPerDay.get(dayDate) :
                                0;

                        int moreSteps = 0;
                        // loop that iterates through the hash set given by the key
                        Log.i("SIZE_OF_VALUES", "values.size = " + values.size());
                        for(String s : values) {
                            int index = -1;
                            // each string in the hash set starts with either time
                            // steps, or mph, so iterate until we find which identifer it
                            // starts with
                            Log.i("VALUE_IN_VALUES", "values.s = " + s);
                            if (s.substring(0, s.indexOf(":")).equals("steps")) {

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

                /* post processing ups data
                 */

                // getting the starting time of the first day of data we retrieve
                long timeOfFirstDay = upsDataPoints.get(0)
                        .getStartTime(TimeUnit.MILLISECONDS);

                // getting the number of the first day we retrieve data for
                calendar.setTimeInMillis(timeOfFirstDay);
                int firstDay = calendar.get(Calendar.DAY_OF_WEEK);

                if(firstDay == Calendar.SUNDAY) {
                    ups.clear();
                }

                /*
                 * if it never reached sunday then we don't have data for a sunday
                 * meaning the interval of time for which we have data
                 * is after the previous sunday and before the current sunday
                 */
                if(!afterSunday) {
                    /*
                     * so fill the days before the current day with 0
                     */
                    for(int i = 1; i <= firstDay; i++) {
                        ups.add(0, 0);
                    }
                }

                // add todays data since retrieve the last weeks data is exclusive of today
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

                for(DataPoint dp : upsDataPoints) {
                    cal.setTimeInMillis(dp.getStartTime(TimeUnit.MILLISECONDS));
                    key = day.format(cal.getTime());
                    // if the day exists in the map, then add it to ps
                    Log.i("DEBUG", "first dp in upsDataPoints is on " + key);
                    if(plannedStepsPerDay.containsKey(key)) {
                        ps.add(plannedStepsPerDay.get(key));
                    }
                }

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

                int oldPSSize = ps.size();
                for(int j = 0; j < 7-oldPSSize; j++) {
                    ps.add(0);
                }
                int oldUPSSize = ups.size();
                for(int k = 0; k < 7-oldUPSSize; k++) {
                    ups.add(0);
                }

                // populate BarEntries
                for (int i = 0; i < 7; i++) {
                    stepVals.add(new BarEntry(i, new float[]{ps.get(i), ups.get(i)}));
                }

                // making dataset from set
                BarDataSet set = new BarDataSet(stepVals, "Steps");

                // labels for the chart legend
                set.setStackLabels(new String[]{"Planned Steps", "Unplanned Steps"});
                set.setColors(Color.parseColor("#81dafc"), // pastel green
                       Color.parseColor(("#77dd77"))); // pastel blue
                BarData data = new BarData(set);

                barChart.getDescription().setEnabled(false);
                barChart.setData(data);

                barChart.invalidate(); // refresh
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

}