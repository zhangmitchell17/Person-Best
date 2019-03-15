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

    private BarChart barChart;
    private FirebaseFirestore db;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        // making the barchart from the view
        barChart = findViewById(R.id.graphProgress);

        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

        Bundle b = getIntent().getExtras();
        email = "";
        if(b!=null) {
            email = b.getString("Email");
        }

        SimpleDateFormat monthDayFormat = new SimpleDateFormat(MONTH_DAY_FMT);
        List<String> dateLabelList = new ArrayList<>();

        // create list of date strings
        for (int i = 0; i > -1*DAYS_PER_WEEK; i--) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, i);
            dateLabelList.add(0, monthDayFormat.format(cal.getTime()));
            Log.i("SHIT", "Date: " + monthDayFormat.format(cal.getTime()));
        }

        String[] dateLabels = new String[dateLabelList.size() + 1];
        dateLabels = dateLabelList.toArray(dateLabels);

        //getting planned walks urns from cloud
        CloudDataRetriever cdr = new CloudDataRetriever(act, email);
        Log.i("SHIT", "ABOUT TO CALL PARSEDATA");
        cdr.parseData(DAYS_PER_WEEK);

        List<Integer> ps = new ArrayList<>();
        List<Integer> ups = new ArrayList<>();
        List<Integer> goals = new ArrayList<>();

        for (int i = 0; i < DAYS_PER_WEEK; i++) {
            ps.add(0);
            ups.add(0);
            goals.add(0);
        }
        Log.i("UPS_SIZE", "ups size is " + ups.size());
        Log.i("PS_SIZE", "ps size is " + ps.size());

        ProgressChart pc = new ProgressChart(barChart, ups, ps, goals, dateLabelList.size(), dateLabels);
        cdr.register(pc);
        pc.setup();
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