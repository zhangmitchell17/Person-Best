package com.example.team31_personalbest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.TextView;

import static android.content.Context.MODE_PRIVATE;

public class SpeedUpdater extends AsyncTask<String, String, String>{
    private String resp;
    private TextView speed;
    private Context c;
    private Timer t;
    private boolean isCancelled;
    private float mph;

    private static final long SECONDS_PER_HOUR = 3600;
    private static final long MS_PER_SEC = 1000;

    private static final long INCHES_PER_MILE = 63360;

    // Initialize values
    public SpeedUpdater (Context c, TextView text, Timer timer)
    {
        this.c = c;
        this.speed = text;
        this.t = timer;
    }

    /**
     * Continuously run in the background, calling publishProgress to update MPH if the
     * value is different than what is already there
     * @return error message
     */
    @Override
    protected String doInBackground(String... params) {
        isCancelled = false;
        // Loop that iterates each second to update time
        // Only loop while the AsyncTask should be running
        while (true) {
            if(isCancelled) {
                break;
            }
            SharedPreferences sharedPreferences = c.getSharedPreferences("savedStride", MODE_PRIVATE);
            int strideLength = sharedPreferences.getInt("stride", 0);
            // TODO: NEED TO MULTIPLY THIS BY NUMBER OF STEPS TAKEN (replace the 10000)
            SharedPreferences sharedPref = c.getSharedPreferences("walkRunStats", MODE_PRIVATE);
            long numSteps = sharedPref.getLong("walkRunSteps", 0);
            if (numSteps != 0) {
                mph = (float) (Math.round((numSteps * strideLength * SECONDS_PER_HOUR) / (t.getSeconds() * INCHES_PER_MILE) * 10.0) / 10.0);
            }
            else
            {
                mph = 0;
            }
            if (Double.parseDouble(speed.getText().toString()) != mph)
            {
                publishProgress(("" + mph));
            }


            // Waits for 1 second before each update

            try {
                Thread.sleep(MS_PER_SEC);
            } catch (Exception e) {
                e.printStackTrace();
                resp = e.getMessage();
            }
        }
        return resp;
    }

    @Override
    protected void onPreExecute() {
        isCancelled = false;
    }

    @Override
    protected void onPostExecute(String result) {
    }

    //Update the speedDisplay TextView
    @Override
    protected void onProgressUpdate(String... text) {
        speed.setText(text[0]);
    }

    public void cancel() {
        isCancelled = true;
    }

    public float getMPH() {
        return mph;
    }

}