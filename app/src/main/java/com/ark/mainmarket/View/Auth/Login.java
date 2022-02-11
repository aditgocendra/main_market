package com.ark.mainmarket.View.Auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.ark.mainmarket.Utility;
import com.ark.mainmarket.View.User.HomeApp;
import com.ark.mainmarket.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class Login extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        listenerClick();

    }

    private void listenerClick(){
        binding.signUpRedirect.setOnClickListener(view -> Utility.updateUI(Login.this, Register.class));

        binding.redirectForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.updateUI(Login.this, ForgotPassword.class);
            }
        });

        binding.signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = binding.emailLoginTi.getText().toString();
                String pass = binding.passwordLoginTi.getText().toString();

                if (email.isEmpty()){
                    binding.emailLoginTi.setError("Email tidak boleh kosong");
                }else if (pass.isEmpty()){
                    binding.passwordLoginTi.setError("Password tidak boleh kosong");
                }else {
                    signIn(email, pass);
                }
            }
        });
    }

    private void signIn(String email, String pass) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Utility.updateUI(Login.this, HomeApp.class);
                    finish();
                }else {
                    Utility.toastLS(Login.this, Objects.requireNonNull(task.getException()).getMessage());
                }
            }
        });
    }
}