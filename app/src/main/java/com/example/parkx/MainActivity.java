package com.example.parkx;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView textView;

    EditText email,password;
    Button  login;
    private static final int Location_Permission_Code=100;
    private static final int GPS_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        SupabaseManager.INSTANCE.init();




        login=findViewById(R.id.login_button);

        login.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
            startActivity(intent);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        Location_Permission_Code);
            } else {
                startActivity(new Intent(MainActivity.this, MapsActivity.class));
            }
        });

        checkLocationPermission();
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Ask for permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    Location_Permission_Code);
        } else {
            // Permission already granted
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (!gpsEnabled) {
                Toast.makeText(this, "Please enable GPS", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, GPS_REQUEST_CODE);
            } else {
                // GPS is already on, proceed with location features
                Toast.makeText(this, "GPS is ON", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void SignIn(View view) {
        SupabaseManager.signIn("anothervalid@email.com", "password", new JavaResultCallback<String>() {
            @Override
            public void onSuccess(String value) {
                System.out.println(value);
                textView.setText(value);
            }

            @Override
            public void onError(@NotNull Throwable exception) {
                System.out.println(exception.getMessage());
                textView.setText(exception.getMessage());
            }
        });
    }

    public void SignOut(View view) {
        SupabaseManager.signOut(new JavaResultCallback<String>() {
            @Override
            public void onSuccess(String value) {
                System.out.println(value);
                textView.setText(value);
            }

            @Override
            public void onError(@NotNull Throwable exception) {
                System.out.println(exception.getMessage());
                textView.setText(exception.getMessage());
            }
        });
    }

    public void GetMessages(View view) {
        SupabaseManager.getMessages(new JavaResultCallback<List<String>>() {
            @Override
            public void onSuccess(List<String> value) {
                System.out.println(value);
                textView.setText(value.toString());
            }

            @Override
            public void onError(@NotNull Throwable exception) {
                System.out.println(exception.getMessage());
                textView.setText(exception.getMessage());
            }
        });
    }

    public void Reset(View view) {
        textView.setText("What's your action?");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Location_Permission_Code) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity(new Intent(MainActivity.this, MapsActivity.class));
            } else {
                Toast.makeText(this, "Η άδεια τοποθεσίας είναι απαραίτητη.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}