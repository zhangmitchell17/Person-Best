package com.example.team31_personalbest;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class WalkRunActivity extends AppCompatActivity implements IStepActivity{
    private Button btnStop;
    private Button btnUpdate;
    private TextView timeDisplay;
    private Timer t;
    private SpeedUpdater s;
    //private StepCounter sc;

    public static final String FITNESS_SERVICE_KEY = "FITNESS_SERVICE_KEY";
    private static final String TAG = "WalkRunActivity";
    private FitnessService fitnessService;

    private TextView stepDisplay;
    private TextView speedDisplay;

    private int stepCnted;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_walk_run);

        stepDisplay = findViewById(R.id.textViewSteps);

        SharedPreferences sharedPref = getSharedPreferences("resetSteps", MODE_PRIVATE);
        stepCnted = sharedPref.getInt("steps", -1);

        String fitnessServiceKey = getIntent().getStringExtra(FITNESS_SERVICE_KEY);
        fitnessService = FitnessServiceFactory.create(fitnessServiceKey, this);
        fitnessService.setup();

        // Makes a timer, makes the async task for it, and begins it
        timeDisplay = findViewById(R.id.textViewTimer);
        t = new Timer(timeDisplay);
        t.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);



//        String fitnessServiceKey = getIntent().getStringExtra(FITNESS_SERVICE_KEY);
//        fitnessService = FitnessServiceFactory.create(fitnessServiceKey, new WalkRunActivity());
//        fitnessService.setup();
//        //StepCounter sc = new StepCounter(stepDisplay, fitnessService);
//        //sc.execute();

        speedDisplay = findViewById(R.id.textViewSpeed);
        s = new SpeedUpdater(this, speedDisplay, t);
        s.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        btnUpdate = findViewById(R.id.button_update_steps);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fitnessService.updateStepCount();
            }
        });

        // Returns back to Home Page after session finished
        btnStop = findViewById(R.id.buttonStop);
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long seconds = t.getSeconds();
                String nameOfDay = "";
                SharedPreferences sharedPreferences = getSharedPreferences("savedWalks", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putLong(nameOfDay + "Time", seconds);
                editor.apply();

                float mph = s.getMPH();
                editor.putFloat(nameOfDay + "MPH", mph);
                editor.apply();

                //editor.putLong(nameOfDay + "Steps", steps);
                //editor.apply();

                t.cancel();
                s.cancel();

                finish();
            }
        });
    }

    public void setStepCount(long stepCount) {
        stepDisplay.setText(String.valueOf(stepCount - stepCnted));
    }


}