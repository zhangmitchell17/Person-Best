//package com.example.team31_personalbest;
//
//import android.app.IntentService;
//import android.app.Service;
//import android.content.Intent;
//import android.os.AsyncTask;
//import android.os.IBinder;
//import android.widget.TextView;
//import android.widget.Toast;
//
//public class Steps extends AsyncTask<String, String, String> {
//    private final int MS_PER_SEC = 1000;
//
//    private TextView stepDisplay;
//    private FitnessService fitnessService;
//
//    private String resp;
//    private boolean isCancelled = false;
//
//    public Steps(TextView tv, FitnessService fs) {
//        this.stepDisplay = tv;
//        this.fitnessService = fs;
//    }
//
//    //public void cancel() {
//        //isCancelled = true;
//    //}
//
//
//    /**
//     *
//     */
//    @Override
//    protected String doInBackground(String... params) {
//        // Loop that iterates each second to update time
//        while (true) {
//            if (isCancelled) break;
//            // Updates time display
//            fitnessService.updateStepCount();
//            publishProgress();
//            // Waits for 1 second before each update
//            try {
//                Thread.sleep(MS_PER_SEC);
//            } catch (Exception e) {
//                e.printStackTrace();
//                resp = e.getMessage();
//            }
//        }
//        return resp;
//    }
//
//    /**
//     * present so that we can extend the class
//     */
//    @Override
//    protected void onPreExecute() {
//        // Doesn't need to to anything before : - )
//    }
//
//    /**
//     *
//     */
//    @Override
//    protected void onProgressUpdate(String ... s) {
//        //stepDisplay.setText(s[0]);
//    }
//
//
//    /**
//     * present so that we can extend the class
//     */
//    @Override
//    protected void onPostExecute(String result) {
//        // Doesn't need to to anything after finishing : - )
//    }
//}
//
//
//import android.app.Service;
//        import android.content.Intent;
//        import android.os.IBinder;
//        import android.widget.Toast;
//
//public class DemoService extends IntentService {
//    public DemoService() { super("worker"); }
//
//    final class MyThread implements Runnable {
//        int startId;
//        public MyThread(int startId) {
//            this.startId = startId;
//        }
//
//        @Override
//        public void run(){
//            synchronized (this) {
//                try {
//                    wait(15000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                stopSelf();
//            }
//
//        }
//    }
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        Toast.makeText(DemoService.this, "Service Started", Toast.LENGTH_SHORT).show();
//        Thread thread = new Thread(new MyThread(startId));
//        thread.start();
//        return super.onStartCommand(intent, flags, startId);
//    }
//
//    public void onDestroy() {
//        Toast.makeText(DemoService.this, "Service Stopped", Toast.LENGTH_SHORT).show();
//        super.onDestroy();
//    }
//}
