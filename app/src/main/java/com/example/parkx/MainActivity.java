package com.example.parkx;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.parkx.supabase.SupabaseManager;
import com.example.parkx.utils.JavaResultCallback;
import com.example.parkx.utils.ParkingSpot;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    TextView textView;

    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button loginButton = findViewById(R.id.login_button);
            loginButton.setOnClickListener(v -> {
                // Navigate to BasicMenu activity or another fragment
                Intent intent = new Intent(MainActivity.this, BasicMenu.class);
                startActivity(intent);
            });

            Button registerButton = findViewById(R.id.register_button);


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


        }

        public void SignIn (View view){

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

        public void SignUp (View view){
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

