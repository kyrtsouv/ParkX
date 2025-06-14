package com.example.parkx;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.parkx.supabase.SupabaseManager;

public class MainActivity extends AppCompatActivity implements HomeListener {
    private FragmentManager ftMan;

    // This activity holds the fragments of sign in and sign up
    // It initializes the Kotlin SupabaseManager which is used for the communication with the Supabase backend
    // and handles the navigation between the fragments and the home activity when the user is authenticated
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupabaseManager.init();
        ftMan = getSupportFragmentManager();
    }


    public void goToSignIn(View view) {
        ftMan.beginTransaction().replace(R.id.fragmentContainerView, new SignInFragment()).commit();
    }


    public void goToSignUp(View view) {
        ftMan.beginTransaction().replace(R.id.fragmentContainerView, new SignUpFragment()).commit();
    }


    public void goToHome() {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}