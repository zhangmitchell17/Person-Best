package com.example.team31_personalbest_ms2v2;

import android.util.Log;

import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.Field;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.example.team31_personalbest_ms2v2.Constants.*;

public class AggregateData {
    List<Bucket> aggData;
    List<Integer> intList;
    List<String> dateList;
    public AggregateData(List<Bucket> aggData) {
        this.aggData = aggData;
        format();
    }

    private void format() {
        intList = new ArrayList<>();
        dateList = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat date = new SimpleDateFormat(MONTH_DAY_FMT);

        // string formats for logging
        DateFormat dateFormat = DateFormat.getDateInstance();
        DateFormat timeFormat = DateFormat.getTimeInstance();

        /*
         * each nested object only has one object in it besides unplannedSteps itself
         * just iterating over to access the innermost object which is Field
         */
        for(Bucket b : aggData) {
            for (DataSet ds : b.getDataSets()) {
                Log.e("History", "Data returned for Data type: " + ds.getDataType().getName());
                for (DataPoint dp : ds.getDataPoints()) {
                    cal.setTimeInMillis(dp.getStartTime(TimeUnit.MILLISECONDS));
                    dateList.add(date.format(cal.getTime()));
                    for (Field field : dp.getDataType().getFields()) {
                        /*
                         * if we haven't reached sunday yet and the day is sunday,
                         * set the corresponding boolean to true
                         */
                        Log.e("History", "Data point:");
                        Log.e("History", "\tType: " + dp.getDataType().getName());
                        Log.e("History", "\tStart: " +
                                dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)) + " " +
                                timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
                        Log.e("History", "\tEnd: " +
                                dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)) + " " +
                                timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
                        Log.d("UPS VALUE", dp.getValue(field).asInt() + " " + field.getName());
                        intList.add(dp.getValue(field).asInt());

                    }
                }
            }
        }
    }

    public List<Integer> toIntList() {
        return intList;
    }

    public List<String> getDateList() {
        return dateList;
    }
}
