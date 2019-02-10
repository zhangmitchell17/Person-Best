package com.example.team31_personalbest;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Timer extends AppCompatActivity {
    private boolean isCancelled = false;
    private Button btnStop;

    private TextView timeDisplay;
    private final int SECS_PER_HOUR = 3600;
    private final int SECS_PER_MIN = 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_walk_run);
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




    private class Clock extends AsyncTask<String, String, String> {
        private String resp;

        // Holds the number of seconds since beginning
        private long time;

        private int hours;
        private int minutes;
        private int seconds;

        @Override
        protected String doInBackground(String... params) {
            String currTime;
            while (true) {
                if (isCancelled()) break;
                updateTime();
                currTime = hours + ":" + minutes + ":" + seconds;
                publishProgress(currTime);
                try {
                    int t = Integer.parseInt(params[0]) * 1000;
                    Thread.sleep(t);
                    time++;
                } catch (Exception e) {
                    e.printStackTrace();
                    resp = e.getMessage();
                }
            }
            return resp;
        }

        @Override
        protected void onPreExecute() {
            time = 0;
        }

        @Override
        protected void onProgressUpdate(String ... s) {
            timeDisplay.setText(s[0]);
        }

        public void updateTime() {
            int temp = (int) time;
            hours = (int) time / SECS_PER_HOUR;
            temp = (int) time % SECS_PER_HOUR;
            minutes = temp / SECS_PER_MIN;
            temp = temp % SECS_PER_MIN;
            seconds = temp;
        }

        @Override
        protected void onPostExecute(String result) {
            if (isCancelled) {
                String currTime = hours + ":" + minutes + ":" + seconds;
                String finalMessage = "Final Time: " + currTime;
                timeDisplay.setText(finalMessage);
            }
        }
    }
}