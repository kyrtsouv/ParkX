package com.example.parkx;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);

        SupabaseManager.INSTANCE.init();


        textView.setText("What's your action?");
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
}