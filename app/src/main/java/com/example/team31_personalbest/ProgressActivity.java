package com.example.team31_personalbest;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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

import java.util.ArrayList;
import java.util.List;

public class ProgressActivity extends AppCompatActivity {
    private final String[] dayNames = { "Sunday", "Monday", "Tuesday",
            "Wednesday", "Thursday", "Friday", "Saturday"};

    private final String[] dayAbbrev = { "Sun", "Mon", "Tues",
            "Wed", "Thu", "Fri", "Sat"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        // making the barchart from the view
        BarChart barChart = findViewById(R.id.graphProgress);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setCenterAxisLabels(true);
        xAxis.setDrawGridLines(false);
        //xAxis.setValueFormatter(new DateAxisValueFormatter(dayAbbrev));

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f);
        barChart.getAxisRight().setEnabled(false);

        List<BarEntry> stepVals = new ArrayList<BarEntry>();


        /*
          TODO code that pulls dat from shared preferences for steps and unplanned steps should
          TODO and fill the ps and ups int arrays that stand for planned steps and unplanned
          TODO steps respectively
         */

        int[] ps = {762, 720, 710, 732, 720, 600, 500};
        int[] ups = {612, 264, 523, 498, 100, 55, 173};

        // populate BarEntries
        for (int i = 0; i < ps.length; i++) {
            stepVals.add(new BarEntry(i, new float[]{ps[i], ups[i]}));
        }

        // making dataset from set
        BarDataSet set = new BarDataSet(stepVals, "Steps");
        // labels for the chart leneged
        set.setStackLabels(new String[]{"Planned Steps", "Unplanned Steps"});
        set.setColors(Color.parseColor("#81dafc"), // pastel green
                      Color.parseColor(("#77dd77"))); // pastel blue
        BarData data = new BarData(set);

        barChart.getDescription().setEnabled(false);
        barChart.setData(data);

        barChart.invalidate(); // refresh
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
