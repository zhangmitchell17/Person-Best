package com.example.team31_personalbest_ms2;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
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

        SharedPreferences sharedPreferences = getSharedPreferences("WalkRunStatsDate", MODE_PRIVATE);
        Map<String, ?> allEntries = sharedPreferences.getAll();

        TableLayout table = findViewById(R.id.tableLayout);

        // Iterate through every SharedPreference key (date and time of run)
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            boolean thisWeek = false;
            String key = entry.getKey();
            int indexWeek = 0;
            for (int i = 0; i <= currentDayIndex; i++)
            {
                if (getMonthDayYear(key).equals(getMonthDayYear(getPreviousDate(i).toString()))) {
                    thisWeek = true;
                    indexWeek = currentDayIndex - i;
                }
            }
            HashSet<String> value = (HashSet<String>) entry.getValue();
            // Only set information relevant from this week (Beginning on Sundays)

            if (thisWeek)
            {
                TableRow row = new TableRow(this);
                TableLayout.LayoutParams tableRowParams=
                        new TableLayout.LayoutParams
                                (TableLayout.LayoutParams.WRAP_CONTENT,TableLayout.LayoutParams.WRAP_CONTENT);
                tableRowParams.setMargins(0, 0, 0, 0);
                row.setLayoutParams(tableRowParams);


                TextView dayOfWeek = new TextView(this);
                dayOfWeek.setText(Constants.WEEKDAY[indexWeek]);
                TableRow.LayoutParams prm = new TableRow.LayoutParams(300, TableRow.LayoutParams.WRAP_CONTENT);

                dayOfWeek.setTextSize(20);
                row.addView(dayOfWeek, prm);
                // Iterate through every information saved for this run
                for (int x = 0; x < TIME_STEPS_MPH.length; x++) {
                    for (String s : value) {
                        if (s.substring(0, s.indexOf(":")).equals(TIME_STEPS_MPH[x])) {
                            // If information gotten was time, steps, or mph, display it accordingly
                            String valueToInput = s.substring(s.indexOf(":") + 2);
                            if (x == 0) {
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
                            } else if(x == 2) {
                                float speed = Float.parseFloat(valueToInput);
                                valueToInput = String.format("%.2f", speed);
                            }
                            // Get the id of the TextView to be set

                            TextView edit = new TextView(this);
                            edit.setText(valueToInput);
                            TableRow.LayoutParams prms = new TableRow.LayoutParams(300, TableRow.LayoutParams.WRAP_CONTENT);

                            //edit.setLayoutParams(new FrameLayout.LayoutParams(100, FrameLayout.LayoutParams.WRAP_CONTENT));
                            edit.setTextSize(20);
                            row.addView(edit, prms);
                        }
                    }
                }
                table.addView(row);
            }

        }

    }
}


