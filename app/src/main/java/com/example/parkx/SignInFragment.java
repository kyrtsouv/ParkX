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

    /**
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_in, container, false);
    }

    /**
     * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     */
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

        btn_signIn.setOnClickListener(v -> signIn());
    }


    //This is the method that is added to the sign in button
    public void signIn() {
        String email = et_signInEmail.getText().toString();
        String password = et_signInPassword.getText().toString();

        //After the login button is pressed the progress bar becomes visible (and will be for the duration of the database authentication) and the button becomes not clickable

        btn_signIn.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        //Checks if the email and password are empty and if they are prints the error accordingly

        if (email.isEmpty() || password.isEmpty()) {
            tv_signInError.setText(R.string.please_fill_in_all_fields);
            btn_signIn.setEnabled(true);
            progressBar.setVisibility(View.GONE);
            return;
        }

        tv_signInError.setText("");

//Performs sign in with the SupabaseManager class

        SupabaseManager.signIn(email, password, new JavaResultCallback<>() {
            @Override
            public void onSuccess(@NotNull Unit value) {

 //If the login is successful it hides the progress bar and it runs the go to home function of MainActivity

                progressBar.setVisibility(View.GONE);
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).goToHome();
                }
            }
//If the login is not successful it handles the errors
            @Override
            public void onError(@NotNull Throwable exception) {
           //In case of an error it hides the progress bar (since the authentication has stopped) and sets the sign in button enabled again
                btn_signIn.setEnabled(true);
                progressBar.setVisibility(View.GONE);
//If the the exception is an instance of AuthRestException it uses error codes provided by Supabase
                if (exception instanceof AuthRestException) {
                    AuthRestException authRestException = (AuthRestException) exception;
                    String errorCode = (authRestException.getErrorCode() != null) ? authRestException.getErrorCode().name() : "UNKNOWN_ERROR";
            //This is the  case of invalid credentials
                    if (errorCode.equals("InvalidCredentials")) {
                        tv_signInError.setText(R.string.invalid_credentials);
                    } else {
            //This is the case of all the other error codes
                        tv_signInError.setText(String.format("%s%s", getString(R.string.authRest_generic_error), errorCode));
                    }
                } else if (exception instanceof IOException) {
                    //This is the case of IOException that indicates network issues
                    tv_signInError.setText(R.string.network_error);
                } else {
                    //This is the  error handling for other types of exceptions
                    tv_signInError.setText(String.format("%s%s", getString(R.string.generic_error), exception.getMessage()));
                }

            }
        });
    }
}