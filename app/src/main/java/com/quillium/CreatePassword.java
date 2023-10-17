package com.quillium;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textview.MaterialTextView;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Locale;

public class CreatePassword extends AppCompatActivity {
    private MaterialTextView resendOtpTimer;
    private int minutes = 1;
    private int seconds = 0;
    private TextInputLayout studentEmailField;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_password);

        // Initialize the MaterialTextView for the countdown timer
        resendOtpTimer = findViewById(R.id.resend_otp_timer);
        studentEmailField = findViewById(R.id.student_email_field);

        // Initialize the Handler
        handler = new Handler();

        // Start the countdown timer
        final Runnable countdownRunnable = new Runnable() {
            @Override
            public void run() {
                if (minutes >= 0 && seconds >= 0) {
                    // Update the MaterialTextView with the remaining time
                    String timerText = String.format(Locale.getDefault(), "%d:%02d", minutes, seconds);
                    resendOtpTimer.setText(timerText);

                    // Decrement the timer
                    if (seconds == 0) {
                        minutes--;
                        seconds = 59;
                    } else {
                        seconds--;
                    }

                    // Delay the update by 1 second (1000 milliseconds)
                    handler.postDelayed(this, 1000);
                } else {
                    // The timer has reached 0:00
                    // Make textView_resend_otp visible
                    findViewById(R.id.textView_resend_otp).setVisibility(View.VISIBLE);
                    // Hide textView_enter_otp_instruction and resend_otp_timer
                    findViewById(R.id.textView_enter_otp_instruction).setVisibility(View.INVISIBLE);
                    findViewById(R.id.resend_otp_timer).setVisibility(View.INVISIBLE);

                    // Update android:layout_below for student_email_field to "textView_resend_otp"
                    setStudentEmailFieldLayoutBelow(R.id.textView_resend_otp);
                }
            }
        };

        // Start the initial countdown
        handler.post(countdownRunnable);

        // Add an OnClickListener for the "Resend OTP" TextView
        TextView resendOtp = findViewById(R.id.textView_resend_otp);
        resendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Reset the countdown timer values
                minutes = 1;
                seconds = 0;

                // Make textView_enter_otp_instruction and resend_otp_timer visible
                findViewById(R.id.textView_enter_otp_instruction).setVisibility(View.VISIBLE);
                findViewById(R.id.resend_otp_timer).setVisibility(View.VISIBLE);

                // Make textView_resend_otp invisible
                findViewById(R.id.textView_resend_otp).setVisibility(View.INVISIBLE);

                // Reset the android:layout_below for student_email_field to "resend_otp_timer"
                setStudentEmailFieldLayoutBelow(R.id.resend_otp_timer);

                // Restart the countdown timer
                handler.removeCallbacksAndMessages(null);
                handler.post(countdownRunnable);
            }
        });

        // Rest of your code for the "Create Account" button and progress indicator
        Button CreateAccount = findViewById(R.id.button_create_account);
        CreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hide the "Create Account" button
                CreateAccount.setVisibility(View.INVISIBLE);

                // Show the circular progress indicator
                CircularProgressIndicator circularLoading = findViewById(R.id.circularLoading);
                circularLoading.setVisibility(View.VISIBLE);

                // Simulate a 2-second delay using a Handler
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Start the next activity (MainActivity) after the initial 1.5-second delay
                        Intent intent = new Intent(CreatePassword.this, MainActivity.class);
                        startActivity(intent);

                        // After an additional 0.5 seconds, make the "Create Account" button visible again
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                CreateAccount.setVisibility(View.VISIBLE);
                                circularLoading.setVisibility(View.INVISIBLE);
                            }
                        }, 500); // 500 milliseconds = 0.5 seconds
                    }
                }, 1500); // 1500 milliseconds = 1.5 seconds
            }
        });
    }

    private void setStudentEmailFieldLayoutBelow(int viewId) {
        if (studentEmailField.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) studentEmailField.getLayoutParams();
            params.addRule(RelativeLayout.BELOW, viewId);
            studentEmailField.setLayoutParams(params);
        }
    }
}