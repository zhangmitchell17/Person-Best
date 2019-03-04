package com.example.team31_personalbest_ms2;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashSet;

public class WalkRunActivity extends AppCompatActivity implements IStepActivity{
    private Button btnStop;
    private Button btnUpdate;
    private TextView timeDisplay;
    private Timer t;
    private SpeedUpdater s;

    public static final String FITNESS_SERVICE_KEY = "FITNESS_SERVICE_KEY";
    private static final String TAG = "WalkRunActivity";
    private FitnessService fitnessService;

    private TextView stepDisplay;
    private TextView speedDisplay;

    private boolean isRunning;
    private Thread thread;
    private int stepCnted;

    //private Steps steps;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isRunning = true;
        setContentView(R.layout.content_walk_run);

        stepDisplay = findViewById(R.id.textViewSteps);

        SharedPreferences sharedPref = getSharedPreferences("resetSteps", MODE_PRIVATE);
        stepCnted = sharedPref.getInt("steps", -1);

        SharedPreferences sharedPreferences = getSharedPreferences("walkRunStats", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("walkRunSteps", 0);
        editor.apply();

        String fitnessServiceKey = getIntent().getStringExtra(FITNESS_SERVICE_KEY);
        fitnessService = FitnessServiceFactory.create(fitnessServiceKey, this);
        fitnessService.setup();

        // Makes a timer, makes the async task for it, and begins it
        timeDisplay = findViewById(R.id.textViewTimer);
        t = new Timer(timeDisplay);
        t.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

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
                float mph = s.getMPH();

                t.cancel();
                s.cancel();
                isRunning = false;
                //thread.();

                // store steps, speed, seconds
                String step = stepDisplay.getText().toString();
                WalkRunStats stats = new WalkRunStats(String.valueOf(mph), step, String.valueOf(seconds));
                storeToSharePref(stats);
                finish();
            }
        });
    }

    public void storeToSharePref(WalkRunStats stats) {
        SharedPreferences sharePref = MainActivity.mainActivity.getSharedPreferences("WalkRunStats", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharePref.edit();
        HashSet<String> set = new HashSet<>();

        set.add("Time: " + stats.totalTime);
        set.add("MPH: " + stats.speed);
        set.add("Steps: " + stats.steps);
        set.add("dayOfWeek: " + stats.dayOfWeek);
        set.add("monthDayYear: " + stats.monthDayYear);

        editor.putStringSet(stats.monthDayYear, set);
        editor.apply();

        SharedPreferences sharedPreferences = MainActivity.mainActivity.getSharedPreferences("WalkRunStatsDate", MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putStringSet(stats.date, set);
        edit.apply();
    }

    public void setStepCount(long stepCount) {
        stepDisplay.setText(String.valueOf(stepCount - stepCnted));
        long stepsToDisplay = stepCount - stepCnted;
        SharedPreferences sharedPreferences = getSharedPreferences("walkRunStats", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("walkRunSteps", stepsToDisplay);
        editor.apply();
    }


}