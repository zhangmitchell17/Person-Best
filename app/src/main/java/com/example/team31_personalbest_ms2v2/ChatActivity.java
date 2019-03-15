package com.example.team31_personalbest_ms2v2;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    String TAG = MainActivity.class.getSimpleName();

    String COLLECTION_KEY = "Chats";
    String DOCUMENT_KEY;
    String FROM_KEY = "from";
    String TEXT_KEY = "text";
    String MESSAGE_KEY = "messages";

    CollectionReference chat;
    String from;
    String to;
    FireBaseAdapter fireBaseAdapter;

    String intendedMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        FirebaseApp.initializeApp(this);

        getFromAndTo();
        setupDocumentKey();
        setupChat();

        fireBaseAdapter = new FireBaseAdapter(chat, DOCUMENT_KEY, FROM_KEY, TEXT_KEY);
        initMessageUpdateListener();
        subscribeToNotificationsTopic();

        findViewById(R.id.btn_send).setOnClickListener(view -> sendMessage());

        TextView nameView = findViewById((R.id.friend_name));
        nameView.setText(to);

        Bundle extras = getIntent().getExtras();
        intendedMessage = extras.getString("MESSAGE");
        if (intendedMessage != "") {
            sendIntendedMessage();
        }

    }

    /**
     * A helper method to get the user and the friend of the user
     */
    private void getFromAndTo() {
        Bundle extraNames = getIntent().getExtras();
        from = extraNames.getString("USER_NAME");
        to = extraNames.getString("FRIEND_NAME");
        //System.out.println(from + " && " + to);
    }

    /**
     * A helper method to setup the document(chat) that we want to write in
     */
    private void setupDocumentKey() {
        if (from.compareTo(to) > 0) {
            DOCUMENT_KEY = COLLECTION_KEY + " between " + from + " and " + to;
        } else {
            DOCUMENT_KEY = COLLECTION_KEY + " between " + to + " and " + from;
        }

        DOCUMENT_KEY = DOCUMENT_KEY.replace(" ", "_");
    }

    private void setupChat() {
        chat = FirebaseFirestore.getInstance()
                .collection(COLLECTION_KEY)
                .document(DOCUMENT_KEY)
                .collection(MESSAGE_KEY);
        //if (chat == null) System.out.println("Chat is null! Can't chat with friend!");
    }

    /**
     * If there is a message already that the user wants to send, send it
     */
    private void sendIntendedMessage() {
        EditText messageView = findViewById(R.id.text_message);
        messageView.setText(intendedMessage);
        sendMessage();
    }

    /**
     * Send the message entered by the user
     */
    private void sendMessage() {
        EditText messageView = findViewById(R.id.text_message);

        Map<String, String> newMessage = new HashMap<>();
        newMessage.put(FROM_KEY, from);
        newMessage.put(TEXT_KEY, messageView.getText().toString());

        chat.add(newMessage).addOnSuccessListener(result -> {
            messageView.setText("");
        }).addOnFailureListener(error -> {
            Log.e(TAG, error.getLocalizedMessage());
        });
    }

    private void initMessageUpdateListener() {
        //System.out.println("MessageUpdate Called!");
        fireBaseAdapter.initMessageUpdateListener(new ChatListener() {
            @Override
            public void success(String string) {
                TextView chatView = findViewById(R.id.chat);
                chatView.append(string);
            }

            @Override
            public void error(String string) {
                Log.e(TAG, string);
                return;
            }
        });
    }

    private void subscribeToNotificationsTopic() {
        fireBaseAdapter.subscribeToNotificationsTopic(new ChatListener() {
            @Override
            public void success(String string) {
                Toast.makeText(ChatActivity.this, string, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void error(String string) {
                Toast.makeText(ChatActivity.this, string, Toast.LENGTH_SHORT).show();
            }
        }, FirebaseMessaging.getInstance());
    }
}







