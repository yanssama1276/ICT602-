package com.uitm.safecampus;

import android.content.Intent;
import android.content.SharedPreferences; // NEW IMPORT
import android.os.Bundle;
import android.text.TextUtils; // Use TextUtils for better checks
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth; // <<< IMPORT FIREBASE AUTH
import com.google.firebase.auth.FirebaseUser; // <<< IMPORT FIREBASE USER


public class MainActivity extends AppCompatActivity {

    private TextInputEditText etName, etPassword;
    private Button btnLogin;
    private TextView tvRegisterLink;
    private FirebaseAuth mAuth; // <<< DECLARE FIREBASE AUTH VARIABLE


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        // <<< FIX: INITIALIZE FIREBASE AUTH HERE >>>
        mAuth = FirebaseAuth.getInstance();

        etName = findViewById(R.id.etName);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegisterLink = findViewById(R.id.tvRegisterLink);

        btnLogin.setOnClickListener(v -> {
            String email = etName.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(MainActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return; // Stop the function
            }

            // --- FIREBASE SIGN-IN LOGIC ---
            // Replaces the SharedPreferences check
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success
                            Log.d("AUTH", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(MainActivity.this, "Welcome back, " + user.getEmail() + "!", Toast.LENGTH_SHORT).show();

                            // Navigate to Dashboard
                            goToDashboard();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("AUTH", "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed. Check your credentials.",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        });

        // Your link to RegisterActivity is correct
        tvRegisterLink.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    // Helper method to navigate to the dashboard
    private void goToDashboard() {
        Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
        // Clear the back stack so the user can't go back to the login screen
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // Finish MainActivity
    }
}
