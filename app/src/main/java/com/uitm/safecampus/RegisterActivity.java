package com.uitm.safecampus;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth; // <<< IMPORT FIREBASE AUTH
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etRegName, etRegPassword, etRegConfirmPass;
    private Button btnRegister;
    private TextView tvLoginLink;

    private FirebaseAuth mAuth; // <<< DECLARE FIREBASE AUTH VARIABLE


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        // --- INITIALIZE FIREBASE AUTH ---
        mAuth = FirebaseAuth.getInstance();


        etRegName = findViewById(R.id.etRegName);
        etRegPassword = findViewById(R.id.etRegPassword);
        etRegConfirmPass = findViewById(R.id.etRegConfirmPass);
        btnRegister = findViewById(R.id.btnRegister);
        tvLoginLink = findViewById(R.id.tvLoginLink);

        btnRegister.setOnClickListener(v -> {
            String email = etRegName.getText().toString().trim();
            String password = etRegPassword.getText().toString().trim();
            String confirmPass = etRegConfirmPass.getText().toString().trim();

            // --- VALIDATION LOGIC ---
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPass)) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return; // Stop the function here
            }

            if (!password.equals(confirmPass)) {
                Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                return; // Stop the function here
            }

            // Firebase requires passwords to be at least 6 characters
            if (password.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            // --- FIREBASE REGISTRATION LOGIC ---
            // Replaces the SharedPreferences code
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Registration success
                            Log.d("AUTH", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            Toast.makeText(this, "Account Created Successfully!", Toast.LENGTH_SHORT).show();

                            // Navigate to the main part of the app (e.g., DashboardActivity)
                            Intent intent = new Intent(RegisterActivity.this, DashboardActivity.class);
                            // Clear the back stack so the user can't go back to the register screen
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();

                        } else {
                            // If registration fails, display a message to the user.
                            Log.w("AUTH", "createUserWithEmail:failure", task.getException());
                            // Common errors: email already in use, weak password, no internet
                            Toast.makeText(this, "Authentication failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        });

        // Your link to the login screen is correct
        tvLoginLink.setOnClickListener(v -> {
            // This takes the user back to the previous screen (presumably LoginActivity)
            finish();
        });


    }
}