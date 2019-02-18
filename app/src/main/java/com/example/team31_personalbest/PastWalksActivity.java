package com.example.team31_personalbest;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;

public class PastWalksActivity extends AppCompatActivity {

    private static final String[] TIME_STEPS_MPH = {"Time", "Steps", "MPH"};

    private int indexOfWeek (String day)
    {
        for (int i = 0; i < Constants.WEEKDAY_LOWER_SHORTENED.length; i++)
        {
            if (Constants.WEEKDAY_LOWER_SHORTENED[i].equals(day))
            {
                return i;
            }
        }
        return -1;
    }

    private Date getPreviousDate (int numDaysAgo)
    {
        return new Date(System.currentTimeMillis() - (numDaysAgo * 24 * 60 * 60 * 1000));
    }

    private String getMonthDayYear(String date)
    {
        int currentIndexOfEnd = date.indexOf(" ", date.indexOf(" ", date.indexOf(" ") + 1) +1);
        return date.substring(date.indexOf(" ") + 1, currentIndexOfEnd) + " " + date.substring(date.length() - 4);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_walks);

        Button updateWalks = findViewById(R.id.buttonUpdateWalks);


        Date day = new Date();
        String date = day.toString();
        String currentDayOfWeek = date.substring(0, date.indexOf(" "));
        int currentDayIndex = indexOfWeek(currentDayOfWeek);
        //String currentMonthDayYear = getMonthDayYear(date);

        SharedPreferences sharedPreferences = getSharedPreferences("WalkRunStats", MODE_PRIVATE);
        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            boolean thisWeek = false;
            String key = entry.getKey();
            int indexWeek = 0;
            for (int i = 0; i <= currentDayIndex; i++)
            {
                if (key.equals(getMonthDayYear(getPreviousDate(i).toString())))
                {
                    thisWeek = true;
                    indexWeek = currentDayIndex - i;
                }
            }
            HashSet<String> value = (HashSet<String>) entry.getValue();
            if (thisWeek)
            {
                for (String s : value) {
                    int y = 0;
                    for (int x = 0; x < TIME_STEPS_MPH.length; x++) {
                        if(s.substring(0, s.indexOf(":")).equals(TIME_STEPS_MPH[x]))
                        {
                            y = x;
                        }
                    }
                    String identifier = "" + Constants.WEEKDAY_LOWER[indexWeek] + TIME_STEPS_MPH[y];
                    String valueToInput = s.substring(s.indexOf(":") + 1);
                    int resId = getResources().getIdentifier(identifier, "id", getPackageName());
                    TextView edit = findViewById(resId);
                    edit.setText(valueToInput);
                }
            }
        }
        /*

        for (int i = currentDayIndex + 1; i < Constants.WEEKDAY.length; i++)
        {
            for (int x = 0; x < TIME_STEPS_MPH.length; x++) {
                String identifier = "" + Constants.WEEKDAY[i] + TIME_STEPS_MPH[x];
                int resId = getResources().getIdentifier(identifier, "id", getPackageName());
                TextView edit = findViewById(resId);
                edit.setText("0");
            }
        }
        */


/*
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
        */
    }
}


