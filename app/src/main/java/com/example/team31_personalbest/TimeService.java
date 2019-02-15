package com.example.team31_personalbest;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.RequiresApi;

public class TimeService extends Service {
    static boolean ifPassed = false;

    private String secondStr, minuteStr, hourStr, dayStr;

    public TimeService() {
    }

    final class MyThread implements Runnable {
        int startID;

        public MyThread(int startID) {
            this.startID = startID;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void run() {
            SharedPreferences sharePref = getSharedPreferences("file", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharePref.edit();
            editor.putString("name", "John");
            editor.apply();

            synchronized (this) {
                while(true) {
                    Date date = new Date();

                    String second = "ss";
                    DateFormat secondFormat = new SimpleDateFormat(second);

                    String minute = "mm";
                    DateFormat minuteFormat = new SimpleDateFormat(minute);

                    String hour = "hh";
                    DateFormat hourFormat = new SimpleDateFormat(hour);

                    String day = "MMdd";
                    DateFormat dayFormat = new SimpleDateFormat(day);

                    // convert time to string
                    secondStr = secondFormat.format(date);
                    minuteStr = minuteFormat.format(date);
                    hourStr = hourFormat.format(date);
                    dayStr = dayFormat.format(date);

                    System.out.println(secondStr + " " + minuteStr + " " + hourStr + " " + dayStr);

                    try {
                        wait(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {
        Toast.makeText(TimeService.this, "Service Started", Toast.LENGTH_LONG).show();
        Thread thread = new Thread(new MyThread(startID));
        thread.start();
        return super.onStartCommand(intent, flags, startID);
    }

    public void onDestroy() {
        Toast.makeText(TimeService.this, "Service Stopped", Toast.LENGTH_SHORT).show();
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
