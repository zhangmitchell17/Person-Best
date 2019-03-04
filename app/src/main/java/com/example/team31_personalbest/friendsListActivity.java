package com.example.team31_personalbest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

public class friendsListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        FirebaseApp.initializeApp(this);
        // add user to database
        User user = new User("Ajax", "hoz054@ucsd.edu");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(user.name).set(user);
    }
}
