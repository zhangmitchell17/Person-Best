package com.example.team31_personalbest_ms2v2;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.Subject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static android.support.constraint.Constraints.TAG;
import static java.lang.Integer.parseInt;

// used to create timer and reset step at beginning of day
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        IStepActivity,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static Activity mainActivity;

    private static final String TAG = "SignIn";
    String TAG2 = MainActivity.class.getSimpleName();

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

    public String currentUserEmail = "test@ucsd.edu";
    public String currentUserName = "test";
    User user;
    public FirebaseFirestore db;
    CollectionReference notifications;

    private String steps;
    private Long goals;

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
                fitnessService.updateStepCount();
                sendStepsToCloud(db);
                goalAchievement();
                Log.i("BoardCast: ", "received boardcast");
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

        // store user info
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            this.currentUserEmail = acct.getEmail();
            this.currentUserName = acct.getDisplayName();
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

        // init firebase firestore
        FirebaseApp.initializeApp(this);
        this.db = FirebaseFirestore.getInstance();

        notifications = FirebaseFirestore.getInstance()
                .collection("Notifications")
                .document("notifications1")
                .collection("notification");

        // init fitness service
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

        subscribeToNotificationsTopic("notifications1");
    }

    /**
     * When user start using new phone or re download the app, he should get goal/step from cloud
     */
    public void grabUserStrideGoalFromCloud(String type, TextView view) {
        DocumentReference docRef;
            docRef = db.collection("users").
                    document(currentUserEmail).collection("HeightAndGoal").document(type);

        // grabe user stride and
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Object stride = documentSnapshot.get(type);
                if (type.equals("stride")) {
                    view.setText("Your " + type + " length is: " + stride);
                } else {
                    view.setText("Your step " + type + " is: " + stride);
                }
            }
        });
    }

    /**
     * Send current user data to cloud
     */
    public void sendStepsToCloud(FirebaseFirestore db) {
        TextView view = findViewById(R.id.textViewStepMain);
        String steps = view.getText().toString();
        Log.i("Cloud steps: ", ("Steps to the cloud: " + steps));

        Map<String, String> dailyStepCnt = new HashMap();
        dailyStepCnt.put("steps", steps);

        Date day = new Date();
        String simpleDate = new SimpleDateFormat("MM-dd-yyyy").
                format(Calendar.getInstance().getTime());

        String date = day.toString();
        String dayOfWeek = date.substring(0, date.indexOf(" "));
        int indexOfEnd = date.indexOf(" ", date.indexOf(" ", date.indexOf(" ") + 1) +1);
        String monthDayYear = date.substring(date.indexOf(" ") + 1, indexOfEnd) + " " + date.substring(date.length() - 4);
        dailyStepCnt.put("monthDayYear", monthDayYear);
        db.collection("users").document(this.currentUserEmail).
                collection("steps").document(simpleDate).set(dailyStepCnt);
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

        // update step count on screen
        updateStepCountAndStride();

        updateSteps();

        // restore cloud goal and steps
        grabUserStrideGoalFromCloud("stride", findViewById(R.id.stride_length));
        grabUserStrideGoalFromCloud("goal", findViewById(R.id.step_count));
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
        TextView strideText = findViewById(R.id.stride_length);
        String text = strideText.getText().toString();
        intent.putExtra("stride", text);
        startActivity(intent);
    }

    /**
     * launchProgressActivity launches the Progress Activity
     */
    public void launchProgressActivity() {
        Intent intent = new Intent(this, ProgressActivity.class);
        String email = "";
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            email = acct.getEmail();
        }
        intent.putExtra("Email", email);
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
        Intent intent = new Intent(this, FriendsListActivity.class);
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
     * Add a user to the database
     * @param user name of the user
     */
    public void addUser(User user) {
        db.collection("users").document(user.email).set(user);
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

        // store user info
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            this.currentUserEmail = acct.getEmail();
            this.currentUserName = acct.getDisplayName();
            this.user = new User(currentUserName, currentUserEmail);
            addUser(user);
            launchInputHeightStepGoalActivity();
        }

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


    private void sendNotification(String message) {
        Map<String, String> newMessage = new HashMap<>();
        newMessage.put("text", message);

        notifications.add(newMessage).addOnSuccessListener(result -> {
            Log.e(TAG2, "Successfully sent notification");
        }).addOnFailureListener(error -> {
            Log.e(TAG2, error.getLocalizedMessage());
        });
    }

    private void subscribeToNotificationsTopic(String documentKey) {
        FirebaseMessaging.getInstance().subscribeToTopic(documentKey)
                .addOnCompleteListener(task -> {
                            String msg = "Subscribed to notifications";
                            if (!task.isSuccessful()) {
                                msg = "Subscribe to notifications failed";
                            }
                            Log.d(TAG2, msg);
                            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                );
    }

    void showNotification(String title, String content) {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default",
                    "YOUR_CHANNEL_NAME",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("YOUR_NOTIFICATION_CHANNEL_DISCRIPTION");
            mNotificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "default")
                .setSmallIcon(R.mipmap.ic_launcher) // notification icon
                .setContentTitle(title) // title for notification
                .setContentText(content)// message for notification
                .setAutoCancel(true); // clear notification after click
        Intent intent = new Intent(getApplicationContext(), InputHeightStepGoal.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pi);
        mNotificationManager.notify(0, mBuilder.build());
    }

    /**
     * This method displays the step goal achievement notification and
     * asks if the user wants to set a new step goal
     * @param stepCount The number of current steps to compare the step goal to
     */
    public void goalAchievement() {
        String date = new SimpleDateFormat("MM-dd-yyyy").
                format(Calendar.getInstance().getTime());

        DocumentReference docRef = db.collection("users")
                .document(currentUserEmail)
                .collection("HeightAndGoal")
                .document("goal");

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    SharedPreferences sharedPreferences = getSharedPreferences("currentGoal", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    System.out.println("goal string " + document.getString("goal"));
                    editor.putString("goal", document.getString("goal"));
                    editor.apply();
                    System.out.println("sharePref: " + sharedPreferences.getString("goal", "-1"));
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

        SharedPreferences sharedPreferences2 = getSharedPreferences("currentGoal", MODE_PRIVATE);
        int stepGoal = Integer.parseInt(sharedPreferences2.getString("goal", "0"));

        docRef = db.collection("users")
                .document(currentUserEmail)
                .collection("steps")
                .document(date);

        docRef.get()
        .addOnCompleteListener(task -> {
            if (task.isSuccessful())
            {
                DocumentSnapshot document = task.getResult();
                if (document.exists())
                {
                    SharedPreferences sharedPreferences = getSharedPreferences("currentSteps", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("steps", document.getString("steps"));
                    editor.apply();
                }
            }
        });

        SharedPreferences sharedPref = getSharedPreferences(date + 1, MODE_PRIVATE);
        goalAchievedDisplayed = sharedPref.getBoolean("accomplishmentDisplayed", false);

        System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");

        SharedPreferences sharePref = getSharedPreferences("currentSteps", MODE_PRIVATE);
        int currentSteps = Integer.parseInt(sharePref.getString("steps","0"));

        Log.i("goals: ", Integer.toString(stepGoal));
        Log.i("steps: ", Integer.toString(currentSteps));
        Log.i("goalAchievedDisplayed", Boolean.toString(goalAchievedDisplayed));

        // Only display it if the step count is greater than the step goal and the notification has not been displayed yet
        if (currentSteps >= stepGoal && !goalAchievedDisplayed) {
            showNotification("good job", "Please set a new step goal");

            // Save the date that the accomplishment notification has been set
            sharedPref = getSharedPreferences(date + 1, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
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