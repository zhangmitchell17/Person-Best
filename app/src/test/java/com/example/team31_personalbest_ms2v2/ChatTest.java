package com.example.team31_personalbest_ms2v2;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.messaging.FirebaseMessaging;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class ChatTest {
    CollectionReference mockCollectionReference;
    FireBaseAdapter firebaseAdapter;
    String DOCUMENT_KEY = "Chats_between_Shicheng_Fan_and_Hongyu_Zou";
    String FROM_KEY = "from";
    String TEXT_KEY = "text";

    @Before
    public void init() {
        mockCollectionReference = Mockito.mock(CollectionReference.class);
        firebaseAdapter = new FireBaseAdapter(mockCollectionReference, DOCUMENT_KEY, FROM_KEY, TEXT_KEY);
    }

    @Test
    public void testOrderBy() {
        when(mockCollectionReference.orderBy(anyString(), (anyObject()))).
                thenReturn(mockCollectionReference);

        firebaseAdapter.initMessageUpdateListener(new ChatListener() {
            @Override
            public void success(String string) {}

            @Override
            public void error(String string) {}
        });

        Mockito.verify(mockCollectionReference, atLeastOnce()).orderBy(anyString(), anyObject());
    }

    @Test
    public void testSubscription() {
        FirebaseMessaging mockMessaging = Mockito.mock(FirebaseMessaging.class);
        when(mockMessaging.subscribeToTopic(anyString())).thenReturn(Mockito.mock(Task.class));
        firebaseAdapter.subscribeToNotificationsTopic(null, mockMessaging);
        Mockito.verify(mockMessaging, atLeastOnce()).subscribeToTopic(anyString());
    }
}
