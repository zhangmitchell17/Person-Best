package com.example.team31_personalbest;

import android.content.SharedPreferences;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class ProgressTest {
    ProgressActivity progressActivity;

    @Before
    public void init() {
        progressActivity = Robolectric.setupActivity(ProgressActivity.class);
    }

    @Test
    public void testInitial() {
        assertEquals(0, 1 - 1);
    }
}
