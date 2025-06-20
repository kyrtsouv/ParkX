package com.example.parkx;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity implements MapFragment.ToggleFullscreenListener {
    private Fragment selectedFragment = new SearchMapFragment();
    private BottomNavigationView bottomNavigationView;
    private boolean isFullscreen = false;

    // This activity holds the fragments of the home screen
    // It initializes the BottomNavigationView and handles the navigation between the fragments
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            //If pressed loads the PostMapFragment
            if (item.getItemId() == R.id.miPost && !(selectedFragment instanceof PostMapFragment))
                selectedFragment = new PostMapFragment();
            //If pressed loads the SearchMapFragment
            if (item.getItemId() == R.id.miSearch && !(selectedFragment instanceof SearchMapFragment))
                selectedFragment = new SearchMapFragment();
            //If pressed loads the SearchMapFragment
            if (item.getItemId() == R.id.miProfile && !(selectedFragment instanceof RequestsFragment))
                selectedFragment = new RequestsFragment();

            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView2, selectedFragment).commit();

            return true;
        });
    }

    //It saves whether the app is in fullscreen mode or not
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isFullscreen", isFullscreen);
    }

    //It restores the saved state and if the app was in fullscreen it hides the bottom navigation view
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isFullscreen = savedInstanceState.getBoolean("isFullscreen", false);
        bottomNavigationView.post(() -> {
            if (isFullscreen) {
                hideBottomNavigationView(0);
            } else {
                showBottomNavigationView(0);
            }
        });

    }


    private void hideBottomNavigationView(int duration) {
        bottomNavigationView.animate().translationY(bottomNavigationView.getHeight()).setDuration(duration).start();
    }


    private void showBottomNavigationView(int duration) {
        bottomNavigationView.animate().translationY(0).setDuration(duration).start();
    }


    @Override
    public void toggleFullscreen() {
        isFullscreen = !isFullscreen;
        if (isFullscreen) hideBottomNavigationView(200);
        else showBottomNavigationView(200);
    }
}