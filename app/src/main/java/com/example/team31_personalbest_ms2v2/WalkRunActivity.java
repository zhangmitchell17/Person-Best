package com.example.team31_personalbest_ms2v2;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashSet;

public class WalkRunActivity extends AppCompatActivity implements IStepActivity{
    private Button btnStop;
    private Button btnUpdate;
    private TextView timeDisplay;
    private Timer t;
    private SpeedUpdater s;

    public static final String FITNESS_SERVICE_KEY = "FITNESS_SERVICE_KEY";
    private static final String TAG = "WalkRunActivity";
    private FitnessService fitnessService;

    private TextView stepDisplay;
    private TextView speedDisplay;

    private boolean isRunning;
    private Thread thread;
    private int stepCnted;

    private String currentUserEmail;
    private String currentUserName;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    User user;

    //private Steps steps;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isRunning = true;
        setContentView(R.layout.content_walk_run);

        stepDisplay = findViewById(R.id.textViewSteps);

        // User's current account
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            this.currentUserEmail = acct.getEmail();
            this.currentUserName = acct.getDisplayName();
        }

        // add current user to the users database
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();
        this.user = new User(currentUserName, currentUserEmail);
        addUser(user);

        SharedPreferences sharedPref = getSharedPreferences("resetSteps", MODE_PRIVATE);
        stepCnted = sharedPref.getInt("steps", -1);

        SharedPreferences sharedPreferences = getSharedPreferences("walkRunStats", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("walkRunSteps", 0);
        editor.apply();

        String fitnessServiceKey = getIntent().getStringExtra(FITNESS_SERVICE_KEY);
        fitnessService = FitnessServiceFactory.create(fitnessServiceKey, this);
        fitnessService.setup();

        // Makes a timer, makes the async task for it, and begins it
        String stride =getIntent().getStringExtra("stride");
        String strideLength = stride.substring(stride.indexOf(":") + 2);
        timeDisplay = findViewById(R.id.textViewTimer);
        t = new Timer(timeDisplay, fitnessService);
        t.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        speedDisplay = findViewById(R.id.textViewSpeed);
        System.out.println("strideLength is: " + strideLength);
        s = new SpeedUpdater(this, speedDisplay, t, Integer.parseInt(strideLength));
        s.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        // Returns back to Home Page after session finished
        btnStop = findViewById(R.id.buttonStop);
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                long seconds = t.getSeconds();
                float mph = s.getMPH();

                t.cancel();
                s.cancel();
                isRunning = false;

                // store steps, speed, seconds
                String step = stepDisplay.getText().toString();
                WalkRunStats stats = new WalkRunStats(String.valueOf(mph), step, String.valueOf(seconds));
                storeToSharePref(stats);

                db.collection("users").document(user.email).
                        collection("WalkRuns").
                        document("Walk at " + String.valueOf(System.currentTimeMillis())).
                        set(stats);

                finish();
            }
        });
    }

    public void addUser(User user) {
        db.collection("users").document(user.email).set(user);
    }

    public void storeToSharePref(WalkRunStats stats) {
        SharedPreferences sharePref = MainActivity.mainActivity.getSharedPreferences("WalkRunStats", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharePref.edit();
        HashSet<String> set = new HashSet<>();

        set.add("Time: " + stats.totalTime);
        set.add("MPH: " + stats.speed);
        set.add("Steps: " + stats.steps);
        set.add("dayOfWeek: " + stats.dayOfWeek);
        set.add("monthDayYear: " + stats.monthDayYear);

        editor.putStringSet(stats.monthDayYear, set);
        editor.apply();

        SharedPreferences sharedPreferences = MainActivity.mainActivity.getSharedPreferences("WalkRunStatsDate", MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putStringSet(stats.date, set);
        edit.apply();
    }

    public void setStepCount(long stepCount) {
        stepDisplay.setText(String.valueOf(stepCount - stepCnted));
        long stepsToDisplay = stepCount - stepCnted;
        SharedPreferences sharedPreferences = getSharedPreferences("walkRunStats", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("walkRunSteps", stepsToDisplay);
        editor.apply();
    }


}