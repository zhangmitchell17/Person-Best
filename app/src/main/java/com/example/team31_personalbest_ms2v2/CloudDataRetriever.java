package com.example.team31_personalbest_ms2v2;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.support.constraint.Constraints.TAG;

public class CloudDataRetriever {
    Context c;
    FirebaseFirestore ff;
    HashMap<String, CollectionReference> colMap;
    public CloudDataRetriever(Context c) {
        FirebaseApp.initializeApp(c);
        ff = FirebaseFirestore.getInstance();

        String email = "";

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(c);
        if (acct != null) {
            email = acct.getEmail();
        }

        colMap = new HashMap<>();
        colMap.put("plannedWalkRuns", ff.collection("users")
                .document(email)
                .collection("WalkRuns"));
    }

    /**
     *
     * @param days interval of time in days that you would like to retrieve data for
     * @return
     */
    public HashMap<String, HashMap<String, Integer>> parseData(int days) {
        HashMap<String, HashMap<String, Integer>> finalMap = new HashMap<>();
        for (Map.Entry<String, CollectionReference> entry : colMap.entrySet()) {
            HashMap<String, Integer> valueMap = new HashMap<>();
            String colRefName = entry.getKey();
            CollectionReference colRef = entry.getValue();
            colRef.orderBy("monthDayYear", Query.Direction.DESCENDING)
                    .addSnapshotListener((newChatSnapShot, error) -> {
                        if (error != null) {
                            Log.e(TAG, error.getLocalizedMessage());
                            return;
                        }

                        if (newChatSnapShot != null && !newChatSnapShot.isEmpty()) {
                            List<DocumentChange> documentChanges = newChatSnapShot.getDocumentChanges();
                            documentChanges.forEach(change -> {
                                QueryDocumentSnapshot document = change.getDocument();
                                // if its within the past 'days' days
                                for (int i = 0; i < days; i++) {
                                    // makes a calendar that subtracts i days from current date
                                    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy");
                                    Calendar cal = Calendar.getInstance();
                                    Date date = new Date();
                                    cal.setTime(date);
                                    cal.add(Calendar.DAY_OF_YEAR, -1 * i);
                                    String dateString = sdf.format(cal);
                                    // if data for it exists, then add it to the HashTable of walks
                                    if (dateString.equals(document.get("monthDayYear"))) {

                                        Integer newSteps = Integer.parseInt((String)document.get("steps)"));
                                        // if the string exists in the value map, then
                                        // increment the old value by the new value, otherwise
                                        // just put in it
                                        if(valueMap.containsKey(dateString)) {
                                            //add
                                            Integer oldSteps = valueMap.get(dateString);
                                            valueMap.replace(dateString, oldSteps + newSteps);

                                        } else {
                                            // put
                                            valueMap.put(dateString, newSteps);
                                        }
                                        break;
                                    }
                                }
                            });
                        }
                    });
            finalMap.put(colRefName, valueMap);
        }
        return finalMap;
    }
}
