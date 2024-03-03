package com.quillium;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;

public class VerifyEmailActivity extends AppCompatActivity {

    String receivedValue1;
    String receivedValue2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);

        // Retrieve the data from the intent
        Intent intent = getIntent();
        if (intent != null) {
            String data = intent.getStringExtra("key"); // Replace "key" with the same key used in SenderActivity
            if (data != null) {
                // Do something with the received data
            }
        }

        TextView studentId = findViewById(R.id.student_id_textView);

        studentId.setText(receivedValue1);


        TextInputEditText studentIdEditText = findViewById(R.id.student_email);

        studentIdEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // Set the dynamic hint text inside the EditText when it gains focus
                    studentIdEditText.setHint("b190305003@cse.jnu.ac.bd");

                    // Request focus programmatically to show the keyboard
                    studentIdEditText.requestFocus();

                    // Show the keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(studentIdEditText, InputMethodManager.SHOW_IMPLICIT);
                } else {
                    // Clear the hint text when the EditText loses focus
                    studentIdEditText.setHint("");
                }
            }
        });

        // open otp entry field and create password activity
        Button SendOTP = findViewById(R.id.button_send_otp);
        SendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hide the "SendOTP" button
                SendOTP.setVisibility(View.INVISIBLE);

                // Show the circular progress indicator
                CircularProgressIndicator circularLoading = findViewById(R.id.circularLoading);
                circularLoading.setVisibility(View.VISIBLE);

                // Simulate a 2-second delay using a Handler
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Start the next activity (VerifyEmailActivity) after the initial 2-second delay
                        Intent intent = new Intent(VerifyEmailActivity.this, CreatePasswordActivity.class);
                        startActivity(intent);

                        // After an additional 0.2 or 0.3 seconds, make the "Verify" button visible again
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                SendOTP.setVisibility(View.VISIBLE);
                                circularLoading.setVisibility(View.INVISIBLE);
                            }
                        }, 500); // 500 milliseconds = 0.5 seconds
                    }
                }, 1500); // 1500 milliseconds = 1.5 seconds
            }
        });
    }
}