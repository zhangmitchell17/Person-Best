package com.example.team31_personalbest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static java.lang.Integer.parseInt;

public class InputHeightStepGoal extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_height);
        final EditText height = (EditText)findViewById(R.id.heightInput);
        updateHeightHint();

        Button updateHeightButton = (Button)findViewById(R.id.updateHeight);

        final Button backButton = (Button)findViewById(R.id.backToMain);
        backButton.setVisibility(View.VISIBLE);




        updateHeightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // save user height to file savedHeight
                SharedPreferences sharePref = getSharedPreferences
                                                ("savedHeight", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharePref.edit();
                editor.putString("height", height.getText().toString());
                editor.apply();
                TextView displayFirstName = (TextView)findViewById(R.id.enteredHeight);
                String notification = "";

                // get user input height and calculate stride length
                try {
                    int newHeight = parseInt(height.getText().toString());
                    Toast.makeText(InputHeightStepGoal.this, "Your stride length is saved",
                            Toast.LENGTH_LONG).show();
                    if (sharePref.getString("height", "").equals("") ||
                        newHeight <= 0) {
                        notification = "Please enter your height :)";

                        // clear current preferences
                        //editor.clear();
                        //editor.apply();
                    }
                    else {
                        int stride = (int) (0.413 * parseInt(height.getText().toString()));
                        notification = "Your stride length will be " + stride + " inches";
                        sharePref = getSharedPreferences("savedStride", MODE_PRIVATE);
                        editor = sharePref.edit();
                        editor.putInt("stride", stride);
                        editor.apply();
                    }

                } catch (NumberFormatException e) {
                    notification = "Please enter your height :)";
                }

                displayFirstName.setText(notification);
                updateHeightHint();
            }
        });

        final EditText step = (EditText)findViewById(R.id.stepInput);
        Button updateStepButton = (Button)findViewById(R.id.updateStep);

        updateGoalHint();

        // let user enter step goal
        updateStepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // save user height to file savedHeight
                SharedPreferences sharePref = getSharedPreferences
                        ("savedStepGoal", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharePref.edit();
                editor.putString("step", step.getText().toString());
                editor.apply();
                TextView displayFirstName = (TextView)findViewById(R.id.enteredStep);
                String notification = "";
                try {
                    int newGoal = Integer.parseInt(step.getText().toString());
                    Toast.makeText(InputHeightStepGoal.this, "Your step goal is saved",
                            Toast.LENGTH_LONG).show();
                    if (sharePref.getString("step", "").equals("") ||
                            newGoal <= 1) {
                        notification = "Please enter a new step goal of at least 2 :)";

                        // clear current preferences
                        //editor.clear();
                        //editor.apply();
                    }
                    else {
                        notification = "Congrats! Your new step goal is " +
                                sharePref.getString("step", "0") + " steps";
                    }

                } catch (NumberFormatException e) {
                    notification = "Please enter a new step goal of at least 2 :)";
                }
                displayFirstName.setText(notification);
                updateGoalHint();

            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchActivity();
            }
        });


    }

    public void launchActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    /**
     * Update the EditText height hint to show the current height
     */
    private void updateHeightHint() {
        EditText height = (EditText)findViewById(R.id.heightInput);
        SharedPreferences sharedPreferences = getSharedPreferences("savedHeight", MODE_PRIVATE);
        String savedHeight = sharedPreferences.getString("height", "");
        if (!savedHeight.equals(""))
        {
            height.setHint(savedHeight);
        }
        else
        {
            height.setHint(getString(R.string.heightHint));
        }
    }


    /**
     * Update the EditText step goal hint to show the current step goal
     */
    private void updateGoalHint() {
        EditText step = (EditText)findViewById(R.id.stepInput);

        SharedPreferences sharedPreference = getSharedPreferences("savedStepGoal", MODE_PRIVATE);
        String savedStepGoal = sharedPreference.getString("step", "");
        if (!savedStepGoal.equals(""))
        {
            step.setHint(savedStepGoal);
        }
        else
        {
            step.setHint(getString(R.string.stepHint));
        }
    }
}
