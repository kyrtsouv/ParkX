package com.example.parkx;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.parkx.supabase.SupabaseManager;
import com.example.parkx.utils.JavaResultCallback;

import org.jetbrains.annotations.NotNull;

import kotlin.Unit;

public class SignInFragment extends Fragment {

    EditText et_signInEmail;
    EditText et_signInPassword;

    TextView tv_signInError;


    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("email", et_signInEmail.getText().toString());
        outState.putString("password", et_signInPassword.getText().toString());
        outState.putString("error", tv_signInError.getText().toString());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_in, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        et_signInEmail = view.findViewById(R.id.et_signInEmail);
        et_signInPassword = view.findViewById(R.id.et_signInPassword);
        tv_signInError = view.findViewById(R.id.tv_signInError);

        if (savedInstanceState != null) {
            et_signInEmail.setText(savedInstanceState.getString("email", ""));
            et_signInPassword.setText(savedInstanceState.getString("password", ""));
            tv_signInError.setText(savedInstanceState.getString("error", ""));
        }

        view.findViewById(R.id.btn_signIn).setOnClickListener(v -> SignIn());

    }

    public void SignIn() {

        String email = et_signInEmail.getText().toString();
        String password = et_signInPassword.getText().toString();

        boolean debug = true;
        if (debug) {
            email = "test@email.com";
            password = "password";
        }

        if (email.isEmpty() || password.isEmpty()) {
            tv_signInError.setText(R.string.please_fill_in_all_fields);
            return;
        }

        tv_signInError.setText("");

        SupabaseManager.signIn(email, password, new JavaResultCallback<>() {
            @Override
            public void onSuccess(@NotNull Unit value) {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).goToHome();
                }
            }

            @Override
            public void onError(@NotNull Throwable exception) {
                tv_signInError.setText(exception.getMessage());
            }
        });
    }
}