package com.quillium;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {

    EditText date_of_birth;
    DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        date_of_birth = findViewById(R.id.date_of_birth);
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        date_of_birth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePicker(); // Call the openDatePicker method when the EditText is clicked
            }
        });

        TextInputEditText studentIdEditText = findViewById(R.id.student_id);

        studentIdEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // Set the dynamic hint text inside the EditText when it gains focus
                    studentIdEditText.setHint("(e.g: B190305001)");

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

        TextInputEditText studentHscRollEditText = findViewById(R.id.student_hsc_roll);

        studentHscRollEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // Set the dynamic hint text inside the EditText when it gains focus
                    studentHscRollEditText.setHint("(e.g: 123456)");

                    // Request focus programmatically to show the keyboard
                    studentHscRollEditText.requestFocus();

                    // Show the keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(studentHscRollEditText, InputMethodManager.SHOW_IMPLICIT);
                } else {
                    // Clear the hint text when the EditText loses focus
                    studentHscRollEditText.setHint("");
                }
            }
        });



        // open login activity
        TextView Login = findViewById(R.id.textView_login);
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // open verified data overview and verify email activity
        Button Verify = findViewById(R.id.button_verify);
        Verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hide the "Verify" button
                Verify.setVisibility(View.INVISIBLE);

                // Show the circular progress indicator
                CircularProgressIndicator circularLoading = findViewById(R.id.circularLoading);
                circularLoading.setVisibility(View.VISIBLE);

                // Simulate a 1.5-second delay using a Handler
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Start the next activity (VerifyEmailActivity) after the initial 1.5-second delay
                        Intent intent = new Intent(RegisterActivity.this, VerifyEmailActivity.class);
                        startActivity(intent);

                        // After an additional 0.5 seconds, make the "Verify" button visible again
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Verify.setVisibility(View.VISIBLE);
                                circularLoading.setVisibility(View.INVISIBLE);
                            }
                        }, 500); // 500 milliseconds = 0.5 seconds
                    }
                }, 1500); // 1500 milliseconds = 1.5 seconds
            }
        });
    }

    // Define the openDatePicker method to open the date picker dialog
    private void openDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        // Set the minimum date to January 1, 1980
        int minYear = 1980;
        int minMonth = 0; // January (months are 0-indexed)
        int minDay = 1;

        datePickerDialog = new DatePickerDialog(
                RegisterActivity.this,
                R.style.CustomDatePickerDialogTheme, // Apply the custom theme here
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        date_of_birth.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                },
                currentYear, currentMonth, currentDay
        );

        Calendar minDate = Calendar.getInstance();
        minDate.set(minYear, minMonth, minDay);

        // Set the minimum date
        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());

        // Set the maximum date to the current date
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        datePickerDialog.show();
    }
}