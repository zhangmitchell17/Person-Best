package com.example.team31_personalbest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.format.Time;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;

// used to create timer and reset step at beginning of day
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // inner class used to reset time everyday
    class ResetSteps {
        Timer timer;

        public ResetSteps() {
            // set time to be midnight
            Calendar midnight = Calendar.getInstance();
            midnight.set(Calendar.HOUR_OF_DAY, 0);
            midnight.set(Calendar.MINUTE, 0);
            midnight.set(Calendar.SECOND, 0);

            timer = new Timer();
            timer.schedule(new ResetTask(), midnight.getTime(),
                    TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS));
        }

        class ResetTask extends TimerTask {
            public void run() {
                SharedPreferences sharePref = getSharedPreferences("resetSteps", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharePref.edit();

                // today's date
                Handler mHandler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message message) {
                        // This is where you do your work in the UI thread.
                        // Your worker tells you in the message what to do.
                        Toast.makeText(MainActivity.this, "Your step is reset", Toast.LENGTH_LONG).show();
                    }
                };

                String date = new SimpleDateFormat("MM-dd-yyyy").format(Calendar.getInstance().getTime());

                // store date in sharedPreferenceFile
                editor.remove("date");
                editor.putString("date", date);
                editor.apply();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Button startButton = findViewById(R.id.buttonStart);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchWalkRunActivity();
            }
        });

        // Use time thread to reset step when app is running
        ResetSteps resetSteps = new ResetSteps();
    }

    // everytime user back to main page, check for step reset
    public void onResume() {
        super.onResume();
        // Hongyu: when app is launched check if date changed, if date is changed reset steps
        SharedPreferences sharePref = getSharedPreferences("resetSteps", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharePref.edit();

        // today's date
        String date = new SimpleDateFormat("MM-dd-yyyy").format(Calendar.getInstance().getTime());

        // if date is not equal, which means we should reset date
        if(!sharePref.getString("date", "").equals(date)) {
            Toast.makeText(MainActivity.this, "Your step is reset", Toast.LENGTH_LONG).show();
        }

        // store date in sharedPreferenceFile
        editor.clear();
        editor.putString("date", date);
        editor.apply();
    }

    public void launchInputHeightActivity() {
        Intent intent = new Intent(this, InputHeight.class);
        //intent.putExtra(WalkRunActivity.FITNESS_SERVICE_KEY, fitnessServiceKey);
        startActivity(intent);
    }

    public void launchWalkRunActivity() {
        Intent intent = new Intent(this, WalkRunActivity.class);
        //intent.putExtra(WalkRunActivity.FITNESS_SERVICE_KEY, fitnessServiceKey);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_stride) {
            // Go to stride settings
            launchInputHeightActivity();
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
