package com.example.team31_personalbest_ms2v2;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import java.util.Date;

import androidx.annotation.RequiresApi;

import static android.support.constraint.Constraints.TAG;

/**
 * TimeService class is the Service for tracking time which runs in background
 */
public class TimeService extends Service {
    static boolean ifPassed = false;

    private String secondStr, minuteStr, hourStr, dayStr;

    private final IBinder iBinder = new LocalService();

    /**
     * This local service class in TimeService class
     */
    class LocalService extends Binder {
        public TimeService getService() {
            return TimeService.this;
        }
    }

    /**
     * MyThread class runs the main service
     */
    final class MyThread implements Runnable {
        int startID;

        public MyThread(int startID) {
            this.startID = startID;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void run() {
            synchronized (this) {
                while(true) {

                    sendBroadcast(new Intent(Intent.ACTION_EDIT));

                    Date date = new Date();

                    String second = "ss";
                    DateFormat secondFormat = new SimpleDateFormat(second);

                    String minute = "mm";
                    DateFormat minuteFormat = new SimpleDateFormat(minute);

                    String hour = "HH";
                    DateFormat hourFormat = new SimpleDateFormat(hour);

                    String day = "MMdd";
                    DateFormat dayFormat = new SimpleDateFormat(day);

                    // convert time to string
                    secondStr = secondFormat.format(date);
                    minuteStr = minuteFormat.format(date);
                    hourStr = hourFormat.format(date);
                    dayStr = dayFormat.format(date);

                    Log.i("timeThread: ",secondStr + " " + minuteStr + " " + hourStr + " " + dayStr);

                    if(hourStr.equals("12") && minuteStr.equals("0") && secondStr.equals("00")) {
                        setProgressNotificationFlag();
                    }

                    SharedPreferences sharedPref = getSharedPreferences("accomplishmentDate", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    String accomplishmentDate = sharedPref.getString("date", "");
                    editor.putString("currentDate", dayStr);
                    if (!dayStr.equals(accomplishmentDate)) {
                        editor.putBoolean("accomplishmentDisplayed", false);
                    }

                    editor.apply();
                    try {
                        wait(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * This method checks if time is passed 8pm, if so, stores today's steps
     * running code on a separate thread since DatRetriever has to get data from Historys API
     * and shouldn't stall the code that calls setProgressNotificationFlag
     */
    public void setProgressNotificationFlag() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DataRetriever dr = new DataRetriever(MainActivity.mainActivity);
                dr.setup();
                int previousDayStepCount = dr.retrieveYesterdaysSteps();
                int todayStepCount = getSharedPreferences("resetSteps", MODE_PRIVATE).getInt("steps", 0);

                Log.i("today day steps: ", String.valueOf(todayStepCount));
                Log.i("previous day steps: ", String.valueOf(previousDayStepCount));

                if(todayStepCount >= (2 * previousDayStepCount) && previousDayStepCount > 0) {
                    SharedPreferences sharePref = getSharedPreferences("progressNotification", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharePref.edit();
                    editor.putBoolean("makeProgress", true);
                    editor.apply();

                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(TimeService.this,"You made huge progress compared to yesterday",Toast.LENGTH_SHORT).show();
                            Log.i("progress toast showed", "");
                        }
                    });
                }

            }
        }).start();
    }

    /**
     * Override the onBind method
     * @param intent
     * @return
     */
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return iBinder;
    }

    /**
     * Override the onStartCommand method
     * @param intent
     * @param flags
     * @param startID
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {
        Thread thread = new Thread(new MyThread(startID));
        thread.start();
        return super.onStartCommand(intent, flags, startID);
    }

    /**
     * Override onDestroy method
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
