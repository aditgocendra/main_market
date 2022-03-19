package com.ark.mainmarket.View.Auth;


import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import com.ark.mainmarket.Utility;
import com.ark.mainmarket.View.User.HomeApp;
import com.ark.mainmarket.databinding.ActivityLoginBinding;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class Login extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Utility.checkWindowSetFlag(this);
        listenerClick();

    }

    private void listenerClick(){
        binding.signUpRedirect.setOnClickListener(view -> Utility.updateUI(Login.this, Register.class));

        binding.redirectForgotPass.setOnClickListener(view -> Utility.updateUI(Login.this, ForgotPassword.class));

        binding.signInBtn.setOnClickListener(view -> {

            String email = binding.emailLoginTi.getText().toString();
            String pass = binding.passwordLoginTi.getText().toString();

            if (email.isEmpty()){
                binding.emailLoginTi.setError("Email tidak boleh kosong");
            }else if (pass.isEmpty()){
                binding.passwordLoginTi.setError("Password tidak boleh kosong");
            }else {
                signIn(email, pass);
            }
        });
    }

    private void signIn(String email, String pass) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                FirebaseUser mUser = mAuth.getCurrentUser();
                Utility.uidCurrentUser = mUser.getUid();
                Utility.updateUI(Login.this, HomeApp.class);
                finish();
            }else {
                Utility.toastLS(Login.this, Objects.requireNonNull(task.getException()).getMessage());
            }
        });
    }
}