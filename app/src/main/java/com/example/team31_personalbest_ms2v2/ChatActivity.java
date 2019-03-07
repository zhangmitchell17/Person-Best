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

class ChatActivity extends AppCompatActivity {
    String TAG = MainActivity.class.getSimpleName();

    String COLLECTION_KEY = "chats";
    String DOCUMENT_KEY = "chat1";
    String MESSAGES_KEY = "messages";
    String FROM_KEY = "from";
    String TEXT_KEY = "text";
    String TIMESTAMP_KEY = "timestamp";

    CollectionReference chat;
    String from;
    FireBaseAdapter fireBaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sharedpreferences = getSharedPreferences("FirebaseLabApp", Context.MODE_PRIVATE);

        FirebaseApp.initializeApp(this);
        from = sharedpreferences.getString(FROM_KEY, null);

        setupChat();

        fireBaseAdapter = new FireBaseAdapter(chat);
        initMessageUpdateListener();
        subscribeToNotificationsTopic();

        findViewById(R.id.btn_send).setOnClickListener(view -> sendMessage());

        EditText nameView = findViewById((R.id.user_name));
        nameView.setText(from);
        nameView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                from = s.toString();
                sharedpreferences.edit().putString(FROM_KEY, from).apply();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setupChat() {
        chat = FirebaseFirestore.getInstance()
                .collection(COLLECTION_KEY)
                .document(DOCUMENT_KEY)
                .collection(MESSAGES_KEY);
    }

    private void sendMessage() {
        if (from == null || from.isEmpty()) {
            Toast.makeText(this, "Enter your name", Toast.LENGTH_SHORT).show();
            return;
        }

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


