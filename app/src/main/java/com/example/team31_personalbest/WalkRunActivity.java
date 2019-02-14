package com.example.team31_personalbest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class WalkRunActivity extends AppCompatActivity {
    private Button btnStop;
    private TextView timeDisplay;
    private Timer t;
    private StepCounter sc;

    public static final String FITNESS_SERVICE_KEY = "FITNESS_SERVICE_KEY";
    private static final String TAG = "StepCountActivity";
    private FitnessService fitnessService;

    //private boolean isCancelled = false;
    private TextView stepDisplay;
    private TextView speedDisplay;

    private static final long SECONDS_PER_HOUR = 3600;

    private static final long INCHES_PER_MILE = 63360;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_walk_run);



        // Makes a timer, makes the async task for it, and begins it
        timeDisplay = findViewById(R.id.textViewTimer);
        t = new Timer(timeDisplay);
        t.execute();



//        String fitnessServiceKey = getIntent().getStringExtra(FITNESS_SERVICE_KEY);
//        fitnessService = FitnessServiceFactory.create(fitnessServiceKey, new StepCountActivity());
//        fitnessService.setup();
//        StepCounter sc = new StepCounter(stepDisplay, fitnessService);
//        sc.execute();

        speedDisplay = findViewById(R.id.textViewSpeed);

/*
      PUT THIS IN WHERE YOU UPDATE SPEED/MPH
        SharedPreferences sharedPreferences = getSharedPreferences("savedStride", MODE_PRIVATE);
        int strideLength = sharedPreferences.getInt("savedStride", 0);
        long mph = (long) Math.round((t.getSeconds()/(SECONDS_PER_HOUR * INCHES_PER_MILE) *100.0)/100.0;
        if (Double.parseDouble(speedDisplay.getText()) != mph)
        {
            speedDisplay.setText("" + mph);
        }
*/
        SharedPreferences sharedPreferences = getSharedPreferences("user_name", MODE_PRIVATE);
        String firstName = sharedPreferences.getString("firstname","");

        // Returns back to Home Page after session finished
        btnStop = findViewById(R.id.buttonStop);
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long seconds = t.getSeconds();

                t.cancel();
                finish();
            }
        });


    }
}
