package com.example.team31_personalbest;

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

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class ProgressActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{
    private final String[] dayNames = { "Sunday", "Monday", "Tuesday",
            "Wednesday", "Thursday", "Friday", "Saturday"};

    private final String[] dayAbbrev = { "Sun", "Mon", "Tues",
            "Wed", "Thu", "Fri", "Sat"};

    DataRetriever dr;
    List<Integer> ups;
    List<BarEntry> stepVals;
    BarChart barChart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        // making the barchart from the view
        barChart = findViewById(R.id.graphProgress);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setCenterAxisLabels(true);
        xAxis.setDrawGridLines(false);
        //xAxis.setValueFormatter(new DateAxisValueFormatter(dayAbbrev));

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f);
        barChart.getAxisRight().setEnabled(false);

        stepVals = new ArrayList<BarEntry>();


        /*
          TODO code that pulls dat from shared preferences for steps and unplanned steps should
          TODO and fill the ps and ups int arrays that stand for planned steps and unplanned
          TODO steps respectively
         */
        dr = new DataRetriever(this);
        dr.setup();

        new Thread(new Runnable() {
           @Override
           public void run() {
              List<Bucket> unplannedSteps = dr.
                       retrieveAggregatedData(DataType.TYPE_STEP_COUNT_DELTA,
                               DataType.AGGREGATE_STEP_COUNT_DELTA);
              ups = new ArrayList();
              for(Bucket b : unplannedSteps) {
                  for(DataSet ds : b.getDataSets()) {
                      for (DataPoint dp : ds.getDataPoints()) {
                          for(Field field: dp.getDataType().getFields()) {
                              Log.d("UPS VALUE", dp.getValue(field).asInt() + " steps");
                              ups.add(dp.getValue(field).asInt());
                          }
                      }
                  }
              }
               int[] ps = {762, 720, 710, 732, 720, 600, 500};
               //int[] ups = {612, 264, 523, 498, 100, 55, 173};

               // populate BarEntries
               for (int i = 0; i < ups.size(); i++) {
                   stepVals.add(new BarEntry(i, new float[]{ps[i], ups.get(i)}));
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


class DateAxisValueFormatter implements IAxisValueFormatter {
    private String[] vals;

    public DateAxisValueFormatter(String[] values) {
        this.vals = values;
    }

    public String getFormattedValue(float value, AxisBase axis) {
        return vals[(int) value];
    }
}
