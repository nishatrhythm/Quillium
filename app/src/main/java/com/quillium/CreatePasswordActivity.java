package com.quillium;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.quillium.utils.Constants;
import com.quillium.utils.PreferenceManager;

import java.util.HashMap;
import java.util.Locale;

public class CreatePasswordActivity extends AppCompatActivity {
    String name, email, id, department;
    MaterialTextView studentName, studentId, departmentName;
    private TextInputEditText password, confirmPassword;
    CircularProgressIndicator circularLoading;
    MaterialButton createAccount;
    String pass, confirmPass;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_password);

        firebaseAuth = FirebaseAuth.getInstance();

        preferenceManager = new PreferenceManager(getApplicationContext());

        // Retrieve the data from the intent
        Intent intent = getIntent();
        if (intent != null) {
            name = intent.getStringExtra("fullName1"); // Replace "key" with the same key used in SenderActivity
            email = intent.getStringExtra("email");
            id = intent.getStringExtra("id");
            department = intent.getStringExtra("dept");
        }

        studentName = findViewById(R.id.student_name);
        studentId = findViewById(R.id.student_id_textView);
        departmentName = findViewById(R.id.department_name);
        circularLoading = findViewById(R.id.circularLoading);
        createAccount = findViewById(R.id.button_create_account);
        password = findViewById(R.id.create_password);
        confirmPassword = findViewById(R.id.confirm_password);

        studentName.setText(name);
        studentId.setText(id);
        departmentName.setText(department);


        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                createAccount.setVisibility(View.INVISIBLE);
                circularLoading.setVisibility(View.VISIBLE);

                pass = password.getText().toString().trim();
                confirmPass = confirmPassword.getText().toString().trim();

                if (pass.isEmpty() || confirmPass.isEmpty()) {
                    // Show toast message for empty password fields
                    createAccount.setVisibility(View.VISIBLE);
                    circularLoading.setVisibility(View.INVISIBLE);
                    Toast.makeText(CreatePasswordActivity.this, "Please enter both password and confirm password.", Toast.LENGTH_SHORT).show();
                } else if (!pass.equals(confirmPass)) {
                    // Show toast message for password mismatch
                    createAccount.setVisibility(View.VISIBLE);
                    circularLoading.setVisibility(View.INVISIBLE);
                    Toast.makeText(CreatePasswordActivity.this, "Passwords did not match. Please Check Again.", Toast.LENGTH_SHORT).show();
                } else if (pass.length() < 8 || !containsUppercase(pass) || !containsSpecialCharacter(pass)) {
                    // Show toast message for password not meeting requirements
                    createAccount.setVisibility(View.VISIBLE);
                    circularLoading.setVisibility(View.INVISIBLE);
                    Toast.makeText(CreatePasswordActivity.this, "Please check the password criteria.", Toast.LENGTH_SHORT).show();
                } else {
                    // All checks passed, register the user
                    registerUser(name, email, pass, id, department);
                }

            }
        });
    }

    public boolean containsUppercase(String password) {
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsSpecialCharacter(String password) {
        String specialCharacters = "!@#$%^&*()-+";
        for (char c : password.toCharArray()) {
            if (specialCharacters.contains(String.valueOf(c))) {
                return true;
            }
        }
        return false;
    }

    private void addUserRegisterToFirestore(String name, String email, String pass, String id, String department) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        HashMap<String, Object> user = new HashMap<>();
        user.put(Constants.KEY_NAME, name);
        user.put(Constants.KEY_EMAIL, email);
        user.put(Constants.KEY_PASSWORD, pass);
        user.put(Constants.KEY_STUDENT_ID, id);
        user.put(Constants.KEY_DEPARTMENT, department);
        user.put(Constants.KEY_VERIFY, false);
        firestore.collection(Constants.KEY_COLLECTION_USERS)
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                    preferenceManager.putString(Constants.KEY_USER_ID, documentReference.getId());
                    preferenceManager.putString(Constants.KEY_NAME, name);
                    preferenceManager.putString(Constants.KEY_STUDENT_ID, id);
                    preferenceManager.putString(Constants.KEY_DEPARTMENT, department);
//                    Toast.makeText(CreatePasswordActivity.this, "Data Inserted to Firestore", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(exception -> {
                    Toast.makeText(CreatePasswordActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void registerUser(String name, String email, String pass, String id, String department) {
        firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this, new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {

                    firebaseAuth.getCurrentUser().sendEmailVerification()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    Toast.makeText(CreatePasswordActivity.this, "Account creation successful. Please verify your email first.", Toast.LENGTH_LONG).show();

                                    // Add your logic to navigate to the next activity or perform other actions
                                    createAccount.setVisibility(View.VISIBLE);
                                    circularLoading.setVisibility(View.INVISIBLE);

                                    addUserRegisterToFirestore(name, email, pass, id, department);
                                    dataInsertIntoDatabase();
                                    Intent intent = new Intent(CreatePasswordActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "Sorry! Registration was unsuccessful. Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });

                } else {
                    // If registration fails, display a message to the user.
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        createAccount.setVisibility(View.VISIBLE);
                        circularLoading.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), "User is already registered. Please try with a different email and password.", Toast.LENGTH_LONG).show();

                    } else {
                        createAccount.setVisibility(View.VISIBLE);
                        circularLoading.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), "Sorry! Registration was unsuccessful. Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    void dataInsertIntoDatabase() {
        // Registration successful
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");

        String userId = firebaseAuth.getCurrentUser().getUid();
        User user = new User(name, email, id, department);

        // Save user data to Realtime Database
        if (!TextUtils.isEmpty(name)) {
            // Save user data to Realtime Database
            databaseReference.child(userId).setValue(user);
        }
    }
}