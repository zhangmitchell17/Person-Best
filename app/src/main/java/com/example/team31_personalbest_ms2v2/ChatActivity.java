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

    //String DOCUMENT_KEY = "chat1";
    String CHATS_KEY = "Chats";
    String thisChatKey;
    String FROM_KEY = "from";
    String TEXT_KEY = "text";
    String MESSAGE_KEY = "messages";
    String TIMESTAMP_KEY = "timestamp";

    CollectionReference chat;
    String from;
    String to;
    FireBaseAdapter fireBaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //SharedPreferences sharedpreferences = getSharedPreferences("FirebaseLabApp", Context.MODE_PRIVATE);

        FirebaseApp.initializeApp(this);

        //CollectionReference users = FirebaseFirestore.getInstance().collection(USERS_KEY);
        //from = sharedpreferences.getString(FROM_KEY, null);

        Bundle extraNames = getIntent().getExtras();
        from = extraNames.getString("USER_NAME");
        to = extraNames.getString("FRIEND_NAME");
        System.out.println(from + " && " + to);

        if (from.compareTo(to) > 0) {
            thisChatKey = CHATS_KEY + " between " + from + " and " + to;
        } else {
            thisChatKey = CHATS_KEY + " between " + to + " and " + from;
        }

        setupChat();

        fireBaseAdapter = new FireBaseAdapter(chat, FROM_KEY, TEXT_KEY);
        initMessageUpdateListener();
        subscribeToNotificationsTopic();

        findViewById(R.id.btn_send).setOnClickListener(view -> sendMessage());

        TextView nameView = findViewById((R.id.friend_name));
        nameView.setText(to);
//        nameView.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                //from = s.toString();
//                //sharedpreferences.edit().putString(FROM_KEY, from).apply();
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//            }
//        });
    }

    private void setupChat() {
        chat = FirebaseFirestore.getInstance()
                .collection(CHATS_KEY)
                .document(thisChatKey)
                .collection(MESSAGE_KEY);
        //if (chat == null) System.out.println("Chat is null! Can't chat with friend!");
    }

    private void sendMessage() {
//        if (from == null || from.isEmpty()) {
//            Toast.makeText(this, "Enter your name", Toast.LENGTH_SHORT).show();
//            return;
//        }

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







