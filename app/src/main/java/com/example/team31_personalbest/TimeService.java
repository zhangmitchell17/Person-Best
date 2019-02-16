package com.example.team31_personalbest;

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
import android.widget.Toast;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.RequiresApi;

public class TimeService extends Service {
    static boolean ifPassed = false;

    private String secondStr, minuteStr, hourStr, dayStr;

    private final IBinder iBinder = new LocalService();


    public TimeService() {
    }

    class LocalService extends Binder {
        public TimeService getService() {
            return TimeService.this;
        }
    }

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

                    System.out.println(secondStr + " " + minuteStr + " " + hourStr + " " + dayStr);

                    if(hourStr.equals("09") && minuteStr.equals("34") && secondStr.equals("20")) {
                        setProgressNotificationFlag();
                    }

                    SharedPreferences sharedPref = getSharedPreferences("accomplishmentDate", MODE_PRIVATE);
                    String accomplishmentDate = sharedPref.getString("date", "");
                    if (!dayStr.equals(accomplishmentDate)) {
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putBoolean("accomplishmentDisplayed", false);
                    }
                    try {
                        wait(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void setProgressNotificationFlag() {
        // check if time is passed 8pm, if so, store today's steps
        int todayStepCount = getSharedPreferences("resetSteps", MODE_PRIVATE).getInt("steps", 0);

        // TODO: put previous day's step count here
        int previousDayStepCount = 0;

        if(todayStepCount >= (2 * previousDayStepCount)) {
            SharedPreferences sharePref = getSharedPreferences("progressNotification", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharePref.edit();
            editor.putBoolean("makeProgress", true);
            editor.apply();

            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(TimeService.this,"You made huge progress compared to yesterday",Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return iBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {
        Thread thread = new Thread(new MyThread(startID));
        thread.start();
        return super.onStartCommand(intent, flags, startID);
    }


    public void onDestroy() {
        //Toast.makeText(TimeService.this, "Service Stopped", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    public String getSeconds()
    {
        return secondStr;
    }

    public String getMinutes() {
        return minuteStr;
    }

    public String getHours() {
        return hourStr;
    }

    public String getDays() {
        return dayStr;
    }

}
