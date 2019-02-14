package com.example.team31_personalbest;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StepCounter extends AsyncTask<String, String, String> {
    private String resp;

    private boolean isCancelled;

    private int currStep;

    private Activity activity;

    public static final String FITNESS_SERVICE_KEY = "FITNESS_SERVICE_KEY";

    private static final String TAG = "StepCountActivity";

    //private Toast toast;
    private TextView stepDisplay;
    private FitnessService fitnessService;

    public StepCounter(TextView stepDisplay, FitnessService fs, Activity act) {
        stepDisplay = stepDisplay;
        fitnessService = fs;
        this.activity = act;
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
        /*
        fitnessService = FitnessServiceFactory.create(fitnessServiceKey, this);
        fitnessService.setup();
        */

    }

    @Override
    protected void onProgressUpdate(String... s) {
        stepDisplay.setText(s[0]);
    }

    @Override
    protected void onPostExecute(String result) {

    }
}
