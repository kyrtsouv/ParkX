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
        btn_signUp=view.findViewById(R.id.btn_signUp);
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

        tv_signUpError.setText("");

        SupabaseManager.signUp(email, password, name, surname, new JavaResultCallback<>() {
            @Override
            public void onSuccess(@NotNull Unit value) {
                btn_signUp.setEnabled(true);
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onError(@NotNull Throwable exception) {
                btn_signUp.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                String result = exception.toString();
                tv_signUpError.setText(result);
            }
        });
    }


}