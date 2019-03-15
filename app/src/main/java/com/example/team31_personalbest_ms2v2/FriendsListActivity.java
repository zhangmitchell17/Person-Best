package com.example.team31_personalbest_ms2v2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import static android.support.constraint.Constraints.TAG;

public class FriendsListActivity extends AppCompatActivity {
    String friendEmail;
    Button addFriendButton;
    String currentUserEmail = "test@ucsd.edu";
    String currentUserName = "test";
    User user;
    FirebaseFirestore db;

    /**
     * Set up the firebase and insert current user info to the database
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        // get current user account information
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            this.currentUserEmail = acct.getEmail();
            this.currentUserName = acct.getDisplayName();
        }

        // add current user to the users database
        //FirebaseApp.initializeApp(this);

        db = FirebaseFirestore.getInstance();


        this.user = new User(currentUserName, currentUserEmail);

        addUser(user);


        // when user click add friend button show dialog
        addFriendButton = findViewById(R.id.addFriend);
        addFriendButton.setOnClickListener((v -> {
            showDialog(new AlertDialog.Builder(this));
        }));

        // asyncrnously add friends to each other
        final CollectionReference docRef = db.collection("users").
                document(currentUserEmail).collection("Friends");
        final Context context = this;

        docRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "listen:error", e);
                    return;
                }

                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            Log.d(TAG, "Friend: " + dc.getDocument().getData());
                            User friend  = dc.getDocument().toObject(User.class);

                            // add friend label to the friend list
                            Button newFriend = new Button(context);
                            newFriend.setVisibility(View.VISIBLE);
                            newFriend.setText(friend.name + " " + friend.email);
                            LinearLayout linearLayout = findViewById(R.id.linearLayout);
                            linearLayout.addView(newFriend);

                            // when user click friends button redirects to the friends info
                            newFriend.setOnClickListener((v -> {
                                //launchChatActivity(friend.getName(),currentUserName);
                                startProgressActivity(friend.email);
                            }));

                            break;
                    }
                }
            }
        });
    }

    public void startProgressActivity(String friendEmail) {
        Intent intent = new Intent(this, ProgressActivity.class);
        Log.i("SHIT", "friends email is ");
        intent.putExtra("Email", friendEmail);
        startActivity(intent);
    }

    public void launchChatActivity(String friendName, String userName) {
        Intent intent = new Intent(this, ChatActivity.class);
        Bundle extraNames = new Bundle();
        extraNames.putString("FRIEND_NAME",friendName);
        extraNames.putString("USER_NAME",userName);
        intent.putExtras(extraNames);
        startActivity(intent);
    }

    /**
     * Show the input for friends name and email for user to add friends
     */
    public void showDialog(AlertDialog.Builder builder) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
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
                    // success, add this friend to user's friend base
                    @Override
                    public void success() {
                        Toast.makeText(getApplicationContext(),
                                "Successfully send friend invitation", Toast.LENGTH_LONG).show();
                        sendInvitation(currentUserEmail, friendEmail);
                    }

                    // failure, show toast
                    @Override
                    public void failure() {
                        Toast.makeText(getApplicationContext(),"No such user exists",
                                       Toast.LENGTH_LONG).show();
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
     * @param user name of the user
     */
    public void addUser(User user) {
        db.collection("users").document(user.email).set(user);
    }

    public void sendInvitation(String userEmail, String friendEmail) {
        Map<String, Object> invitation = new HashMap<>();
        invitation.put("friendEmail", friendEmail);

        DocumentReference docRef = db.collection("users").
                document(friendEmail);

        // check if such user exists
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User friend = documentSnapshot.toObject(User.class);

                invitationExists(friendEmail, new IListener() {
                    @Override
                    public void success() {
                        addFriends(user, friend);
                        Toast.makeText(getApplicationContext(),"You and " + friend.name + " become friends",
                                Toast.LENGTH_LONG).show();
                    }

                    // not find double ended invitation, do not add friends, just send invitation
                    @Override
                    public void failure() {
                        db.collection("users").document(friendEmail).
                                collection("Invitation").document(userEmail).
                                set(user);
                    }
                });
            }
        });
    }

    /**
     * Add friends to existing user
     * @param user email of the user
     * @param friend friend object
     */
    public void addFriends(User user, User friend) {
        // reference of friend's document
        db.collection("users").document(user.email).
                collection("Friends").
                document(friend.email).set(friend);

        db.collection("users").document(friend.email).
                collection("Friends").
                document(user.email).set(user);
    }

    /**
     * Check if user exist in the database
     * @param email
     * @param listener
     */
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

    /**
     * Check if a invitation from friends already exists in your database
     * @param friendEmail
     * @param listener
     */
    public void invitationExists(String friendEmail, IListener listener) {
        DocumentReference docRef = db.collection("users").document(currentUserEmail).
                collection("Invitation").document(friendEmail);
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
