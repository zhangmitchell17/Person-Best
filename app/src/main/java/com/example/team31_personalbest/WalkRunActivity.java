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
    // Milliseconds per second
    private final int MS_PER_SEC = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_walk_run);

        // Makes a timer, makes the async task for it, and begins it
        timeDisplay = findViewById(R.id.textViewTimer);
        Clock clock = new Clock();
        clock.execute();

        // Returns back to Home Page after session finished
        btnStop = findViewById(R.id.buttonStop);
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

        // Hours/Minutes/Seconds displayed in 00:00:00 format
        private int hours;
        private int minutes;
        private int seconds;

        /**
         * Updates the timer while session still occurring
         */
        @Override
        protected String doInBackground(String... params) {
            // Loop that iterates each second to update time
            while (true) {
                if (isCancelled) break;
                // Updates time display
                updateTime();
                publishProgress(getTime());
                // Waits for 1 second before each update
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

        /**
         * present so that we can extend the classs
         */
        @Override
        protected void onPreExecute() {
            // Doesn't need to to anything before : - )
        }

        /**
         * updating timeDisplay so that the text is updated on the activity
         */
        @Override
        protected void onProgressUpdate(String ... s) {
            timeDisplay.setText(s[0]);
        }

        /**
         * Format time to pad 1 digits numbers with a 0 e.g. "01, 02"
         * @return Current time formatted in 00:00:00
         */
        public String getTime() {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }

        /**
         * Incrementing seconds, minutes, and hours if seconds
         * or minutes overflows above 60
         */
        public void updateTime() {
            int temp = (int) time;
            // Updates hours
            hours = temp / SECS_PER_HOUR;
            temp = temp % SECS_PER_HOUR;
            // Updates minutes
            minutes = temp / SECS_PER_MIN;
            temp = temp % SECS_PER_MIN;
            // Updates seconds
            seconds = temp;
        }

        /**
         * present so that we can extend the class
         */
        @Override
        protected void onPostExecute(String result) {
            // Doesn't need to to anything after finishing : - )
        }
    }

}
