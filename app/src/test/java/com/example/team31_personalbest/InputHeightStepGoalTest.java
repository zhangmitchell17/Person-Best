package com.example.team31_personalbest;

import android.content.SharedPreferences;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class InputHeightStepGoalTest {
    private InputHeightStepGoal inputHeightStepGoal;
    private TextView heightPrompt;
    private TextView stepGoalPrompt;
    private TextView enteredHeight;
    private TextView enteredStep;
    private EditText inputHeightText;
    private EditText inputStepGoalText;
    private Button heightButton;
    private Button stepGoalButton;


    @Before
    public void setup() {
        inputHeightStepGoal = Robolectric.setupActivity(InputHeightStepGoal.class);
        heightPrompt = inputHeightStepGoal.findViewById(R.id.textViewHeightPrompt);
        stepGoalPrompt = inputHeightStepGoal.findViewById(R.id.textViewStepPrompt);
        enteredHeight = inputHeightStepGoal.findViewById(R.id.enteredHeight);
        enteredStep = inputHeightStepGoal.findViewById(R.id.enteredStep);
        inputHeightText = inputHeightStepGoal.findViewById(R.id.heightInput);
        inputStepGoalText = inputHeightStepGoal.findViewById(R.id.stepInput);
        heightButton = inputHeightStepGoal.findViewById(R.id.updateHeight);
        stepGoalButton = inputHeightStepGoal.findViewById(R.id.updateStep);
    }

    // Tests if prompts are available and entered values are not yet set to anything
    @Test
    public void testInitial() {
        assertEquals("Enter your height (inches)", heightPrompt.getText());
        assertEquals("Enter your step goal", stepGoalPrompt.getText());
        assertEquals("", enteredStep.getText());
        assertEquals("", enteredHeight.getText());
    }

    // Tests if height is revealed after inputting : - )
    @Test
    public void testInputHeight() {
        // 69 inches => 5' 9"
        inputHeightText.setText("69");
        heightButton.performClick();
        assertEquals("Your stride length will be 28 inches", enteredHeight.getText());
    }

    // Tests if new step goal is revealed after inputting : - )
    @Test
    public void testInputStepGoal() {
        // Sample default step goal like 5000 : - )
        inputStepGoalText.setText("5000");
        stepGoalButton.performClick();
        assertEquals("Congrats! Your new step goal is 5000 steps", enteredStep.getText());
    }
}
