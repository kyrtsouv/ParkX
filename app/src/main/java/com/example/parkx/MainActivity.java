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

import com.example.parkx.supabase.SupabaseManager;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView textView;

    EditText email,password;
    Button  login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        SupabaseManager.INSTANCE.init();

        login=findViewById(R.id.login_button);
        /// Αυτο πρεπει να φυγει !!!
        login.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, BasicMenu.class);
            startActivity(intent);
            /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        Location_Permission_Code);
            } else {
                startActivity(new Intent(MainActivity.this, MapsActivity.class));
            }*/
        });

        textView = findViewById(R.id.error_log);

        SupabaseManager.signIn("test@email.com", "password", new JavaResultCallback<String>() {
            @Override
            public void onSuccess(String value) {
                System.out.println(value);
                textView.setText(value);

                SupabaseManager.getSpots(new JavaResultCallback<List<ParkingSpot>>() {
                    @Override
                    public void onSuccess(List<ParkingSpot> value) {
                        System.out.println(value);
                        textView.append(value.toString());
                    }

                    @Override
                    public void onError(@NotNull Throwable exception) {
                        textView.append(exception.getMessage());
                    }
                });

            }

            @Override
            public void onError(@NotNull Throwable exception) {
                System.out.println(exception.getMessage());
                textView.setText(exception.getMessage());
            }
        });

    }

    public void SignIn(View view) {
        SupabaseManager.signIn("test@email.com", "password", new JavaResultCallback<String>() {
            @Override
            public void onSuccess(String value) {
                System.out.println(value);
                textView.setText(value);

                String md = SupabaseManager.getMetadata();
                System.out.println(md);
                textView.setText(md);

                /// επιτυχής σύνδεση οδηγεί στο Βασικό μενού της Εφαρμογής
                //startActivity(new Intent(MainActivity.this, BasicMenu.class));
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

    public void SignUp(View view) {
        SupabaseManager.signUp("anothertest@email.com", "password", new JavaResultCallback<String>() {
            @Override
            public void onSuccess(String value) {
                System.out.println(value);
                textView.setText(value);
            }

            @Override
            public void onError(@NotNull Throwable exception) {
                String result = exception.toString();
                System.out.println(result);
                textView.setText(result);
            }
        });
    }
}