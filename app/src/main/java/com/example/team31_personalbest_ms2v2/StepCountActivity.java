package com.example.team31_personalbest_ms2v2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This is the StepCountActivity which counts the number of steps of the user
 */
public class StepCountActivity extends AppCompatActivity implements IStepActivity{

    public static final String FITNESS_SERVICE_KEY = "FITNESS_SERVICE_KEY";

    private static final String TAG = "StepCountActivity";

    //private TextView textSteps;
    private FitnessService fitnessService;

    private boolean isCancelled = false;
    private Button btnStop;

    private TextView stepDisplay;

    private TextView textSteps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_count);
        textSteps = findViewById(R.id.textSteps);

        //fitnessServiceKey gets into the fitness mode
        String fitnessServiceKey = getIntent().getStringExtra(FITNESS_SERVICE_KEY);
        fitnessService = FitnessServiceFactory.create(fitnessServiceKey, this);

        //This button update step counts
        Button btnUpdateSteps = findViewById(R.id.buttonUpdateSteps);
        btnUpdateSteps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fitnessService.updateStepCount();
            }
        });

        fitnessService.setup();
    }

    /**
     * Override the onActivityResult method from super class
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //If authentication was required during google fit setup,
        // this will be called after the user authenticates
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == fitnessService.getRequestCode()) {
                fitnessService.updateStepCount();

            }
        } else {
            Log.e(TAG, "ERROR, google fit result code: " + resultCode);
        }
    }

    /**
     * This method allows user to set step count manually
     * @param stepCount
     */
    public void setStepCount(long stepCount) {
        textSteps.setText(String.valueOf(stepCount));
        SharedPreferences sharedPref = getSharedPreferences("resetSteps", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("steps", (int)stepCount);
        editor.apply();
        Log.i("Steps: ",String.valueOf(editor.putInt("steps", (int)stepCount)));

        if(Integer.parseInt(textSteps.getText().toString()) == 1000) {
            showEncouragement();
        }

    }

    /**
     * This method shows the Encouragement messages
     */
    public void showEncouragement() {
        int steps = Integer.parseInt(textSteps.getText().toString());

        // use toast message to print the Encourage messages
        Context context = getApplicationContext();
        CharSequence text = "Good job! You're already at ";
        int duration = Toast.LENGTH_LONG;
        Log.i("Duration: ", String.valueOf(duration));

        text  = text + Double.toString(steps / (double)(100)) + "% of the daily recommended number of steps.";
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
