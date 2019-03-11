package com.example.team31_personalbest_ms2v2;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;


import com.github.mikephil.charting.charts.BarChart;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataType;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.example.team31_personalbest_ms2v2.Constants.*;

/**
 * This file defines the ProgressActivity class which is used to record
 * the weekly and daily progress of a user
 */
public class ProgressActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private Activity act = this;
    private final String[] dayNames = { "Sunday", "Monday", "Tuesday",
            "Wednesday", "Thursday", "Friday", "Saturday"};

    private final String[] dayAbbrev = { "Sun", "Mon", "Tues",
            "Wed", "Thu", "Fri", "Sat"};

    private static final String[] TIME_STEPS_MPH = {"Time", "Steps", "MPH"};

    private final int STEPS_IDX = 1;

    private DataRetriever dr;
    private BarChart barChart;
    private FirebaseFirestore db;
    private CollectionReference plannedWalks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        // making the barchart from the view
        barChart = findViewById(R.id.graphProgress);

        // TODO initialize firebase
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

        String email = "";

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            email = acct.getEmail();
        }

        plannedWalks = db.collection("users")
                         .document(email)
                         .collection("WalkRuns");

        dr = new DataRetriever(this);
        dr.setup();

        /* running on a separate thread so that it doesn't stall the activity and crash */
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO replace following code to populate ups with things from cloud
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

                //getting planned walks urns from cloud
                CloudDataRetriever cdr = new CloudDataRetriever(act);
                HashMap<String, HashMap<String, Integer>> map = cdr.parseData(DAYS_PER_WEEK);
                Map<String, Integer> plannedStepsPerDay = map.get("plannedWalksRuns");

                // add todays data since retrieve the last weeks data is exclusive of today
                dateLabels[dateLabels.length-1] = (new SimpleDateFormat(MONTH_DAY_FMT)).format(new Date());
                ups.add(dr.retrieveTodaysSteps());

                /* post processing ps data */
                List<Integer> ps = new ArrayList<>();

                // TODO transfer data from plannedStepsPerDay into ps

                /*
                 * print all keys in plannedstepsperday
                 */
                Log.i("PROGRESS_KEYS", plannedStepsPerDay.keySet().toString());

                /* for dates that are in upsdatapoints, then check if they exist in maps, and
                 * then populate the corresponding
                 * things in ps
                 */
                Calendar cal = Calendar.getInstance();
                String key;
                SimpleDateFormat aggFormat = new SimpleDateFormat(MONTH_DAY_FMT);
                SimpleDateFormat cloudFormat = new SimpleDateFormat("MMM dd yyyy");
                for(String s : dateLabels) {
                    try {
                        cal.setTime(aggFormat.parse(s));
                    } catch (Exception e) {
                        Log.e(this.getClass().getSimpleName(),
                                "LABEL FROM AGG DATA WAS INCORRECTLY FORMATTED");
                    }
                    ps.add(plannedStepsPerDay.get(cloudFormat.format(cal)));
                }
                // TODO COMMENTED OUT BECAUSE CLOUD SHOULD GATHER DATA FOR TODAY INCLUSIVE
                // TODO DELETE IF THIS IS THE CASE
//                /*
//                 * if plannedStepsPerDay has data for today, then add it to ps
//                 */
//                cal.setTimeInMillis(System.currentTimeMillis());
//                key = day.format(cal.getTime());
//                if(plannedStepsPerDay.containsKey(key)) {
//                    Log.i("PROGRESS_VALUE", "plannedStepsPerDay.get(key): "+plannedStepsPerDay.get(key));
//                    ps.add(plannedStepsPerDay.get(key));
//                }

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

}