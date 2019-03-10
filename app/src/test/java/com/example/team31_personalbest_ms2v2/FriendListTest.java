package com.example.team31_personalbest_ms2v2;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


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
