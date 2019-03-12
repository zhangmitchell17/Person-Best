package com.example.team31_personalbest_ms2v2;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.Subject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static java.lang.Integer.parseInt;

// used to create timer and reset step at beginning of day
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
                   IStepActivity,
                   GoogleApiClient.ConnectionCallbacks,
                   GoogleApiClient.OnConnectionFailedListener {

    public static Activity mainActivity;

    private static final String TAG = "SignIn";

    private final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = System.identityHashCode(this) & 0xFFFF;

    private TextView stepDisplay;
    public static boolean loggedIn = false;

    private FitnessService fitnessService;
    private String fitnessServiceKey = "GOOGLE_FIT";

    private boolean goalAchievedDisplayed;

    private TimeService timeService;
    //public Steps steps;
    private boolean isBound;
    public DataUpdateReceiver dataUpdateReceiver;

    public String currentUserEmail;
    public String currentUserName;


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            TimeService.LocalService localService = (TimeService.LocalService) service;
            timeService = localService.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    // observer of time thread
    private class DataUpdateReceiver extends BroadcastReceiver {

        FitnessService fitnessService;

        DataUpdateReceiver(FitnessService fitnessService) {
            this.fitnessService = fitnessService;
        }

        /**
         * When receive boradcast from timer, update steps and send it to cloud
         * @param context
         * @param intent
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction() == Intent.ACTION_EDIT) {
                System.out.println("update steps");
                fitnessService.updateStepCount();
                sendStepsToCloud();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = this;
        if (!loggedIn) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail().build();

            // Build a GoogleSignInClient with the options specified by gso.
            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, AppCompatActivity.RESULT_OK);
        }

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Log.i(TAG, "toolbar: " + toolbar.toString());

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // setting stepDisplay
        stepDisplay = findViewById(R.id.textViewStepMain);
        Log.i(TAG, "stepDisplay: " + stepDisplay.toString());

        Button startButton = findViewById(R.id.buttonStart);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSteps();
                launchWalkRunActivity();
            }
        });

        // store user info
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            this.currentUserEmail = acct.getEmail();
            this.currentUserName = acct.getDisplayName();
        }

        FitnessServiceFactory.put(fitnessServiceKey, new FitnessServiceFactory.BluePrint() {

            @Override
            public FitnessService create(IStepActivity stepActivity) {
                return new GoogleFitAdapter(stepActivity);
            }
        });

        // start real time step count
        fitnessService = FitnessServiceFactory.create(fitnessServiceKey, this);
        fitnessService.setup();
        Log.i(TAG, "fitness Service: " + fitnessService.toString());

        // update step real time
        dataUpdateReceiver = new DataUpdateReceiver(this.fitnessService);
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_EDIT);
        registerReceiver(dataUpdateReceiver, intentFilter);

        // Bind time service to main activity
        Intent intent = new Intent(MainActivity.this, TimeService.class);

        Log.i(TAG, "Time Service: " + intent.toString());
        startService(intent);
    }

    /**
     * Send current user data to cloud
     */
    public void sendStepsToCloud() {
        TextView view = findViewById(R.id.textViewStepMain);
        String steps = view.getText().toString();
        Log.i("Cloud steps: ", ("Steps to the cloud: " + steps));

        FirebaseApp.initializeApp(this);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, String> dailyStepCnt = new HashMap();
        dailyStepCnt.put("steps", steps);
        String date = new SimpleDateFormat("MM-dd-yyyy").
                format(Calendar.getInstance().getTime());


        db.collection("users").document(this.currentUserEmail).
                collection("steps").document(date).set(dailyStepCnt);
    }

    /**
     * This method launch the StepCountActivity
     */
    public void launchStepCountActivity() {
        Intent intent = new Intent(this, StepCountActivity.class);
        intent.putExtra(StepCountActivity.FITNESS_SERVICE_KEY, fitnessServiceKey);
        startActivity(intent);
    }

    /**
     * onStart() method sets the initial behavior
     */
    public void onStart() {
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
    }

    /**
     * onResume() method check the dates, print messages, and update steps
     */
    public void onResume() {
        super.onResume();

        //When app is launched check if date changed, if date is changed reset steps
        SharedPreferences sharePref = getSharedPreferences("resetSteps", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharePref.edit();

        // today's date
        String date = new SimpleDateFormat("MM-dd-yyyy").format(Calendar.getInstance().getTime());

        // if date is not equal, which means we should reset date
        if(!sharePref.getString("date", "").equals(date)) {
            Toast.makeText(MainActivity.this, "Your step is reset", Toast.LENGTH_LONG).show();
            editor.putInt("steps", 0);
        }

        // store date in sharedPreferenceFile
        editor.remove("date");
        editor.putString("date", date);
        editor.apply();

        // update step count on screen
        updateStepCountAndStride();

        updateSteps();

    }

    /**
     * updateStepCountAndStride() method // stores current height and
     * stride length for textview in main page to use
     */
    public void updateStepCountAndStride() {

        SharedPreferences sharePref = getSharedPreferences("savedStepGoal", MODE_PRIVATE);
        String currentSteps = sharePref.getString("step", "1000");

        SharedPreferences sharedPreferences = getSharedPreferences("savedStride", MODE_PRIVATE);
        int currentStrideLength = sharedPreferences.getInt("stride", 0);

        // Update the strideLength and stepCount with output messages
        TextView strideLength = (TextView)findViewById(R.id.stride_length);
        strideLength.setText("Your stride length is: " + currentStrideLength);
        TextView stepCount = (TextView)findViewById(R.id.step_count);
        stepCount.setText("Your step goal is: " + currentSteps);

        // log message to see of text are correct
        Log.i("Step text: ", "Step goal text is: " + stepCount.getText().toString());
        Log.i("Stride length: ", "Stride length text is: " + strideLength.getText().toString());
    }

    /**
     * setStepCount() method saves the number of steps in sharedPreferences
     * and changes the TextView appropriately
     * @param stepCount The number of steps to update with
     */
    public void setStepCount(long stepCount) {
        SharedPreferences sharedPref = getSharedPreferences("resetSteps", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("steps", (int)stepCount);
        editor.apply();
        TextView totalSteps = (TextView) findViewById(R.id.textViewStepMain);
        totalSteps.setText(String.valueOf(stepCount));
        goalAchievement(stepCount);
        Log.i(TAG, "currentTotalSteps: " + totalSteps.getText().toString());
    }

    // updateSteps() updates the step counts
    public void updateSteps() {
        fitnessService.updateStepCount();
    }

    /**
     * launchInputHeightStepGoalActivity() launches the height and step goal input activity
     */
    public void launchInputHeightStepGoalActivity() {
        Intent intent = new Intent(this, InputHeightStepGoal.class);
        startActivity(intent);
    }

    /**
     * launchWalkRunActivity() launches the WalkRunActivity
     */
    public void launchWalkRunActivity() {
        Intent intent = new Intent(this, WalkRunActivity.class);
        intent.putExtra(WalkRunActivity.FITNESS_SERVICE_KEY, fitnessServiceKey);
        startActivity(intent);
    }

    /**
     * launchProgressActivity launches the Progress Activity
     */
    public void launchProgressActivity() {
        Intent intent = new Intent(this, ProgressActivity.class);
        startActivity(intent);
    }

    /**
     * launchPastWalksActivity Launches the activity showing the past week's walks/runs
     */
    public void launchPastWalksActivity() {
        Intent intent = new Intent(this, PastWalksActivity.class);
        startActivity(intent);
    }

    /**
     * launchPastWalksActivity Launches the activity showing the past week's walks/runs
     */
    public void launchFriendsListActivity() {
        Intent intent = new Intent(this, friendsListActivity.class);
        startActivity(intent);
    }

    /**
     * Override the onBackPressed method from super class
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Override the onCreateOptionsMenu method from super class
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Override the onOptionsItemSelected method from super class
     * @param item
     * @return
     */
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

    /**
     * Override the onNavigationItemSelected method
     * specify the function of each buttons
     * @param item
     * @return
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_stride) {
            // Go to stride settings
            launchInputHeightStepGoalActivity();
        } else if(id == R.id.nav_progress) {
            launchProgressActivity();
        } else if (id == R.id.nav_pastWalks) {
            launchPastWalksActivity();

        } else if (id == R.id.nav_slideshow) {
            launchFriendsListActivity();

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Override the onActivityResult method from super class
     * handle the sign in result of the task
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //System.out.println("onActivityResult called");

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == AppCompatActivity.RESULT_OK) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    /**
     * This method updates user interfaces and necessary information
     * after each time the user signed in
     * @param completedTask
     */
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        //System.out.println("handleSignInResult called");

        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    /**
     * This method update the user interface with messages to indicate if you login successful
     * @param account
     */
    public void updateUI(GoogleSignInAccount account) {
        if (account != null) {
            loggedIn = true;
            Toast.makeText(getApplicationContext(), "Login Successful!", Toast.LENGTH_LONG);
        } else {
            Log.w(TAG, "You need to log in again.");
            Toast.makeText(getApplicationContext(), "You need to log in again.", Toast.LENGTH_LONG);
        }
    }

    public void setFitnessServiceKey(String fitnessServiceKey) {
        this.fitnessServiceKey = fitnessServiceKey;
    }


    /**
     * This method displays the step goal achievement notification and
     * asks if the user wants to set a new step goal
     * @param stepCount The number of current steps to compare the step goal to
     */
    public void goalAchievement(long stepCount) {
        SharedPreferences sharedPreferences = getSharedPreferences("savedStepGoal", MODE_PRIVATE);
        int stepGoal = parseInt(sharedPreferences.getString("step", "0"));

        SharedPreferences sharedPref = getSharedPreferences("accomplishmentDate", MODE_PRIVATE);
        /*
        // Check if an accomplishment notification has been displayed today yet
        String date = sharedPref.getString("date", "");
        if (!timeService.getDays().equals(date)) {
            goalAchievedDisplayed = false;
        }
        */
        goalAchievedDisplayed = sharedPref.getBoolean("accomplishmentDisplayed", false);
        // Only display it if the step count is greater than the step goal and the notification has not been displayed yet
        if (stepCount > stepGoal && !goalAchievedDisplayed) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Achievement Notification");
            final int newStepGoal = (stepGoal * 1.10 > stepGoal + 500) ? stepGoal + 500 :
                                                                         (int) (stepGoal * 1.10);
            builder.setMessage("Good Job! You have achieved your step goal. Would you like accept our a new step goal of: " + newStepGoal);
            // Yes button sets the recommended step goal
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    SharedPreferences sharedPreferences = getSharedPreferences("savedStepGoal", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("step", "" + newStepGoal);
                    editor.apply();
                    updateStepCountAndStride();
                }
            });
            // Redirect to page to let the user set their own step goal
            builder.setNeutralButton("I'd like to set my own new step goal", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    launchInputHeightStepGoalActivity();
                }
            });
            // Close the message
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();

            // Save the date that the accomplishment notification has been set
            sharedPref = getSharedPreferences("accomplishmentDate", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            String currentDate = sharedPref.getString("currentDate", "");
            editor.putString("date", currentDate);
            editor.putBoolean("accomplishmentDisplayed", true);
            editor.apply();
        }


    }

    /**
     * Override the onConnectionSuspended method implementing ConnectionCallbacks
     * @param i
     */
    @Override
    public void onConnectionSuspended(int i) {
        Log.e("HistoryAPI", "onConnectionSuspended");
    }

    /**
     * Override onConnectionFailed method implementing onConnectionFailedListener
     * @param connectionResult
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("HistoryAPI", "onConnectionFailed");
    }

    /**
     * Override onConnected method implementing ConnectionCallbacks
     * @param bundle
     */
    public void onConnected(@Nullable Bundle bundle) {
        Log.e("HistoryAPI", "onConnected");

    }

}
