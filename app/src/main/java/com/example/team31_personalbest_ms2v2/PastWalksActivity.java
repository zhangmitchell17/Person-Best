package com.example.team31_personalbest_ms2v2;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static android.support.constraint.Constraints.TAG;

public class PastWalksActivity extends AppCompatActivity {

    private static final String[] TIME_STEPS_MPH = {"Time", "Steps", "MPH"};

    FirebaseFirestore db;
    CollectionReference walks;

    String currentUserEmail = "test@ucsd.edu";
    String currentUserName = "test";

    TableLayout table;


    /**
     * Gets the day of the week index, with Sunday as 0 and Saturday as 6
     * @param day This is the string of the day of the week
     * @return the index of the day of the week, with Sunday as 0 and Saturday as 6
     * Returns -1 if string is not a day of the week
     */
    private int indexOfWeek (String day)
    {
        for (int i = 0; i < Constants.WEEKDAY_LOWER_SHORTENED.length; i++)
        {
            if (Constants.WEEKDAY_LOWER_SHORTENED[i].equals(day))
            {
                return i;
            }
        }
        return -1;
    }

    /**
     * Get a new Date for a certain number of days ago
     * @param numDaysAgo The number of days ago to get a Date object for
     * @return Date object on a certain number of days ago
     */
    private Date getPreviousDate (int numDaysAgo)
    {
        return new Date(System.currentTimeMillis() - (numDaysAgo * 24 * 60 * 60 * 1000));
    }

    /**
     * Get a String of Month (shortened) Day and Year in that order
     * @param date the String to get substrings of
     * @return a String of "Month(shortened) Day Year"
     */
    private String getMonthDayYear(String date)
    {
        int currentIndexOfEnd = date.indexOf(" ", date.indexOf(" ", date.indexOf(" ") + 1) +1);
        return date.substring(date.indexOf(" ") + 1, currentIndexOfEnd) + " " + date.substring(date.length() - 4);
    }

    /**
     * Begins on creation, set up the information from SharedPreferences and display it accordingly
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_walks);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            this.currentUserEmail = acct.getEmail();
            this.currentUserName = acct.getDisplayName();
        }

        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

        walks = db.collection("users")
                .document(currentUserEmail)
                .collection("WalkRuns");

        table = findViewById(R.id.tableLayout);

        initWalkRunUpdateListener();

    }

    private void addToTable(QueryDocumentSnapshot document) {
        TableRow row = new TableRow(this);
        TableLayout.LayoutParams tableRowParams=
                new TableLayout.LayoutParams
                        (TableLayout.LayoutParams.WRAP_CONTENT,TableLayout.LayoutParams.WRAP_CONTENT);
        tableRowParams.setMargins(0, 0, 0, 0);
        row.setLayoutParams(tableRowParams);

        // Iterate through every information saved for this run
        String totalTime = (String) document.get("totalTime");
        int temp = (int) Integer.parseInt(totalTime);
        // Updates hours
        int hours = temp / Constants.SECS_PER_HOUR;
        temp = temp % Constants.SECS_PER_HOUR;
        // Updates minutes
        int minutes = temp / Constants.SECS_PER_MIN;
        temp = temp % Constants.SECS_PER_MIN;
        // Updates seconds
        int seconds = temp;
        String time = String.format("%02d:%02d:%02d", hours, minutes, seconds);

        String speed = String.format("%.2f", document.get("speed"));

        String steps = (String) document.get("steps");

        String dayOfWeek = (String) document.get("dayOfWeek");


        TextView dayView = new TextView(this);
        TextView timeView = new TextView(this);
        TextView stepsView = new TextView(this);
        TextView speedView = new TextView(this);

        dayView.setText(dayOfWeek);
        dayView.setTextSize(20);

        timeView.setText(time);
        timeView.setTextSize(20);

        stepsView.setText(steps);
        stepsView.setTextSize(20);

        speedView.setText(speed);
        speedView.setTextSize(20);

        TableRow.LayoutParams prms = new TableRow.LayoutParams(300, TableRow.LayoutParams.WRAP_CONTENT);

        row.addView(dayView, prms);
        row.addView(timeView, prms);
        row.addView(stepsView, prms);
        row.addView(speedView, prms);

        table.addView(row);
    }

    private void initWalkRunUpdateListener() {
        walks.orderBy("monthDayYear", Query.Direction.DESCENDING)
                .addSnapshotListener((newChatSnapShot, error) -> {
                    if (error != null) {
                        Log.e(TAG, error.getLocalizedMessage());
                        return;
                    }

                    if (newChatSnapShot != null && !newChatSnapShot.isEmpty()) {
                        List<DocumentChange> documentChanges = newChatSnapShot.getDocumentChanges();
                        documentChanges.forEach(change -> {
                            QueryDocumentSnapshot document = change.getDocument();
                            for (int i = 0; i < 6; i++) {
                                if (getMonthDayYear(getPreviousDate(i).toString()) == document.get("monthDayYear")) {
                                    addToTable(document);
                                    break;
                                }
                            }
                        });
                    }
                });
    }
}


