package com.example.parkx;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.parkx.supabase.SupabaseManager;

public class MainActivity extends AppCompatActivity {

    private FragmentManager ftMan;

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
    }
}

//            SupabaseManager.signIn("anothertest@email.com", "password", new JavaResultCallback<String>() {
//                @Override
//                public void onSuccess(String value) {
//                    System.out.println(value);
//
//                    SupabaseManager.getSpots(40.0, -40.0, new JavaResultCallback<List<ParkingSpot>>() {
//                        @Override
//                        public void onSuccess(List<ParkingSpot> value) {
//
//                            for (ParkingSpot spot : value) {
//                                System.out.println(spot.getId());
//                                System.out.println(spot.getLat());
//                                System.out.println(spot.getLong());
//                            }
//
//                            SupabaseManager.publishSpot("POINT(-40 40)", new JavaResultCallback<String>() {
//                                @Override
//                                public void onSuccess(String value) {
//                                    System.out.println(value);
//                                }
//
//                                @Override
//                                public void onError(@NotNull Throwable exception) {
//                                    System.out.println(exception.getMessage());
//                                }
//                            });
//                        }
//
//                        @Override
//                        public void onError(@NotNull Throwable exception) {
//                            System.out.println(exception.getMessage());
//                        }
//                    });
//                }
//
//                @Override
//                public void onError(@NotNull Throwable exception) {
//                    System.out.println(exception.getMessage());
//                    textView.setText(exception.getMessage());
//                }
//            });
