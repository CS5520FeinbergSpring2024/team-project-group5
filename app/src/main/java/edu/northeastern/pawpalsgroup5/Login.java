package edu.northeastern.pawpalsgroup5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class Login extends AppCompatActivity {
    FirebaseAuth myAuth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText emailEditText = findViewById(R.id.emailEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        Button loginButton = findViewById(R.id.loginButton);
        TextView registerTextLinkView = findViewById(R.id.registerLinkTextView);

        loginButton.setOnClickListener(view -> {

            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(Login.this, "Username and password cannot be empty.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!validateEmail(email)) {
                emailEditText.setError("Invalid email format");
                Toast.makeText(getApplicationContext(), "Invalid email format", Toast.LENGTH_SHORT).show();
                return;
            }
            myAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, navigate to Main Activity
                            Intent intent = new Intent(Login.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, show the user authentication failed
                            Toast.makeText(Login.this, "Authentication failed. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });

        });

        registerTextLinkView.setOnClickListener(view -> {
            Intent intent = new Intent(Login.this, Register.class);
            startActivity(intent);
        });
    }
    private boolean validateEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        Pattern pattern = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pattern.matcher(email).matches();
    }

    private boolean validatePassword(String password) {
        if (password.length() < 6) {
            Toast.makeText(Login.this, "Password must be at least 6 characters.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}