package com.example.team31_personalbest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static java.lang.Integer.parseInt;

public class InputHeight extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_height);
        final EditText height = (EditText)findViewById(R.id.height);
        Button saveButton = (Button)findViewById(R.id.save);

        // back button will be visible after input height
        final Button backButton = (Button)findViewById(R.id.backToMain);
        backButton.setVisibility(View.INVISIBLE);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // save user height to file savedHeight
                SharedPreferences sharePref = getSharedPreferences
                                                ("savedHeight", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharePref.edit();
                editor.putString("height", height.getText().toString());
                editor.apply();
                Toast.makeText(InputHeight.this, "Your stride length is saved",
                                Toast.LENGTH_LONG).show();

                // calculate stride
                String notification = "Your stride length will be " +
                        (int)(0.413 * parseInt(sharePref.getString("height", "0")))
                        + " inches";

                TextView displayFirstName = (TextView)findViewById(R.id.text3);

                // clear current preferences
                editor.clear();
                editor.apply();

                displayFirstName.setText(notification);
                backButton.setVisibility(View.VISIBLE);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchActivity();
            }
        });
    }

    public void launchActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
