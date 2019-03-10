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
    String CHAT_KEY = "Chats";
    String fromKey;
    String textKey;
    String TIMESTAMP_KEY = "timestamp";

    CollectionReference chat;
    public FireBaseAdapter(CollectionReference chat, String fromKey, String textKey) {
        this.chat = chat;
        this.fromKey = fromKey;
        this.textKey = textKey;
    }

    public void initMessageUpdateListener(ChatListener listener) {
          chat.orderBy(TIMESTAMP_KEY, Query.Direction.ASCENDING).addSnapshotListener((newChatSnapShot, error) -> {
              //System.out.println("Listener working!");
              if (newChatSnapShot == null || !newChatSnapShot.isEmpty()) System.out.println("Here is a HUUUUUUUUUGE error.");
                if (newChatSnapShot != null && !newChatSnapShot.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    List<DocumentChange> documentChanges = newChatSnapShot.getDocumentChanges();
                    documentChanges.forEach(change -> {
                        QueryDocumentSnapshot document = change.getDocument();
                        sb.append(document.get(fromKey));
                        sb.append(":\n");
                        sb.append(document.get(textKey));
                        sb.append("\n");
                        sb.append("---\n");
                    });

                    listener.success(sb.toString());
                }

                if(error != null) {
                    //System.out.println("An error happened during showing string.");
                    listener.error("error");
                }
          });
    }

    public void subscribeToNotificationsTopic(ChatListener listener, FirebaseMessaging messaging) {
        messaging.subscribeToTopic(CHAT_KEY)
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
