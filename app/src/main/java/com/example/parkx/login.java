package com.example.parkx;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.parkx.supabase.SupabaseManager;
import com.example.parkx.utils.JavaResultCallback;
import com.example.parkx.utils.ParkingSpot;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class login extends Fragment {

TextView textView;
        public login() {
            // Required empty constructor
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater,
                                 @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            // Inflate login fragment layout (create this XML next)
            return inflater.inflate(R.layout.fragment_login, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            Button loginButton = view.findViewById(R.id.login_button);
            loginButton.setOnClickListener(v -> {
                // Navigate to BasicMenu activity or another fragment
                Intent intent = new Intent(getActivity(), BasicMenu.class);
                startActivity(intent);
            });

            Button registerButton = view.findViewById(R.id.register_button);
            registerButton.setOnClickListener(v -> {
                // Replace this fragment with RequestsFragment
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new RequestsFragment())
                        .addToBackStack(null)  // allow going back to login
                        .commit();
            });

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



