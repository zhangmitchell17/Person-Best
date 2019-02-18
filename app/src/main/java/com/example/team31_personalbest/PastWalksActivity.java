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

    /**
     * Gets the day of the week index, with Sunday as 0 and Saturday as 6
     * @param day This is the string of the day of the week
     * @return the index of the day of the week, with Sunday as 0 and Saturday as 6
     * Returns -1 if string is not a day of the week
     */
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

    /**
     * Get a new Date for a certain number of days ago
     * @param numDaysAgo The number of days ago to get a Date object for
     * @return Date object on a certain number of days ago
     */
    private Date getPreviousDate (int numDaysAgo)
    {
        return new Date(System.currentTimeMillis() - (numDaysAgo * 24 * 60 * 60 * 1000));
    }

    /**
     * Get a String of Month (shortened) Day and Year in that order
     * @param date the String to get substrings of
     * @return a String of "Month(shortened) Day Year"
     */
    private String getMonthDayYear(String date)
    {
        int currentIndexOfEnd = date.indexOf(" ", date.indexOf(" ", date.indexOf(" ") + 1) +1);
        return date.substring(date.indexOf(" ") + 1, currentIndexOfEnd) + " " + date.substring(date.length() - 4);
    }

    /**
     * Begins on creation, set up the information from SharedPreferences and display it accordingly
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_walks);

        // Get information about the current date
        Date day = new Date();
        String date = day.toString();
        String currentDayOfWeek = date.substring(0, date.indexOf(" "));
        int currentDayIndex = indexOfWeek(currentDayOfWeek);
        //String currentMonthDayYear = getMonthDayYear(date);

        SharedPreferences sharedPreferences = getSharedPreferences("WalkRunStats", MODE_PRIVATE);
        Map<String, ?> allEntries = sharedPreferences.getAll();


        // Iterate through every SharedPreference key (date and time of run)
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
            // Only set information relevant from this week (Beginning on Sundays)
            if (thisWeek)
            {
                // Iterate through every information saved for this run
                for (String s : value) {
                    int y = -1;
                    // Get the index of either the time, steps, or mph information
                    for (int x = 0; x < TIME_STEPS_MPH.length; x++) {
                        if (s.substring(0, s.indexOf(":")).equals(TIME_STEPS_MPH[x])) {
                            y = x;
                        }
                    }
                    // If information gotten was time, steps, or mph, display it accordingly
                    if (y != -1) {
                        String valueToInput = s.substring(s.indexOf(":") + 2);
                        String identifier = "" + Constants.WEEKDAY_LOWER[indexWeek] + TIME_STEPS_MPH[y];
                        if (y == 0) {
                            int temp = (int) Integer.parseInt(valueToInput);
                            // Updates hours
                            int hours = temp / Constants.SECS_PER_HOUR;
                            temp = temp % Constants.SECS_PER_HOUR;
                            // Updates minutes
                            int minutes = temp / Constants.SECS_PER_MIN;
                            temp = temp % Constants.SECS_PER_MIN;
                            // Updates seconds
                            int seconds = temp;
                            valueToInput = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                        }
                        // Get the id of the TextView to be set
                        int resId = getResources().getIdentifier(identifier, "id", getPackageName());
                        TextView edit = findViewById(resId);
                        edit.setText(valueToInput);
                    }
                }
            }
        }


        // Set the rest of the days later on this week (should all be 0)
        for (int i = currentDayIndex + 1; i < Constants.WEEKDAY.length; i++)
        {
            for (int x = 0; x < TIME_STEPS_MPH.length; x++) {
                String identifier = "" + Constants.WEEKDAY_LOWER[i] + TIME_STEPS_MPH[x];
                int resId = getResources().getIdentifier(identifier, "id", getPackageName());
                TextView edit = findViewById(resId);
                edit.setText("0");
            }
        }

    }
}


