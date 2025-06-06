package com.example.parkx;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.parkx.supabase.SupabaseManager;
import com.example.parkx.utils.JavaResultCallback;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

import io.github.jan.supabase.auth.GoTrueErrorResponse;
import io.github.jan.supabase.auth.exception.AuthRestException;
import kotlin.Unit;


public class SignInFragment extends Fragment {

    EditText et_signInEmail;

    EditText et_signInPassword;

    TextView tv_signInError;
    Button btn_signIn;
    ProgressBar progressBar;


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
        btn_signIn = view.findViewById(R.id.btn_signIn);
        progressBar = view.findViewById(R.id.progressBar);

        if (savedInstanceState != null) {
            et_signInEmail.setText(savedInstanceState.getString("email", ""));
            et_signInPassword.setText(savedInstanceState.getString("password", ""));
            tv_signInError.setText(savedInstanceState.getString("error", ""));
        }

        btn_signIn.setOnClickListener(v -> SignIn());

    }


    public void SignIn() {

        View view = getView();
        String email = et_signInEmail.getText().toString();
        String password = et_signInPassword.getText().toString();
        btn_signIn.setEnabled(false);
        progressBar.setVisibility(view.VISIBLE);

        boolean debug = true;
        if (debug) {
            email = "test@email.com";
            password = "password";
        }

        if (email.isEmpty() || password.isEmpty()) {
            tv_signInError.setText(R.string.please_fill_in_all_fields);
            btn_signIn.setEnabled(true);
            return;
        }


        tv_signInError.setText("");

        SupabaseManager.signIn(email, password, new JavaResultCallback<>() {
            @Override
            public void onSuccess(@NotNull Unit value) {
                btn_signIn.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).goToHome();

                }

            }

            @Override
            public void onError(@NotNull Throwable exception) {
                btn_signIn.setEnabled(true);
                tv_signInError.setText(exception.getMessage());
                progressBar.setVisibility(View.GONE);


                if (exception instanceof AuthRestException) {
                    AuthRestException authRestException = (AuthRestException) exception;
                    String errorCode = (authRestException.getErrorCode() != null) ? authRestException.getErrorCode().name() : "UNKNOWN_ERROR";
                    String errorMessage = authRestException.getMessage();

                    switch (errorCode) {
                        case "InvalidCredentials":
                            tv_signInError.setText(R.string.invalid_credentials);
                            break;

                        default:
                            tv_signInError.setText(String.format("%s%s", getString(R.string.authRest_generic_error), errorCode));
                    }


                } else if (exception instanceof IOException) {
                    // IOException indicates network issues (e.g., no internet connection)
                    tv_signInError.setText(R.string.network_error);
                } else {
                    // General error handling for other types of exceptions
                    tv_signInError.setText(String.format("%s%s", getString(R.string.generic_error), exception.getMessage()));
                }

            }
        });
    }
}