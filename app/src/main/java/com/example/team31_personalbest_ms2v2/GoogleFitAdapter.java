package com.example.team31_personalbest_ms2v2;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * This is a google find adapter adapt IStepActivity to the FitnessService interface
 */
public class GoogleFitAdapter implements FitnessService {
    private final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = System.identityHashCode(this) & 0xFFFF;
    private final String TAG = "GoogleFitAdapter";
    private IStepActivity stepActivity;

    /**
     * Constructor of the GoogleFitAdapter
     * @param stepActivity
     */
    public GoogleFitAdapter(IStepActivity stepActivity) {
        this.stepActivity = stepActivity;
    }

    /**
     * Initialize method for the Adapter
     */
    public void setup() {
        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .build();

        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount((Context)stepActivity), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    (Activity)stepActivity, // your stepActivity
                    GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                    GoogleSignIn.getLastSignedInAccount((Context)stepActivity),
                    fitnessOptions);
        } else {
            updateStepCount();
            startRecording();
        }

    }

    /**
     * Adapt the startRecording method for IStepActivity
     */
    private void startRecording() {
        //check if logged in
        GoogleSignInAccount lastSignedInAccount = GoogleSignIn.getLastSignedInAccount((Context)stepActivity);
        if (lastSignedInAccount == null) {
            return;
        }

        Fitness.getRecordingClient((Activity)stepActivity, GoogleSignIn.getLastSignedInAccount((Context)stepActivity))
                .subscribe(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "Successfully subscribed!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "There was a problem subscribing.");
                    }
                });
    }


    /**
     * Adapt the updateStepCount method which reads the current daily step total,
     * computed from midnight of the current day on the device's current timezone.
     */
    public void updateStepCount() {
        //check if logged in
        GoogleSignInAccount lastSignedInAccount = GoogleSignIn.getLastSignedInAccount((Context)stepActivity);
        if (lastSignedInAccount == null) {
            return;
        }

        Fitness.getHistoryClient((Activity)stepActivity, lastSignedInAccount)
                .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener(
                        new OnSuccessListener<DataSet>() {
                            @Override
                            public void onSuccess(DataSet dataSet) {
                                Log.d(TAG, dataSet.toString());
                                long total =
                                        dataSet.isEmpty()
                                                ? 0
                                                : dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();

                                stepActivity.setStepCount(total);
                                Log.d(TAG, "Total steps: " + total);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "There was a problem getting the step count.");
                            }
                        });

    }


    @Override
    /**
     * Override the getRequestCode method, return the request code
     */
    public int getRequestCode() {
        return GOOGLE_FIT_PERMISSIONS_REQUEST_CODE;
    }
}