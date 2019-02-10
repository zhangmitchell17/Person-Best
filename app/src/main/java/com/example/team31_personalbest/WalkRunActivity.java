package com.example.team31_personalbest;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class WalkRunActivity extends AppCompatActivity {
    private boolean isCancelled = false;
    private Button btnStop;

    private TextView timeDisplay;
    private final int SECS_PER_HOUR = 3600;
    private final int SECS_PER_MIN = 60;
    private final int MS_PER_SEC = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_walk_run);

        Button startButton = findViewById(R.id.buttonStop);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /*
         * makes a timer, makes the async task for it, and begins it
         */
        timeDisplay = findViewById(R.id.textViewTimer);
        btnStop = findViewById(R.id.buttonStop);
        Clock clock = new Clock();
        clock.execute();

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCancelled = true;
                finish();
            }
        });
    }

    /**
     * Clock class is an AsyncTask that offloads a timer task onto a thread
     * to continuously update the timer text on WalkRunActivity
     */
    private class Clock extends AsyncTask<String, String, String> {
        private String resp;

        // Holds the number of seconds since beginning
        private long time = 0;

        private int hours;
        private int minutes;
        private int seconds;

        @Override
        protected String doInBackground(String... params) {
            // variable that holds the string for time
            String currTime;

            // loop that iterates each second to update time
            while (true) {
                if (isCancelled) break;
                updateTime();
                // format to pad 1 digits numbers with a 0 e.g. "01, 02"
                currTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                publishProgress(currTime);
                try {
                    Thread.sleep(MS_PER_SEC);
                    time++;
                } catch (Exception e) {
                    e.printStackTrace();
                    resp = e.getMessage();
                }
            }
            return resp;
        }

        @Override
        /**
         * present so that we can extend the classs
         */
        protected void onPreExecute() {
        }

        @Override
        /**
         * updating timeDisplay so that the text is updated on the activity
         */
        protected void onProgressUpdate(String ... s) {
            timeDisplay.setText(s[0]);
        }

        /**
         * incrementing minutes and hours if seconds or minutes overflows above 60
         */
        public void updateTime() {
            int temp = (int) time;
            hours = temp / SECS_PER_HOUR;
            temp = temp % SECS_PER_HOUR;
            minutes = temp / SECS_PER_MIN;
            temp = temp % SECS_PER_MIN;
            seconds = temp;
        }

        @Override
        /**
         * present so that we can extend the class
         */
        protected void onPostExecute(String result) {
            // doesn't need to to anything after finishing
        }
    }

}
