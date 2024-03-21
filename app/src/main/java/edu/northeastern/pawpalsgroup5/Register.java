package edu.northeastern.pawpalsgroup5;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText usernameEditText = findViewById(R.id.usernameEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        Button registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(view -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (!validateUsername(username) || !validatePassword(password)) {
                return;
            }

            // TODO: if frontend validation passes, ensure password is encrypted before sending
            //  to backend for user registration & redirect user to main page
        });
    }


    private boolean validateUsername(String username) {
        if (username.isEmpty()) {
            Toast.makeText(Register.this, "Username cannot be empty.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (username.contains(" ")) {
            Toast.makeText(Register.this, "Username cannot contain spaces.", Toast.LENGTH_SHORT).show();
            return false;
        }
        // TODO: validate if the username already exists in db once backend is ready
        return true;
    }

    private boolean validatePassword(String password) {
        if (password.length() < 6) {
            Toast.makeText(Register.this, "Password must be at least 6 characters.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}