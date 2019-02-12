package com.example.team31_personalbest;

import android.os.AsyncTask;
import android.widget.TextView;

public class StepCounter extends AsyncTask<String, String, String> {
    private String resp;
    private int currStep;
    private TextView stepDisplay;
    private boolean isCancelled;

    private FitnessService fitnessService;

    public StepCounter(TextView tv, FitnessService fs) {
        stepDisplay = tv;
        fitnessService = fs;
    }
    public void cancel() {
        isCancelled = true;
    }

    @Override
    protected String doInBackground(String... params) {

        while (true && !isCancelled) {
            fitnessService.updateStepCount();
            publishProgress(String.valueOf(currStep));
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
                resp = e.getMessage();
            }
        }
        return resp;
    }

    @Override
    protected void onPreExecute() {
        currStep = 0;
    }

    @Override
    protected void onProgressUpdate(String... s) {
        stepDisplay.setText(s[0]);
    }

    @Override
    protected void onPostExecute(String result) {

    }
}
