package com.example.parkx;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private Fragment firstSelectedFragment = new Search_Map_Fragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(item -> {

            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.miPost &&
                    !(firstSelectedFragment instanceof Post_MapFragment))
                selectedFragment = new Post_MapFragment();

            if (item.getItemId() == R.id.miSearch &&
                    !(firstSelectedFragment instanceof Search_Map_Fragment))
                selectedFragment = new Search_Map_Fragment();

            /*  //Need code here!!!!!!!!!
            if(item.getItemId()==R.id.miProfile ) {

            }*/

            if (selectedFragment!=null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainerView2, selectedFragment)
                        .commit();
                firstSelectedFragment=selectedFragment;
            }else{
                Log.d("mytag","selected == null");
            }

            return true;
        });

    }
}