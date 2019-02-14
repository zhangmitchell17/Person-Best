package com.example.team31_personalbest;

import android.os.AsyncTask;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Clock class is an AsyncTask that offloads a timer task onto a thread
 * to continuously update the timer text on WalkRunActivity
 */
public class Timer extends AsyncTask<String, String, String> {
    private final int SECS_PER_HOUR = 3600;
    private final int SECS_PER_MIN = 60;
    private final int MS_PER_SEC = 1000;

    private String resp;
    private boolean isCancelled;
    // Holds the number of seconds since beginning
    private long time = 0;

    // Hours/Minutes/Seconds displayed in 00:00:00 format
    private int hours;
    private int minutes;
    private int seconds;
    private TextView timeDisplay;

    public Timer(TextView tv) {
        this.timeDisplay = tv;
    }

    public void cancel() {
        isCancelled = true;
    }


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
     * @return the number of total seconds
     */
    public long getSeconds() {
        return time;
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