package com.example.team31_personalbest_ms2;

import android.os.AsyncTask;
import android.widget.TextView;

/**
 * Implement the AsyncTask for performing multitasks at the same time
 */
public class Steps extends AsyncTask<String, String, String> {

    private TextView stepDisplay;
    private FitnessService fitnessService;

    private String resp;
    private boolean isCancelled = false;

    public Steps(TextView tv, FitnessService fs) {
        this.stepDisplay = tv;
        this.fitnessService = fs;
    }

    /**
     * This method is for the task to do in the background
     */
    @Override
    protected String doInBackground(String... params) {
        // Loop that iterates each second to update time
        while (true) {
            if (isCancelled) break;
            // Updates time display
            fitnessService.updateStepCount();
            publishProgress();
            // Waits for 1 second before each update
            try {
                Thread.sleep(Constants.MS_PER_SEC);
            } catch (Exception e) {
                e.printStackTrace();
                resp = e.getMessage();
            }
        }
        return resp;
    }

    /**
     * This method presents so that we can extend the class
     */
    @Override
    protected void onPreExecute() {
        // Doesn't need to to anything before
    }

    /**
     * Override the onProgressUpdate method
     */
    @Override
    protected void onProgressUpdate(String ... s) {
        //stepDisplay.setText(s[0]);
    }


    /**
     * onPostExecute presents so that we can extend the class
     */
    @Override
    protected void onPostExecute(String result) {
        // Doesn't need to to anything after finishing : - )
    }
}


