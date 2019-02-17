package com.example.team31_personalbest;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResult;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class DataRetriever implements FitnessService {
    private final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = System.identityHashCode(this) & 0xFFFF;
    private final String TAG = "DataRetriever";

    private Activity activity;
    private GoogleApiClient historyClient;

    public DataRetriever(Activity activity) {
        this.activity = activity;
    }

    /**
     * filling implementation from FitnessService
     */
    public void setup() {
        historyClient = new GoogleApiClient.Builder(activity)
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks)activity)
                .enableAutoManage((FragmentActivity) activity, 0, (GoogleApiClient.OnConnectionFailedListener) activity)
                .build();

        /*
            if permissions aren't yet granted, request for them, otherwise continue as normal
         */
        /*
        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(activity),
                                        (GoogleSignInOptionsExtension)historyClient)) {
            GoogleSignIn.requestPermissions(
                    activity, // your activity
                    GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                    GoogleSignIn.getLastSignedInAccount(activity),
                    (GoogleSignInOptionsExtension)historyClient);
        }
        */
    }


/*    private void subscribe() {
        //check if logged in
        GoogleSignInAccount lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(activity);
        if (lastSignedInAccount == null) {
            return;
        }

        *//* subscribing to total steps, *//*
        Fitness.getRecordingClient(activity, GoogleSignIn.getLastSignedInAccount(activity))
                .subscribe(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "Successfully subscribed!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "There was a problem subscribing.");
                    }
                });

    }*/


    /**
     * Necessary to implement FitnessService
     */
    public void updateStepCount() {
    }


    /**
     * filling implementation from FitnessService
     */
    @Override
    public int getRequestCode() {
        return GOOGLE_FIT_PERMISSIONS_REQUEST_CODE;
    }

    public List<Bucket> retrieveAggregatedData(DataType dt, DataType agg) {

        List<Bucket> buckets = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        // setting time to midnight
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        // setting today as the last of the days to retrieve data from
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.WEEK_OF_YEAR, -1);
        // setting startTime as a week ago from endTime
        long startTime = calendar.getTimeInMillis();

        /*
        Requests for regular steps and planned steps respectively
         */

        // Create a request to aggregate all of dt by day
        DataReadRequest dataReadRequest = new DataReadRequest.Builder()
                .aggregate(dt, agg)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        DataReadResult dataReadResult = Fitness.HistoryApi.readData(historyClient, dataReadRequest)
                .await(1, TimeUnit.MINUTES);

        //Used for aggregated data
        if (dataReadResult.getBuckets().size() > 0) {

            for (Bucket bucket : dataReadResult.getBuckets()) {
                /*
                List<DataSet> dataSets = bucket.getDataSets();
                for (DataSet dataSet : dataSets) {
                    dataSets.add(dataSet);
                }
                */
                buckets.add(bucket);
            }
        }
        return buckets;
    }

    // should request different types of data
    //      for the progressChart we should get steps and planned steps per each day
    //      for other data we should get activity steps, time, and speed
    public List<DataSet> retrieveData(DataType dt) {

        List<DataSet> dataSets = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        // setting time to midnight
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        // setting today as the last of the days to retrieve data from
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.WEEK_OF_YEAR, -1);
        // setting startTime as a week ago from endTime
        long startTime = calendar.getTimeInMillis();

        /*
        Requests for regular steps and planned steps respectively
         */
        //Check how many steps were walked and recorded in the last 7 days
        DataReadRequest dataReadRequest = new DataReadRequest.Builder()
                .read(dt)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        DataReadResult dataReadResult = Fitness.HistoryApi.readData(historyClient, dataReadRequest)
                .await(1, TimeUnit.MINUTES);

        if (dataReadResult.getDataSets().size() > 0) {
            Log.e("History", "Number of returned DataSets: " + dataReadResult.getDataSets().size());
            for (DataSet dataSet : dataReadResult.getDataSets()) {
                dataSets.add(dataSet);
            }
        }
        return dataSets;
    }

}