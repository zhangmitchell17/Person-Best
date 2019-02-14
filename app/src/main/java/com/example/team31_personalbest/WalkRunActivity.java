package com.example.team31_personalbest;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class WalkRunActivity extends AppCompatActivity {
    private boolean isCancelled = false;
    private Button btnStop;
    private TextView timeDisplay;
    private Timer t;
    //private StepCounter sc;


    public static final String FITNESS_SERVICE_KEY = "FITNESS_SERVICE_KEY";

    private static final String TAG = "StepCountActivity";

    //private Toast toast;
    private TextView stepDisplay;
    //private TextView mainStepDisplay;
    private FitnessService fitnessService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_walk_run);

        stepDisplay = findViewById(R.id.textViewSteps);
        stepDisplay.setText(String.valueOf(0));
        //mainStepDisplay = findViewById(R.id.step_text);

        String fitnessServiceKey = getIntent().getStringExtra(FITNESS_SERVICE_KEY);
        fitnessService = FitnessServiceFactory.create(fitnessServiceKey, this);

        Button btnUpdateSteps = findViewById(R.id.button_update_steps);
        btnUpdateSteps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fitnessService.updateStepCount();
            }
        });

        fitnessService.setup();

        // Makes a timer, makes the async task for it, and begins it
        timeDisplay = findViewById(R.id.textViewTimer);
        t = new Timer(timeDisplay);
        t.execute();



//        String fitnessServiceKey = getIntent().getStringExtra(FITNESS_SERVICE_KEY);
//        fitnessService = FitnessServiceFactory.create(fitnessServiceKey, new StepCountActivity());
//        fitnessService.setup();
//        StepCounter sc = new StepCounter(stepDisplay, fitnessService);
//        sc.execute();

        // Returns back to Home Page after session finished
        btnStop = findViewById(R.id.buttonStop);
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                t.cancel();
                stepDisplay.setText(String.valueOf(0));
//                counter.cancel();
                finish();
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//       If authentication was required during google fit setup, this will be called after the user authenticates
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == fitnessService.getRequestCode()) {
                fitnessService.updateStepCount();

            }
        } else {
            Log.e(TAG, "ERROR, google fit result code: " + resultCode);
        }
    }

    public void setStepCount(long stepCount) {
        //mainStepDisplay.setText(String.valueOf(stepCount));
        SharedPreferences sharePref = getSharedPreferences("totalSteps", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharePref.edit();
        editor.putLong("stepCount", stepCount);
        editor.apply();

        stepDisplay.setText(String.valueOf(stepCount));
    }
}


//        int i = 1000;
//        textSteps.setText(Integer.toString(i));
//        if(Integer.parseInt(textSteps.getText().toString()) == 1000) {
//            showEncouragement();
//        }


//    public void showEncouragement() {
//        int steps = Integer.parseInt(textSteps.getText().toString());
//
//        // use toast message
//        Context context = getApplicationContext();
//        CharSequence text = "Good job! You're already at ";
//        int duration = Toast.LENGTH_LONG;
//
//        text  = text + Double.toString(steps / (double)(100)) + "% of the daily recommended number of steps.";
//        Toast toast = Toast.makeText(context, text, duration);
//        toast.show();
//    }



