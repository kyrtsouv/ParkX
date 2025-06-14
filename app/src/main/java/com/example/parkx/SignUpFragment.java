package com.example.parkx;

import android.content.Context;
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
    HomeListener homeListener;

    //A method for checking if the email address is valid by matching it to a regular expression
    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }

    // This method is called when the fragment is attached to the activity.
    // It checks if the activity implements the HomeListener interface, which is used to navigate to the home screen after successful registration.
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            homeListener = (HomeListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context + " must implement HomeListener");
        }

    }

    // This method saves the state of the EditTexts and TextView when the fragment is paused or stopped, so that it can be restored later.
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    // This method is called after the view is created and restores the state of the details EditTexts and error TextView if there is a saved instance state and sets the onClickListener for the sign up button.
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

        btn_signUp.setOnClickListener(v -> signUp());
    }

    //This is the method that is  attached to the sign up button
    public void signUp() {
        //Sets the progress bar visible for the duration of the registration and sets the button disabled
        progressBar.setVisibility(View.VISIBLE);
        btn_signUp.setEnabled(false);
        String name = et_name.getText().toString();
        String surname = et_surname.getText().toString();
        String email = et_signUpEmail.getText().toString();
        String password = et_signUpPassword.getText().toString();

        //If the any fields are empty prints the according error
        if (name.isEmpty() || surname.isEmpty() || email.isEmpty() || password.isEmpty()) {
            //Hides the progress bar and sets the button enabled
            btn_signUp.setEnabled(true);
            progressBar.setVisibility(View.GONE);
            tv_signUpError.setText(R.string.please_fill_in_all_fields);
            return;
        }

        //Checks if the email address is valid using the previous method and sends the text error to the error field
        if (!isValidEmail(email)) {
            //Hides the progress bar and sets the button enabled
            btn_signUp.setEnabled(true);
            progressBar.setVisibility(View.GONE);
            tv_signUpError.setText(R.string.not_valid_email);
            return;

        }

        tv_signUpError.setText("");

        //This is the SupabaseManager method for registration
        SupabaseManager.signUp(email, password, name, surname, new JavaResultCallback<>() {
            @Override
            public void onSuccess(@NotNull Unit value) {
                //If the registration is successful it hides the progress bar and runs the go to home method of MainActivity
                progressBar.setVisibility(View.GONE);
                homeListener.goToHome();
            }

            //If the registration is not successful it handles the error
            @Override
            public void onError(@NotNull Throwable exception) {
                btn_signUp.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                //If the the exception is an instance of AuthRestException it uses error codes provided by Supabase
                if (exception instanceof AuthRestException) {
                    AuthRestException authRestException = (AuthRestException) exception;
                    String errorCode = (authRestException.getErrorCode() != null) ? authRestException.getErrorCode().name() : "UNKNOWN_ERROR";
                    //This is the case of using an email that is registered
                    if (errorCode.equals("UserAlreadyExists")) {
                        tv_signUpError.setText(R.string.email_already_in_use);
                    } else {
                        //This is the case of all the other error codes
                        tv_signUpError.setText(String.format("%s%s", getString(R.string.authRest_generic_error), errorCode));
                    }
                } else if (exception instanceof IOException) {
                    //   //This is the case of IOException that indicates network issues
                    tv_signUpError.setText(R.string.network_error);
                } else {
                    //This is the  error handling for other types of exceptions
                    tv_signUpError.setText(String.format("%s%s", getString(R.string.generic_error), exception.getMessage()));
                }
            }
        });
    }
}