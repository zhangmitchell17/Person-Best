package com.example.team31_personalbest;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class WalkRunActivity extends AppCompatActivity {
    private boolean isCancelled = false;
    private Button btnStop;
    private TextView timeDisplay;
    private Timer t;
    private StepCounter sc;

    public static final String FITNESS_SERVICE_KEY = "FITNESS_SERVICE_KEY";
    private static final String TAG = "StepCountActivity";
    private FitnessService fitnessService;

    //private boolean isCancelled = false;
    //private Button btnStop;
    private TextView stepDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_walk_run);

//        String fitnessServiceKey = getIntent().getStringExtra(FITNESS_SERVICE_KEY);
//        fitnessService = FitnessServiceFactory.create(fitnessServiceKey, this);
//        fitnessService.setup();

        // Makes a timer, makes the async task for it, and begins it
        timeDisplay = findViewById(R.id.textViewTimer);
        t = new Timer(timeDisplay);
        t.execute();

//        // Makes a step counter(as an async task) and execute
//        stepDisplay = findViewById(R.id.textViewSteps);
//        final StepCounter counter = new StepCounter(stepDisplay, fitnessService);
//        counter.execute();

        // Returns back to Home Page after session finished
        btnStop = findViewById(R.id.buttonStop);
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                t.cancel();
//                counter.cancel();
                finish();
            }
        });


    }
}
