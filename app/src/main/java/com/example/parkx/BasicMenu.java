package com.example.parkx;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentContainer;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentTransaction;

public class BasicMenu extends AppCompatActivity{

    Button searchButton,profile,AddParking,requests_button;
  FragmentContainerView fragmentContainer;

    TextView textView;
    private static final int GPS_REQUEST_CODE = 101;
    private static final int Location_Permission_Code=100;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        textView=findViewById(R.id.textView);
        searchButton=findViewById(R.id.Search_button);
        requests_button=findViewById(R.id.Requests_button);
        fragmentContainer=findViewById(R.id.requests_container);

        searchButton.setOnClickListener(view -> {
            //startActivity(new Intent(BasicMenu.this,MapsActivity.class));
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        Location_Permission_Code);
            } else {
                //Toast.makeText(this, "Aπαραίτητο GPS ανοιχτό.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(BasicMenu.this, MapsActivity.class));
            }
        });

        requests_button.setOnClickListener( view -> {

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

// To replace the current fragment with the RequestsFragment
            transaction.replace(R.id.requests_container, new RequestsFragment());

// Add it to the back stack if you want to allow navigating back
            transaction.addToBackStack(null);

// Commit the transaction
            transaction.commit();



        });

    }

    /// Απαραίτητη άδεια χρήσης GPS χρήστη
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Location_Permission_Code) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity(new Intent(BasicMenu.this, MapsActivity.class));
            } else {
                Toast.makeText(this, "Η άδεια τοποθεσίας είναι απαραίτητη.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
