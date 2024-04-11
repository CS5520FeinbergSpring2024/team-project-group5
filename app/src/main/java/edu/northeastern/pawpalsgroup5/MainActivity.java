package edu.northeastern.pawpalsgroup5;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(navListener);

        // Load the default FeedFragment when the activity is first created
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FeedFragment()).commit();
        }

        // Find the chat history button by its ID
        ImageView chatHistoryButton = findViewById(R.id.chatHistoryButton);

        // Set an OnClickListener for the chat history button
        chatHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to start the ChatHistoryActivity
                Intent intent = new Intent(MainActivity.this, ChatHistoryActivity.class); // Modified to ChatHistoryActivity
                startActivity(intent);
            }
        });
    }

    private final NavigationBarView.OnItemSelectedListener navListener = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();
            if (itemId == R.id.nav_me) {
                selectedFragment = new ProfileFragment();
            } else if (itemId == R.id.nav_home) {
                selectedFragment = new FeedFragment();

            } else if (itemId == R.id.nav_following) {
                selectedFragment = new SpotlightFragment();

            } else if(itemId == R.id.nav_post) {
                Intent intent = new Intent(MainActivity.this, PostActivity.class);
                startActivity(intent);
            } else if(itemId == R.id.nav_settings) {
                selectedFragment = new ProfileSetupFragment();

            }
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
                return true;
            }

            return true;
        }
    };

    public void switchToProfileFragment(String userId) {
        ProfileFragment profileFragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString("userId", userId);
        profileFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, profileFragment)
                .commit();

    }
}
