package com.example.team31_personalbest;


import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class InputHeightStepGoalEspressoTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void inputHeightStepGoalEspressoTest() {
        Login.loggedIn = true;
        ViewInteraction appCompatImageButton = onView(
                allOf(withContentDescription("Open navigation drawer"),
                        childAtPosition(
                                allOf(withId(R.id.toolbar),
                                        childAtPosition(
                                                withClassName(is("android.support.design.widget.AppBarLayout")),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction navigationMenuItemView = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.design_navigation_view),
                                childAtPosition(
                                        withId(R.id.nav_view),
                                        0)),
                        1),
                        isDisplayed()));
        navigationMenuItemView.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.textViewHeightPrompt), withText("Enter your height (inches)"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),

                        isDisplayed()));

        textView.check(matches(withText("Enter your height (inches)")));

        ViewInteraction editText = onView(
                allOf(withId(R.id.heightInput), withText("Height (In inches)"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        editText.check(matches(withText("Height (In inches)")));

        ViewInteraction button = onView(
                allOf(withId(R.id.updateHeight),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        button.check(matches(isDisplayed()));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.textViewStepPrompt), withText("Enter your step goal"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                4),
                        isDisplayed()));
        textView2.check(matches(withText("Enter your step goal")));

        ViewInteraction editText2 = onView(
                allOf(withId(R.id.stepInput), withText("Step Goal"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                5),
                        isDisplayed()));
        editText2.check(matches(withText("Step Goal")));

        ViewInteraction button2 = onView(
                allOf(withId(R.id.updateStep),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                7),
                        isDisplayed()));
        button2.check(matches(isDisplayed()));

        ViewInteraction button3 = onView(
                allOf(withId(R.id.backToMain),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                8),
                        isDisplayed()));
        button3.check(matches(isDisplayed()));

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.updateHeight), withText("Update Height"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction editText3 = onView(
                allOf(withId(R.id.heightInput), withText("Height (In inches)"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        editText3.check(matches(withText("Height (In inches)")));

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.enteredHeight), withText("Please enter your height :)"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        textView3.check(matches(withText("Please enter your height :)")));

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.heightInput),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("0"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.heightInput), withText("0"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText2.perform(click());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.updateHeight), withText("Update Height"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        appCompatButton2.perform(click());

        ViewInteraction editText4 = onView(
                allOf(withId(R.id.heightInput), withText("0"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        editText4.check(matches(withText("0")));

        ViewInteraction textView4 = onView(
                allOf(withId(R.id.enteredHeight), withText("Please enter your height :)"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        textView4.check(matches(withText("Please enter your height :)")));

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.heightInput), withText("0"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText3.perform(replaceText("60"));

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.heightInput), withText("60"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText4.perform(closeSoftKeyboard());

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.updateHeight), withText("Update Height"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        appCompatButton3.perform(click());

        ViewInteraction editText5 = onView(
                allOf(withId(R.id.heightInput), withText("60"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        editText5.check(matches(withText("60")));

        ViewInteraction textView5 = onView(
                allOf(withId(R.id.enteredHeight), withText("Your stride length will be 24 inches"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        textView5.check(matches(withText("Your stride length will be 24 inches")));

        ViewInteraction appCompatEditText5 = onView(
                allOf(withId(R.id.heightInput), withText("60"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText5.perform(click());

        ViewInteraction appCompatEditText6 = onView(
                allOf(withId(R.id.heightInput), withText("60"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText6.perform(replaceText("84"));

        ViewInteraction appCompatEditText7 = onView(
                allOf(withId(R.id.heightInput), withText("84"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText7.perform(closeSoftKeyboard());

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.updateHeight), withText("Update Height"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        appCompatButton4.perform(click());

        ViewInteraction editText6 = onView(
                allOf(withId(R.id.heightInput), withText("84"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        editText6.check(matches(withText("84")));

        ViewInteraction textView6 = onView(
                allOf(withId(R.id.enteredHeight), withText("Your stride length will be 34 inches"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        textView6.check(matches(withText("Your stride length will be 34 inches")));

        pressBack();

        ViewInteraction appCompatButton5 = onView(
                allOf(withId(R.id.updateStep), withText("Update Step Goal"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        appCompatButton5.perform(click());

        ViewInteraction editText7 = onView(
                allOf(withId(R.id.stepInput), withText("Step Goal"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                5),
                        isDisplayed()));
        editText7.check(matches(withText("Step Goal")));

        ViewInteraction textView7 = onView(
                allOf(withId(R.id.enteredStep), withText("Please enter a new step goal of at least 2 :)"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                6),
                        isDisplayed()));
        textView7.check(matches(withText("Please enter a new step goal of at least 2 :)")));

        ViewInteraction appCompatEditText8 = onView(
                allOf(withId(R.id.stepInput),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText8.perform(replaceText("1"), closeSoftKeyboard());

        pressBack();

        ViewInteraction appCompatButton6 = onView(
                allOf(withId(R.id.updateStep), withText("Update Step Goal"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        appCompatButton6.perform(click());

        ViewInteraction editText8 = onView(
                allOf(withId(R.id.stepInput), withText("1"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                5),
                        isDisplayed()));
        editText8.check(matches(withText("1")));

        ViewInteraction textView8 = onView(
                allOf(withId(R.id.enteredStep), withText("Please enter a new step goal of at least 2 :)"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                6),
                        isDisplayed()));
        textView8.check(matches(withText("Please enter a new step goal of at least 2 :)")));

        ViewInteraction appCompatEditText9 = onView(
                allOf(withId(R.id.stepInput), withText("1"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText9.perform(replaceText("5000"));

        ViewInteraction appCompatEditText10 = onView(
                allOf(withId(R.id.stepInput), withText("5000"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText10.perform(closeSoftKeyboard());

        ViewInteraction appCompatButton7 = onView(
                allOf(withId(R.id.updateStep), withText("Update Step Goal"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        appCompatButton7.perform(click());

        ViewInteraction editText9 = onView(
                allOf(withId(R.id.stepInput), withText("5000"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                5),
                        isDisplayed()));
        editText9.check(matches(withText("5000")));

        ViewInteraction textView9 = onView(
                allOf(withId(R.id.enteredStep), withText("Congrats! Your new step goal is 5000 steps"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                6),
                        isDisplayed()));
        textView9.check(matches(withText("Congrats! Your new step goal is 5000 steps")));

        ViewInteraction appCompatEditText11 = onView(
                allOf(withId(R.id.stepInput), withText("5000"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText11.perform(click());

        ViewInteraction appCompatEditText12 = onView(
                allOf(withId(R.id.stepInput), withText("5000"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText12.perform(replaceText("100"));

        ViewInteraction appCompatEditText13 = onView(
                allOf(withId(R.id.stepInput), withText("100"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatEditText13.perform(closeSoftKeyboard());

        pressBack();

        ViewInteraction appCompatButton8 = onView(
                allOf(withId(R.id.updateStep), withText("Update Step Goal"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                3),
                        isDisplayed()));
        appCompatButton8.perform(click());

        ViewInteraction editText10 = onView(
                allOf(withId(R.id.stepInput), withText("100"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                5),
                        isDisplayed()));
        editText10.check(matches(withText("100")));

        ViewInteraction textView10 = onView(
                allOf(withId(R.id.enteredStep), withText("Congrats! Your new step goal is 100 steps"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                6),
                        isDisplayed()));
        textView10.check(matches(withText("Congrats! Your new step goal is 100 steps")));
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
