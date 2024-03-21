package edu.northeastern.pawpalsgroup5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText usernameEditText = findViewById(R.id.usernameEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        Button loginButton = findViewById(R.id.loginButton);
        TextView registerTextLinkView = findViewById(R.id.registerLinkTextView);

        loginButton.setOnClickListener(view -> {
            // TODO: add authentication logic here before navigating to main
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(Login.this, "Username and password cannot be empty.", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
        });

        registerTextLinkView.setOnClickListener(view -> {
            Intent intent = new Intent(Login.this, Register.class);
            startActivity(intent);
        });
    }
}