package com.example.team31_personalbest_ms2v2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Button;
import android.support.v7.widget.Toolbar;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

import net.bytebuddy.utility.RandomString;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowIntent;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;


@RunWith(RobolectricTestRunner.class)
public class FriendListTest {
    FriendsListActivity friendsListActivity;
    ActivityController<FriendsListActivity> friendsController;
    MockFriendsListActivity mockFriendsListActivity;

    @Before
    public void init() {
        friendsListActivity = Mockito.mock(FriendsListActivity.class);
        //mockFriendsListActivity = Robolectric.setupActivity(MockFriendsListActivity.class);
    }

    /*
    @Test
    public void testAddPeople() {
        mockFriendsListActivity.addUser();
        mockFriendsListActivity.userExists("tes@ucsd.edu", new IListener() {
            @Override
            public void success() {
            }
            @Override
            public void failure() {
                Assert.fail();
            }
        });
    }
    */

    @Test
    public void testUserInit() {
        User user = new User("Hongyu Zou", "hoz054@ucsd.edu");
        assertEquals(user.name, "Hongyu Zou");
        assertEquals(user.email, "hoz054@ucsd.edu");
    }

    @Test
    public void testWrongNameAndEmail() {
        User user = new User("hoz054@ucsd.edu", "Hongyu Zou");
        assertNotEquals(user.name, "Hongyu Zou");
        assertNotEquals(user.email, "hoz054@ucsd.edu");
    }

    @Test
    public void testAddUser() {
        friendsListActivity.addUser(Mockito.anyObject());
        verify(friendsListActivity, times(1)).addUser(Mockito.anyObject());
    }

    @Test
    public void testSendInviation() {
        friendsListActivity.sendInvitation(Mockito.anyString(), Mockito.anyString());
        verify(friendsListActivity, atLeastOnce()).sendInvitation(Mockito.anyString(),
                Mockito.anyString());
    }

    @Test
    public void testShowingDialog() {
        friendsListActivity.showDialog(Mockito.anyObject());
        verify(friendsListActivity, atLeastOnce()).showDialog(Mockito.anyObject());
    }


}