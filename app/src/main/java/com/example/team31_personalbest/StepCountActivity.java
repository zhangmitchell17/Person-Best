//package com.example.team31_personalbest;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
//import android.widget.Toast;
//
//public class StepCountActivity extends AppCompatActivity {
//
//    public static final String FITNESS_SERVICE_KEY = "FITNESS_SERVICE_KEY";
//
//    private static final String TAG = "StepCountActivity";
//
//    //private TextView textSteps;
//    private FitnessService fitnessService;
//
//    private boolean isCancelled = false;
//    private Button btnStop;
//
//    private TextView stepDisplay;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_step_count);
//        //textSteps = findViewById(R.id.textSteps);
//
//        String fitnessServiceKey = getIntent().getStringExtra(FITNESS_SERVICE_KEY);
//        fitnessService = FitnessServiceFactory.create(fitnessServiceKey, this);
//
////        Button btnUpdateSteps = findViewById(R.id.buttonUpdateSteps);
////        btnUpdateSteps.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                fitnessService.updateStepCount();
////            }
////        });
//
//        fitnessService.setup();
//
//        stepDisplay = findViewById(R.id.textViewSteps);
//        btnStop = findViewById(R.id.buttonStop);
//        StepCounter counter = new StepCounter();
//        counter.execute();
//
//        btnStop.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                isCancelled = true;
//                finish();
//            }
//        });
//
//    }
//
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
////       If authentication was required during google fit setup, this will be called after the user authenticates
//        if (resultCode == Activity.RESULT_OK) {
//            if (requestCode == fitnessService.getRequestCode()) {
//                fitnessService.updateStepCount();
//
//            }
//        } else {
//            Log.e(TAG, "ERROR, google fit result code: " + resultCode);
//        }
//    }
//
//    public void setStepCount(long stepCount) {
//        stepDisplay.setText(String.valueOf(stepCount));
//
////        int i = 1000;
////        stepDisplay.setText(Integer.toString(i));
////        if (Integer.parseInt(stepDisplay.getText().toString()) == 1000) {
////            showEncouragement();
////        }
//    }
//
////    public void showEncouragement() {
////        int steps = Integer.parseInt(textSteps.getText().toString());
////
////        // use toast message
////        Context context = getApplicationContext();
////        CharSequence text = "Good job! You're already at ";
////        int duration = Toast.LENGTH_LONG;
////
////        text = text + Double.toString(steps / (double) (100)) + "% of the daily recommended number of steps.";
////        Toast toast = Toast.makeText(context, text, duration);
////        toast.show();
////    }
//
//
//    private class StepCounter extends AsyncTask<String, String, String> {
//        private String resp;
//
//        private int currStep;
//
//        @Override
//        protected String doInBackground(String... params) {
//
//            while (true && !isCancelled) {
//                fitnessService.updateStepCount();
//                publishProgress(String.valueOf(currStep));
//                try {
//                    Thread.sleep(1000);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    resp = e.getMessage();
//                }
//            }
//            return resp;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            currStep = 0;
//        }
//
//        @Override
//        protected void onProgressUpdate(String... s) {
//            stepDisplay.setText(s[0]);
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//
//        }
//    }
//
//}
//
//
