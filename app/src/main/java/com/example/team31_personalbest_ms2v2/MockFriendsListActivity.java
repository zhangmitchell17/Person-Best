package com.example.team31_personalbest_ms2v2;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static android.support.constraint.Constraints.TAG;

public class MockFriendsListActivity extends AppCompatActivity {
    String currentUser = "TestUser";
    String currentEmail = "test@ucsd.edu";
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mock_friends_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

    }

    public void addUser() {
        db.collection("users").document(currentEmail)
                .set(new User(currentUser, currentEmail));
    }

    public void userExists(String email, IListener listener) {
        DocumentReference docRef = db.collection("users").document(email);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    listener.success();
                } else {
                    Log.d(TAG, "No such document");
                    listener.failure();
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }

}
