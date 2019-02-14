package com.example.team31_personalbest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class WalkRunActivity extends AppCompatActivity {
    private Button btnStop;
    private TextView timeDisplay;
    private Timer t;
    private SpeedUpdate s;
    private StepCounter sc;

    public static final String FITNESS_SERVICE_KEY = "FITNESS_SERVICE_KEY";
    private static final String TAG = "StepCountActivity";
    private FitnessService fitnessService;

    //private boolean isCancelled = false;
    private TextView stepDisplay;
    private TextView speedDisplay;

    private static final long SECONDS_PER_HOUR = 3600;
    private static final long MS_PER_SEC = 1000;

    private static final long INCHES_PER_MILE = 63360;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_walk_run);



        // Makes a timer, makes the async task for it, and begins it
        timeDisplay = findViewById(R.id.textViewTimer);
        t = new Timer(timeDisplay);
        t.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);



//        String fitnessServiceKey = getIntent().getStringExtra(FITNESS_SERVICE_KEY);
//        fitnessService = FitnessServiceFactory.create(fitnessServiceKey, new StepCountActivity());
//        fitnessService.setup();
//        StepCounter sc = new StepCounter(stepDisplay, fitnessService);
//        sc.execute();

        speedDisplay = findViewById(R.id.textViewSpeed);
        s = new SpeedUpdate(speedDisplay, t);
        s.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


        // Returns back to Home Page after session finished
        btnStop = findViewById(R.id.buttonStop);
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long seconds = t.getSeconds();
                String nameOfDay = "";
                SharedPreferences sharedPreferences = getSharedPreferences("savedData", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putLong(nameOfDay + "Time", seconds);
                editor.apply();

                float mph = s.getMPH();
                editor.putFloat(nameOfDay + "MPH", mph);
                editor.apply();

                //editor.putLong(nameOfDay + "Steps", steps);
                //editor.apply();




                t.cancel();
                s.cancel();

                finish();
            }
        });


    }


    public class SpeedUpdate extends AsyncTask<String, String, String>
    {
        private String resp;
        private TextView speed;
        private Timer t;
        private boolean isCancelled;
        private float mph;

        // Initialize values
        public SpeedUpdate (TextView text, Timer timer)
        {
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
                SharedPreferences sharedPreferences = getSharedPreferences("savedStride", MODE_PRIVATE);
                int strideLength = sharedPreferences.getInt("stride", 0);
                // TODO: NEED TO MULTIPLY THIS BY NUMBER OF STEPS TAKEN (replace the 10000)
                mph = (float) (Math.round((10000.0 * strideLength * SECONDS_PER_HOUR)/(t.getSeconds() * INCHES_PER_MILE) * 10.0)/10.0);
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
}
