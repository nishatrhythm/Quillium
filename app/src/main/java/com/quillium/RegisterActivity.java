package com.quillium;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private Button verifyButton;
    private TextInputEditText studentFullNameEditText, studentEmailEditText;
    private TextView loginTextView;
    CircularProgressIndicator circularLoading;
    String std_id = null;
    String dept_code = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        studentFullNameEditText = findViewById(R.id.student_id);
        studentEmailEditText = findViewById(R.id.student_hsc_roll);
        loginTextView = findViewById(R.id.textView_login);
        verifyButton = findViewById(R.id.button_verify);
        circularLoading = findViewById(R.id.circularLoading);

//        dateOfBirthButton.setOnClickListener(view -> openDatePicker());

        // Set dynamic hints and show keyboard when EditTexts gain focus
//        setEditTextFocusListener(studentFullNameEditText, "(e.g: John Doe)");
//        setEditTextFocusListener(studentEmailEditText, "(e.g: b190305034@cse.jnu.ac.bd)");

        loginTextView.setOnClickListener(view -> openLoginActivity());

        verifyButton.setOnClickListener(view -> verifyStudentDetails());
    }

    private void setEditTextFocusListener(TextInputEditText editText, String hint) {
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                editText.setHint(hint);
                editText.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            } else {
                editText.setHint("");
            }
        });
    }

    private void openLoginActivity() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }

//    private void openDatePicker() {
//        Calendar calendar = Calendar.getInstance();
//        DatePickerDialog datePickerDialog = new DatePickerDialog(
//                RegisterActivity.this,
//                R.style.CustomDatePickerDialogTheme, // Apply the custom theme here
//                (view, year, month, dayOfMonth) -> {
//                    selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
//                    dateOfBirthButton.setText("    " + selectedDate);
//                },
//                calendar.get(Calendar.YEAR),
//                calendar.get(Calendar.MONTH),
//                calendar.get(Calendar.DAY_OF_MONTH)
//        );
//
//        // Set the minimum date to January 1, 1980
//        Calendar minDate = Calendar.getInstance();
//        minDate.set(1980, 0, 1);
//        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
//
//        // Set the maximum date to the current date
//        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
//
//        datePickerDialog.show();
//    }


    private void verifyStudentDetails() {
        // Show the circular progress indicator
        circularLoading.setVisibility(View.VISIBLE);
        verifyButton.setVisibility(View.INVISIBLE);

        String name = studentFullNameEditText.getText().toString().trim();
        String email = studentEmailEditText.getText().toString().trim();

        if (!email.isEmpty() && !name.isEmpty()) {

            // Define the pattern for extracting student ID and department code
            Pattern pattern = Pattern.compile("([a-zA-Z0-9]+)@([a-zA-Z]+)\\.jnu\\.ac\\.bd");
            Matcher matcher = pattern.matcher(email);
            if (matcher.find()) {
                std_id = matcher.group(1).toUpperCase(); // Student ID
                if (matcher.group(2).equals("bangla")) {
                    dept_code = "Bangla";
                } else if (matcher.group(2).equals("eng")) {
                    dept_code = "English";
                } else if (matcher.group(2).equals("his")) {
                    dept_code = "History";
                } else if (matcher.group(2).equals("phi")) {
                    dept_code = "Philosophy";
                } else if (matcher.group(2).equals("ihc")) {
                    dept_code = "Islamic History and Culture";
                } else if (matcher.group(2).equals("is")) {
                    dept_code = "Islamic Studies";
                } else if (matcher.group(2).equals("mus")) {
                    dept_code = "Music";
                } else if (matcher.group(2).equals("theatre")) {
                    dept_code = "Theatre";
                } else if (matcher.group(2).equals("phy")) {
                    dept_code = "Physics";
                } else if (matcher.group(2).equals("math")) {
                    dept_code = "Mathematics";
                } else if (matcher.group(2).equals("chem")) {
                    dept_code = "Chemistry";
                } else if (matcher.group(2).equals("stat")) {
                    dept_code = "Statistics";
                } else if (matcher.group(2).equals("cse")) {
                    dept_code = "Computer Science and Engineering";
                } else if (matcher.group(2).equals("eco")) {
                    dept_code = "Economics";
                } else if (matcher.group(2).equals("pol")) {
                    dept_code = "Political Science";
                } else if (matcher.group(2).equals("sociology")) {
                    dept_code = "Sociology";
                } else if (matcher.group(2).equals("sw")) {
                    dept_code = "Social Work";
                } else if (matcher.group(2).equals("anp")) {
                    dept_code = "Anthropology";
                } else if (matcher.group(2).equals("mcj")) {
                    dept_code = "Mass Communication and Journalism";
                } else if (matcher.group(2).equals("pad")) {
                    dept_code = "Public Administration";
                } else if (matcher.group(2).equals("ais")) {
                    dept_code = "Accounting & Information System";
                } else if (matcher.group(2).equals("mgt")) {
                    dept_code = "Management Studies";
                } else if (matcher.group(2).equals("fin")) {
                    dept_code = "Finance";
                } else if (matcher.group(2).equals("mkt")) {
                    dept_code = "Marketing";
                } else if (matcher.group(2).equals("law")) {
                    dept_code = "Law";
                } else if (matcher.group(2).equals("land")) {
                    dept_code = "Land Management and Law";
                } else if (matcher.group(2).equals("geography")) {
                    dept_code = "Geography & Environment";
                } else if (matcher.group(2).equals("bot")) {
                    dept_code = "Botany";
                } else if (matcher.group(2).equals("zool")) {
                    dept_code = "Zoology";
                } else if (matcher.group(2).equals("psy")) {
                    dept_code = "Psychology";
                } else if (matcher.group(2).equals("pharm")) {
                    dept_code = "Pharmacy";
                } else if (matcher.group(2).equals("mib")) {
                    dept_code = "Microbiology";
                } else if (matcher.group(2).equals("bmb")) {
                    dept_code = "Biochemistry & Molecular Biology";
                } else if (matcher.group(2).equals("geb")) {
                    dept_code = "Genetic Engineering and Biotechnology";
                }

                // Implement your verification logic here
                verifyButton.setVisibility(View.VISIBLE);
                circularLoading.setVisibility(View.INVISIBLE);

                Intent intent = new Intent(RegisterActivity.this, CreatePasswordActivity.class);
                intent.putExtra("fullName1", name); // Replace "key" with your desired key and "value" with the data you want to pass
                intent.putExtra("email", email); // Replace "key" with your desired key and "value" with the data you want to pass
                intent.putExtra("id", std_id); // Replace "key" with your desired key and "value" with the data you want to pass
                intent.putExtra("dept", dept_code); // Replace "key" with your desired key and "value" with the data you want to pass
                startActivity(intent);
                finish();
            } else {
                verifyButton.setVisibility(View.VISIBLE);
                circularLoading.setVisibility(View.INVISIBLE);
                Toast.makeText(RegisterActivity.this, "This email address is not associated with Jagannath University.", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(RegisterActivity.this, "Please fill out all the fields.", Toast.LENGTH_SHORT).show();
            verifyButton.setVisibility(View.VISIBLE);
            circularLoading.setVisibility(View.INVISIBLE);
        }
    }
}