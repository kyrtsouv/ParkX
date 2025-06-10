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

import io.github.jan.supabase.auth.exception.AuthRestException;
import kotlin.Unit;

public class SignUpFragment extends Fragment {

    EditText et_name;
    EditText et_surname;
    EditText et_signUpEmail;
    EditText et_signUpPassword;
    TextView tv_signUpError;
    ProgressBar progressBar;
    Button btn_signUp;

    public SignUpFragment() {
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("name", et_name.getText().toString());
        outState.putString("surname", et_surname.getText().toString());
        outState.putString("email", et_signUpEmail.getText().toString());
        outState.putString("password", et_signUpPassword.getText().toString());
        outState.putString("error", tv_signUpError.getText().toString());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        et_name = view.findViewById(R.id.et_name);
        et_surname = view.findViewById(R.id.et_surname);
        et_signUpEmail = view.findViewById(R.id.et_signUpEmail);
        et_signUpPassword = view.findViewById(R.id.et_signUpPassword);
        tv_signUpError = view.findViewById(R.id.tv_signUpError);
        btn_signUp = view.findViewById(R.id.btn_signUp);
        progressBar = view.findViewById(R.id.progressBar);

        if (savedInstanceState != null) {
            et_name.setText(savedInstanceState.getString("name", ""));
            et_surname.setText(savedInstanceState.getString("surname", ""));
            et_signUpEmail.setText(savedInstanceState.getString("email", ""));
            et_signUpPassword.setText(savedInstanceState.getString("password", ""));
            tv_signUpError.setText(savedInstanceState.getString("error", ""));
        }

        btn_signUp.setOnClickListener(v -> SignUp());

    }

    public void SignUp() {

        progressBar.setVisibility(View.VISIBLE);
        btn_signUp.setEnabled(false);
        String name = et_name.getText().toString();
        String surname = et_surname.getText().toString();
        String email = et_signUpEmail.getText().toString();
        String password = et_signUpPassword.getText().toString();

        if (name.isEmpty() || surname.isEmpty() || email.isEmpty() || password.isEmpty()) {
            btn_signUp.setEnabled(true);
            progressBar.setVisibility(View.GONE);
            tv_signUpError.setText(R.string.please_fill_in_all_fields);
            return;
        }
        if (!isValidEmail(email)) {
            btn_signUp.setEnabled(true);
            progressBar.setVisibility(View.GONE);
            tv_signUpError.setText(R.string.not_valid_email);
            return;

        }

        tv_signUpError.setText("");

        SupabaseManager.signUp(email, password, name, surname, new JavaResultCallback<>() {
                    @Override
                    public void onSuccess(@NotNull Unit value) {

                        progressBar.setVisibility(View.GONE);
                        if (getActivity() instanceof MainActivity) {
                            ((MainActivity) getActivity()).goToHome();
                        }
                    }

                    @Override
                    public void onError(@NotNull Throwable exception) {
                        btn_signUp.setEnabled(true);
                        progressBar.setVisibility(View.GONE);


                        if (exception instanceof AuthRestException) {
                            AuthRestException authRestException = (AuthRestException) exception;
                            String errorCode = (authRestException.getErrorCode() != null) ? authRestException.getErrorCode().name() : "UNKNOWN_ERROR";
                            String errorMessage = authRestException.getMessage();

                            switch (errorCode) {
                                case "UserAlreadyExists":
                                    tv_signUpError.setText(R.string.email_already_in_use);
                                    break;

                                default:
                                    tv_signUpError.setText(String.format("%s%s", getString(R.string.authRest_generic_error), errorCode));
                            }


                        } else if (exception instanceof IOException) {
                            // IOException indicates network issues (e.g., no internet connection)
                            tv_signUpError.setText(R.string.network_error);
                        } else {
                            // General error handling for other types of exceptions
                            tv_signUpError.setText(String.format("%s%s", getString(R.string.generic_error), exception.getMessage()));
                        }


                    }
                }

        );
    }

    public static boolean isValidEmail(String email) {
        // Simple regex for email validation
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

        // Use String's matches() method to check if the email matches the regex
        return email.matches(emailRegex);
    }

}