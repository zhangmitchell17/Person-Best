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

public class InputHeightStepGoal extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_height);
        final EditText height = (EditText)findViewById(R.id.heightInput);
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
                try {
                    int newHeight = parseInt(height.getText().toString());
                    Toast.makeText(InputHeightStepGoal.this, "Your stride length is saved",
                            Toast.LENGTH_LONG).show();
                    if (sharePref.getString("height", "0").equals("") ||
                        newHeight <= 0) {
                        notification = "Please enter your height :)";

                        // clear current preferences
                        //editor.clear();
                        //editor.apply();
                    }
                    else {
                        notification = "Your stride length will be " +
                                (int)(0.413 * parseInt(height.getText().toString()))
                                + " inches";
                    }

                } catch (NumberFormatException e) {
                    notification = "Please enter your height :)";
                }
                displayFirstName.setText(notification);

            }
        });

        final EditText step = (EditText)findViewById(R.id.stepInput);
        Button updateStepButton = (Button)findViewById(R.id.updateStep);

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
                    if (sharePref.getString("step", "0").equals("") ||
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
        startActivity(intent);
    }
}
