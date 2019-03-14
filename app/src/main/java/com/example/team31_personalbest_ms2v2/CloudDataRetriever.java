package com.example.team31_personalbest_ms2v2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static android.support.constraint.Constraints.TAG;

public class CloudDataRetriever implements ISubject<IDataRetrieverObserver> {
    Collection<IDataRetrieverObserver> observers;

    Context c;
    FirebaseFirestore ff;

    /* maps names of collections to their respective collection reference */
    HashMap<String, CollectionReference> colMap;
    public CloudDataRetriever(Context c, String email) {
        FirebaseApp.initializeApp(c);
        ff = FirebaseFirestore.getInstance();

        colMap = new HashMap<>();
        colMap.put("ps", ff.collection("users")
                .document(email)
                .collection("WalkRuns"));
        colMap.put("ups", ff.collection("users")
                .document(email)
                .collection("steps"));

        observers = new ArrayList<>();
    }

    /**
     *
     * @param days interval of time in days that you would like to retrieve data for
     * @return
     */
    public void parseData(int days) {

        for (Map.Entry<String, CollectionReference> entry : colMap.entrySet()) {
            HashMap<String, Integer> valueMap = new HashMap<>();
            String colRefName = entry.getKey();
            CollectionReference colRef = entry.getValue();
            Log.d(this.getClass().getSimpleName(),"Pulling data from firestore");
            colRef.get()
                  .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                      @Override
                      public void onComplete(@NonNull Task<QuerySnapshot> task) {
                          Log.d("SHIT","Task was completed");
                          if (task.isSuccessful()) {

                              HashSet<String> dateSet = new HashSet<>();
                              List<String> dateList = new ArrayList<>();
                              SimpleDateFormat cloudFormat = new SimpleDateFormat("MMM dd yyyy");
                              // create hashset of date strings
                              for(int i = 0; i > -1*days; i--) {
                                  Calendar cal = Calendar.getInstance();
                                  cal.add(Calendar.DAY_OF_YEAR, i);
                                  dateSet.add(cloudFormat.format(cal.getTime()));
                                  dateList.add(0, cloudFormat.format(cal.getTime()));
                              }

                              for(QueryDocumentSnapshot document : task.getResult()) {

                                  if(dateSet.contains(document.get("monthDayYear"))) {
                                      String dateString = (String) document.get("monthDayYear");
                                      Integer newSteps = Integer.parseInt((String)document.get("steps"));
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
                                  }
                              }

                              Log.d("SHIT","SHIT SHULD BE NOTIFIED");
                              Log.i("SHIT", "Retrieved data for " + colRefName);
                              dataRetrievalComplete(colRefName, mapToList(dateList, valueMap));
                          }
                      }
                  });
        }
    }

    public void register(IDataRetrieverObserver observer) {
        observers.add(observer);
    }

    public void unregister(IDataRetrieverObserver observer) {
        observers.remove(observer);
    }

    private void dataRetrievalComplete(String label, List<Integer> list) {
        Log.i("SHIT", "notifying observers");
        for(IDataRetrieverObserver observer: observers) {
            observer.onDataRetrieved(label, list);
        }
    }

    private List<Integer> mapToList(List<String> dates, Map<String, Integer> map) {
        List<Integer> res = new ArrayList<>();
        Log.i("SHIT", "map values");
        for(String s: dates) {
            int value = 0;
            if(map.containsKey(s)) {
                value = map.get(s);
            }
            res.add(value);
            Log.i("SHIT", "Date: " + s + "\nSteps: " + value );

        }
        return res;
    }
}
