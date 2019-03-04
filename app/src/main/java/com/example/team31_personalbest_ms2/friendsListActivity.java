package com.example.team31_personalbest_ms2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static android.support.constraint.Constraints.TAG;

public class friendsListActivity extends AppCompatActivity {
    String friendEmail;
    Button addFriendButton;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

        Toast.makeText(getApplicationContext(),"Sueccessfully send friend invitation", Toast.LENGTH_LONG).show();
        // when user click add friend button show dialog
        addFriendButton = findViewById(R.id.addFriend);
        addFriendButton.setOnClickListener((v -> {
            showDialog();

        }));
    }

    /**
     * Show the input for friends name and email for user to add friends
     */
    public void showDialog() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Friend");

        builder.setView(layout);

        // Set up the input for email
        final EditText inputEmail = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        inputEmail.setInputType(InputType.TYPE_CLASS_TEXT);
        inputEmail.setHint("Friend's Email");
        builder.setView(inputEmail);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            // check if user exists and add friends
            @Override
            public void onClick(DialogInterface dialog, int which) {
                friendEmail = inputEmail.getText().toString();

                // check if such friends exists in database
                userExists(friendEmail, new IListener() {
                    @Override
                    public void success() {
                        Toast.makeText(getApplicationContext(),"Sueccessfully send friend invitation", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void faliure() {

                    }
                });
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    /**
     * Add a user to the database
     * @param name name of the user
     * @param email email of the user
     */
    public void addUser(String name, String email) {
        // add user to database
        User user = new User(name, email);
        db.collection("users").document(user.email).set(user);
    }

    /**
     * Add friends to existing user
     * @param email email of the user
     * @param friend friend object
     */
    public void addFriends(String email, User friend) {
        // add user to friends list
        db.collection("friends").document(email).set(friend);
    }

    public void userExists(String email, IListener listener) {
        DocumentReference docRef = db.collection("users").document(email);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }


}
