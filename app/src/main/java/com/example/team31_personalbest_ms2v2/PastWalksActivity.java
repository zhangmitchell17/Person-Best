package com.example.team31_personalbest_ms2v2;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
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
     *
     * @param day This is the string of the day of the week
     * @return the index of the day of the week, with Sunday as 0 and Saturday as 6
     * Returns -1 if string is not a day of the week
     */
    private int indexOfWeek(String day) {
        for (int i = 0; i < Constants.WEEKDAY_LOWER_SHORTENED.length; i++) {
            if (Constants.WEEKDAY_LOWER_SHORTENED[i].equals(day)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Get a new Date for a certain number of days ago
     *
     * @param numDaysAgo The number of days ago to get a Date object for
     * @return Date object on a certain number of days ago
     */
    private Date getPreviousDate(int numDaysAgo) {
        return new Date(System.currentTimeMillis() - (numDaysAgo * 24 * 60 * 60 * 1000));
    }

    /**
     * Get a String of Month (shortened) Day and Year in that order
     *
     * @param date the String to get substrings of
     * @return a String of "Month(shortened) Day Year"
     */
    private String getMonthDayYear(String date) {
        int currentIndexOfEnd = date.indexOf(" ", date.indexOf(" ", date.indexOf(" ") + 1) + 1);
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

        //table = findViewById(R.id.tableLayout);
        addToTable();
    }

    private void addToTable() {
        final Context context = this;
        db.collection("users/" + currentUserEmail + "/WalkRuns")
                //.whereEqualTo("capital", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                System.out.println("fill table with stats");
                                Log.d(TAG, document.getId() + " => " + document.getData());

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

                                String speed = ((String) document.get("speed")).substring(0, 2);

                                String steps = (String) document.get("steps");

                                String dayOfWeek = (String) document.get("dayOfWeek");

                                // add friend label to the friend list
                                TextView newFriend = new Button(context);
                                newFriend.setVisibility(View.VISIBLE);
                                newFriend.setText("time: " + time + " spd: " + speed + " steps: " + steps + " " + dayOfWeek);
                                newFriend.setWidth(500);
                                newFriend.setHeight(50);
                                LinearLayout linearLayout = findViewById(R.id.linearLayout2);
                                linearLayout.addView(newFriend);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
