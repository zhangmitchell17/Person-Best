//package com.example.team31_personalbest;
//
//import android.app.Activity;
//import android.os.AsyncTask;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
//
//public class StepCounter extends AsyncTask<String, String, String> {
//    private String resp;
//
//    private boolean isCancelled;
//
//    private int currStep;
//
//    private Activity activity;
//
//    public static final String FITNESS_SERVICE_KEY = "FITNESS_SERVICE_KEY";
//
//    private static final String TAG = "StepCountActivity";
//
//    //private Toast toast;
//    private TextView textSteps;
//    private FitnessService fitnessService;
//
//    public StepCounter(TextView tv, FitnessService fs, Activity act) {
//        textSteps = tv;
//        fitnessService = fs;
//        this.activity = act;
//    }
//
//    public void cancel() {
//        isCancelled = true;
//    }
//
//    @Override
//    protected String doInBackground(String... params) {
//
//        while (true && !isCancelled) {
//            fitnessService.updateStepCount();
//            publishProgress(String.valueOf(currStep));
//            try {
//                Thread.sleep(1000);
//            } catch (Exception e) {
//                e.printStackTrace();
//                resp = e.getMessage();
//            }
//        }
//        return resp;
//    }
//
//    @Override
//    protected void onPreExecute() {
//        String fitnessServiceKey = getIntent().getStringExtra(FITNESS_SERVICE_KEY);
//        fitnessService = FitnessServiceFactory.create(fitnessServiceKey, this);
//
//        Button btnUpdateSteps = findViewById(R.id.buttonUpdateSteps);
//        btnUpdateSteps.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                fitnessService.updateStepCount();
//            }
//        });
//
//        fitnessService.setup();
//
//    }
//
//    @Override
//    protected void onProgressUpdate(String... s) {
//        stepDisplay.setText(s[0]);
//    }
//
//    @Override
//    protected void onPostExecute(String result) {
//
//    }
//}
