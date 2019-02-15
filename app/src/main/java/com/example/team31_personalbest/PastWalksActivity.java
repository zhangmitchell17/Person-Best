package com.example.team31_personalbest;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PastWalksActivity extends AppCompatActivity {

    private static final String[] TIME_STEPS_MPH = {"Time", "Steps", "MPH"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_walks);

        Button updateWalks = findViewById(R.id.buttonUpdateWalks);
        updateWalks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int x = 0; x < Constants.WEEKDAY_LOWER.length; x++) {
                    SharedPreferences sharedPreferences = getSharedPreferences("savedWalks", MODE_PRIVATE);
                    for (int y = 0; y < TIME_STEPS_MPH.length; y++) {
                        String identifier = "" + Constants.WEEKDAY_LOWER[x] + TIME_STEPS_MPH[y];
                        int resID = getResources().getIdentifier(identifier, "id", getPackageName());
                        TextView edit = findViewById(resID);
                        if (y == 0 || y == 2) {
                            edit.setText("" + sharedPreferences.getInt(identifier, 0));
                        } else {
                            edit.setText(sharedPreferences.getString(identifier, "0"));
                        }
                    }
                }
            }
        });
    }
}


