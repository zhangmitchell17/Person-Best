package com.example.team31_personalbest_ms2v2;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResult;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DataRetriever {

    private Activity activity;
    private GoogleApiClient historyClient;

    public DataRetriever(Activity activity) {
        this.activity = activity;
    }

    /**
     * filling implementation from FitnessService
     *
     */
    public void setup() {
        historyClient = new GoogleApiClient.Builder(activity)
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks)activity)
                .enableAutoManage((FragmentActivity) activity, 0, (GoogleApiClient.OnConnectionFailedListener) activity)
                .build();
    }

    /**
     * pulls todays step data from Google Fit's History API
     * @return number of steps taken yesterday
     */
    public int retrieveTodaysSteps() {

        Calendar calendar = Calendar.getInstance();
        // setting today as the last of the days to retrieve data from
        long endTime = calendar.getTimeInMillis();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        // setting startTime as a week ago from endTime
        long startTime = calendar.getTimeInMillis();


        DataSource dataSource = new DataSource.Builder()
                .setAppPackageName("com.google.android.gms")
                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .setType(DataSource.TYPE_DERIVED)
                .setStreamName("estimated_steps")
                .build();

        DataReadRequest dataReadRequest = new DataReadRequest.Builder()
                .aggregate(dataSource, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        DataReadResult dataReadResult = Fitness.HistoryApi.readData(historyClient, dataReadRequest)
                .await(1, TimeUnit.MINUTES);

        //Used for aggregated data
        if (dataReadResult.getBuckets().size() > 0) {

            for (Bucket bucket : dataReadResult.getBuckets()) {
                for(DataSet dataSet : bucket.getDataSets()) {
                    for (DataPoint dp : dataSet.getDataPoints()) {
                        for(Field field: dp.getDataType().getFields()) {
                            Log.d("UPS VALUE", dp.getValue(field).asInt() + " steps");
                            return dp.getValue(field).asInt();
                        }
                    }
                }
            }
        }

        return -1;
    }

    /**
     * pulls yesterdays step data from Google Fit's History API
     * @return number of steps taken yesterday
     */
    public int retrieveYesterdaysSteps() {
        List<Bucket> buckets = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        // setting time to midnight
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        // setting today as the last of the days to retrieve data from
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        // setting startTime as a week ago from endTime
        long startTime = calendar.getTimeInMillis();

        /*
        Requests for regular steps and planned steps respectively
         */

        // Create a request to aggregate all of dt by day
        DataReadRequest dataReadRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        DataReadResult dataReadResult = Fitness.HistoryApi.readData(historyClient, dataReadRequest)
                .await(1, TimeUnit.MINUTES);

        //Used for aggregated data
        if (dataReadResult.getBuckets().size() > 0) {

            for (Bucket bucket : dataReadResult.getBuckets()) {
                for(DataSet ds : bucket.getDataSets()) {
                    for (DataPoint dp : ds.getDataPoints()) {
                        for(Field field: dp.getDataType().getFields()) {
                            Log.d("UPS VALUE", dp.getValue(field).asInt() + " steps");
                            return dp.getValue(field).asInt();
                        }
                    }
                }
            }
        }

        return -1;
    }

    /**
     * functions that retrieves data of type dt from Google Fit's History API for the past week
     * and returns it in a list
     * @param dt datatype of the data that you want to request
     * @param agg datatype of the data that you want dt aggregated into
     * @param field value of the window of time you want to aggregate data for
     * @return a list of Buckets requested
     */
    public List<Bucket> retrieveAggregatedData(DataType dt, DataType agg, int field, int amount) {

        List<Bucket> buckets = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        // setting time to midnight
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        // setting today as the last of the days to retrieve data from
        long endTime = calendar.getTimeInMillis();
        calendar.add(field, -1*amount);
        // setting startTime as a week ago from endTime
        long startTime = calendar.getTimeInMillis();

        DateFormat dateFormat = DateFormat.getDateInstance();
        DateFormat timeFormat = DateFormat.getTimeInstance();
        Log.i("History", "Range Start: " + dateFormat.format(startTime) + " " + timeFormat.format(startTime));
        Log.i("History", "Range End: " + dateFormat.format(endTime) + " " + timeFormat.format(endTime));

        // Create a request to aggregate all of dt by day
        DataReadRequest dataReadRequest = new DataReadRequest.Builder()
                .aggregate(dt, agg)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        DataReadResult dataReadResult = Fitness.HistoryApi.readData(historyClient, dataReadRequest)
                .await(1, TimeUnit.MINUTES);

        // adding each bucket to the list
        if (dataReadResult.getBuckets().size() > 0) {
            for (Bucket bucket : dataReadResult.getBuckets()) {
                buckets.add(bucket);
            }
        }
        else {
            Log.e("Dataset", "No data was read : - (");
        }
        return buckets;
    }

    /**
     * functions that retrieves data of type dt for the past week and returns it
     * in a list
     * @param dt datatype of the data that you want to request
     * @return a list of datasets requested
     */
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
        DateFormat dateFormat = DateFormat.getDateInstance();
        DateFormat timeFormat = DateFormat.getTimeInstance();
        Log.i("History", "Range Start: " + dateFormat.format(startTime) + " " + timeFormat.format(startTime));
        Log.i("History", "Range End: " + dateFormat.format(endTime) + " " + timeFormat.format(endTime));

        //Check how many steps were walked and recorded in the last 7 days
        DataReadRequest dataReadRequest = new DataReadRequest.Builder()
                .read(dt)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        DataReadResult dataReadResult = Fitness.HistoryApi.readData(historyClient, dataReadRequest)
                .await(1, TimeUnit.MINUTES);

        // adding each dataset to the list
        if (dataReadResult.getDataSets().size() > 0) {
            for (DataSet dataSet : dataReadResult.getDataSets()) {
                dataSets.add(dataSet);
            }
        }
        else {
            Log.e("Dataset", "No data was read : - (");
        }
        return dataSets;
    }

}