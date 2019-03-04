package com.example.team31_personalbest_ms2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static java.lang.Integer.parseInt;

/**
 * This file is for you to input your step and height goals
 */
public class InputHeightStepGoal extends AppCompatActivity{


    /**
     * Begins on creation of the activity page
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_height);
        // Where the user can input their height
        final EditText height = (EditText)findViewById(R.id.heightInput);
        updateHeightHint();

        Button updateHeightButton = (Button)findViewById(R.id.updateHeight);

        // Where the user can input their step goal
        Button returnButton = (Button)findViewById(R.id.backToMain);

        // Perform the click activity on the update height button
        updateHeightButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Functionality for when the button is clicked
             * If the height is not 0, saves the height into sharedPreferences and
             * updates according TextViews
             * Displays a toast message
             */
            @Override
            public void onClick(View v) {
                // save user height to file savedHeight
                SharedPreferences sharedPref = getSharedPreferences
                                                ("savedHeight", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                // Only save the height if the user did not input 0
                if (!height.getText().toString().equals("0")) {
                    editor.putString("height", height.getText().toString());
                    editor.apply();
                }
                TextView displayFirstName = (TextView)findViewById(R.id.enteredHeight);
                String notification = "";

                // get user input height and calculate stride length
                try {
                    int newHeight = parseInt(height.getText().toString());
                    // Invalid height
                    if (sharedPref.getString("height", "").equals("") ||
                        newHeight <= 0) {
                        notification = "Please enter your height :)";
                        Toast.makeText(InputHeightStepGoal.this, "Your stride length is unchanged",
                                Toast.LENGTH_LONG).show();

                    }
                    // Valid height
                    else {
                        int stride = (int) (0.413 * parseInt(height.getText().toString()));
                        notification = "Your stride length will be " + stride + " inches";
                        sharedPref = getSharedPreferences("savedStride", MODE_PRIVATE);
                        editor = sharedPref.edit();
                        editor.putInt("stride", stride);
                        editor.apply();
                        Toast.makeText(InputHeightStepGoal.this, "Your stride length is saved",
                                Toast.LENGTH_LONG).show();
                    }

                } catch (NumberFormatException e) {
                    notification = "Please enter your height :)";
                }

                displayFirstName.setText(notification);
                Log.i("Notification: ",(String)displayFirstName.getText());
                updateHeightHint();
            }
        });

        final EditText goal = findViewById(R.id.stepInput);
        Button updateStepButton = findViewById(R.id.updateStep);

        updateGoalHint();

        // let user enter step goal
        updateStepButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Functionality for when the button is clicked
             * If the goal is not 0, saves the goal into sharedPreferences and
             * updates according TextViews
             * Displays a toast message
             */
            @Override
            public void onClick(View v) {
                // save user height to file savedHeight
                SharedPreferences sharePref = getSharedPreferences
                        ("savedStepGoal", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharePref.edit();
                if (!goal.getText().toString().equals("0")) {
                    editor.putString("step", goal.getText().toString());
                    editor.apply();
                }
                TextView displayFirstName = (TextView)findViewById(R.id.enteredStep);
                String notification = "";
                try {
                    int newGoal = Integer.parseInt(goal.getText().toString());
                    // Goal is 0 or invalid
                    if (sharePref.getString("step", "").equals("") ||
                            newGoal <= 0) {
                        notification = "Please enter a new step goal of at least 1 :)";
                        Toast.makeText(InputHeightStepGoal.this, "Your step goal is unchanged",
                                Toast.LENGTH_LONG).show();
                    }
                    // Valid goal
                    else {
                        notification = "Congrats! Your new step goal is " +
                                sharePref.getString("step", "1") + " steps";
                        Toast.makeText(InputHeightStepGoal.this, "Your step goal is saved",
                                Toast.LENGTH_LONG).show();
                    }

                } catch (NumberFormatException e) {
                    notification = "Please enter a new step goal of at least 2 :)";
                }
                displayFirstName.setText(notification);
                Log.i("Notification: ",String.valueOf(displayFirstName.getText()));
                updateGoalHint();

            }
        });

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchMainActivity();
            }
        });

    }

    /**
     * Method to return to the main activity page
     */
    public void launchMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    /**
     * Update the EditText height hint to show the current height
     */
    private void updateHeightHint() {
        EditText height = findViewById(R.id.heightInput);
        SharedPreferences sharedPreferences = getSharedPreferences("savedHeight", MODE_PRIVATE);
        String savedHeight = sharedPreferences.getString("height", "");
        // If a valid height was saved, then display the current height as the hint
        if (!savedHeight.equals(""))
        {
            height.setHint(savedHeight);
        }
        // Invalid height or no height saved
        else
        {
            height.setHint(getString(R.string.heightHint));
        }
        Log.i("Height: ", "The input height is "+height.getText());
    }


    /**
     * Update the EditText step goal hint to show the current step goal
     */
    private void updateGoalHint() {
        EditText step = (EditText)findViewById(R.id.stepInput);
        SharedPreferences sharedPreference = getSharedPreferences("savedStepGoal", MODE_PRIVATE);
        String savedStepGoal = sharedPreference.getString("step", "");
        // If a valid goal was saved, then display the current goal as the hint
        if (!savedStepGoal.equals(""))
        {
            step.setHint(savedStepGoal);
        }
        // Invalid goal or no goal saved
        else
        {
            step.setHint(getString(R.string.goalHint));
        }
        Log.i("Step Goal: ", "The step goal is "+step.getText());
    }
}
