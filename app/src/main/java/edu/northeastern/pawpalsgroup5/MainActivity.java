package edu.northeastern.pawpalsgroup5;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(navListener);

        // Load the default PlaceholderFragment when the activity is first created
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, PlaceholderFragment.newInstance(1)).commit();
        }
    }

    private final NavigationBarView.OnItemSelectedListener navListener = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;

//            switch (item.getItemId()) {
//                case R.id.nav_home:
//                    selectedFragment = PlaceholderFragment.newInstance(1);
//                    break;
//                case R.id.nav_following:
//                    selectedFragment = PlaceholderFragment.newInstance(2);
//                    break;
//                case R.id.nav_post:
//                    selectedFragment = PlaceholderFragment.newInstance(3);
//                    break;
//                case R.id.nav_me:
//                    selectedFragment = PlaceholderFragment.newInstance(4);
//                    break;
//                case R.id.nav_settings:
//                    selectedFragment = PlaceholderFragment.newInstance(5);
//                    break;
//            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            }

            return true; // Indicate that the item selection has been handled
        }
    };
}
