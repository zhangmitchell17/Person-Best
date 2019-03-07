package com.example.team31_personalbest_ms2v2;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);
    UiDevice mUiDevice;

    @Before
    public void before() throws Exception {
        MainActivity.isCancelled = true;
        mUiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiObject mText = mUiDevice.findObject(new UiSelector().textContains("cloudtestlabaccounts.com"));
        try {
            mText.click();
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.example.team31_personalbest_ms2v2", appContext.getPackageName());
    }
}
