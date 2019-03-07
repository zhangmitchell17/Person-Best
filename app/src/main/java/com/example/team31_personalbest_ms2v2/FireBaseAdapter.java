package com.example.team31_personalbest_ms2v2;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class FireBaseAdapter {
    String COLLECTION_KEY = "chats";
    String DOCUMENT_KEY = "chat1";
    String MESSAGES_KEY = "messages";
    String FROM_KEY = "from";
    String TEXT_KEY = "text";
    String TIMESTAMP_KEY = "timestamp";

    CollectionReference chat;

    public FireBaseAdapter(CollectionReference chat) {
        this.chat = chat;
    }

    public void initMessageUpdateListener(ChatListener listener) {
          chat.orderBy(TIMESTAMP_KEY, Query.Direction.ASCENDING).addSnapshotListener((newChatSnapShot, error) -> {

                if (newChatSnapShot != null && !newChatSnapShot.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    List<DocumentChange> documentChanges = newChatSnapShot.getDocumentChanges();
                    documentChanges.forEach(change -> {
                        QueryDocumentSnapshot document = change.getDocument();
                        sb.append(document.get(FROM_KEY));
                        sb.append(":\n");
                        sb.append(document.get(TEXT_KEY));
                        sb.append("\n");
                        sb.append("---\n");
                    });

                    listener.success(sb.toString());
                }

                if(error != null) {
                    listener.error("error");
                }
          });
    }

    public void subscribeToNotificationsTopic(ChatListener listener, FirebaseMessaging messaging) {
        messaging.subscribeToTopic(DOCUMENT_KEY)
                .addOnCompleteListener(task -> {
                            String msg = "Subscribed to notifications";

                            listener.success(msg);

                            if (!task.isSuccessful()) {
                                msg = "Subscribe to notifications failed";
                                listener.error(msg);
                            }

                        }
                );
    }




}
