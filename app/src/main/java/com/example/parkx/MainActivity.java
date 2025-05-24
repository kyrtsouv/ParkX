package com.example.parkx;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.parkx.supabase.SupabaseManager;
import com.example.parkx.utils.JavaResultCallback;
import com.example.parkx.utils.ParkingSpot;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView textView;

    EditText email, password;
    Button login;
    Button register;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        SupabaseManager.INSTANCE.init();


        SupabaseManager.signIn("anothertest@email.com", "password", new JavaResultCallback<String>() {
            @Override
            public void onSuccess(String value) {
                System.out.println(value);

                SupabaseManager.getSpots(40.0, -40.0, new JavaResultCallback<List<ParkingSpot>>() {
                    @Override
                    public void onSuccess(List<ParkingSpot> value) {

                        for (ParkingSpot spot : value) {
                            System.out.println(spot.getId());
                            System.out.println(spot.getLat());
                            System.out.println(spot.getLong());
                        }

                        SupabaseManager.publishSpot("POINT(-40 40)", new JavaResultCallback<String>() {
                            @Override
                            public void onSuccess(String value) {
                                System.out.println(value);
                            }

                            @Override
                            public void onError(@NotNull Throwable exception) {
                                System.out.println(exception.getMessage());
                            }
                        });
                    }

                    @Override
                    public void onError(@NotNull Throwable exception) {
                        System.out.println(exception.getMessage());
                    }
                });
            }

            @Override
            public void onError(@NotNull Throwable exception) {
                System.out.println(exception.getMessage());
                textView.setText(exception.getMessage());
            }
        });


        login = findViewById(R.id.login_button);
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
   register= findViewById(R.id.register_button);
        register.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, Requests.class);
            startActivity(intent);

        });
        textView = findViewById(R.id.error_log);

    }

    public void SignIn(View view) {

        SupabaseManager.signIn("test@email.com", "password", new JavaResultCallback<String>() {
            @Override
            public void onSuccess(String value) {
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