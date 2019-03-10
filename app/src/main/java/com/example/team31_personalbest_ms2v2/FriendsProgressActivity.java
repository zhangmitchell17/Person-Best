package com.example.team31_personalbest_ms2v2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.BarChart;

import java.util.ArrayList;
import java.util.List;

public class FriendsProgressActivity extends AppCompatActivity {

    BarChart bc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_progress);

        // getting friend info to get data from cloud
        Bundle b = getIntent().getExtras();
        String value;
        if(b!=null) {
            value = b.getString("Email");
        }

        bc = findViewById(R.id.barChartFriends);

        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO get data from cloud via the 'value' variable
                List<Integer> unplannedSteps = new ArrayList<>();
                List<Integer> plannedSteps = new ArrayList<>();
                String[] labels = new String[unplannedSteps.size()];

                ProgressChart pc = new ProgressChart(bc, unplannedSteps, plannedSteps,
                        unplannedSteps.size(), labels);
            }
        }).start();



    }
}
