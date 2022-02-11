 package com.ark.mainmarket.View.Auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.ark.mainmarket.Utility;
import com.ark.mainmarket.databinding.ActivityForgotPasswordBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

 public class ForgotPassword extends AppCompatActivity {

    private ActivityForgotPasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.resetPassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = binding.emailForgotPass.getText().toString();
                if (email.isEmpty()){
                    binding.emailForgotPass.setError("Email tidak boleh kosong");
                }else {
                    forgotPass(email);
                }
            }
        });

        binding.redirectSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.updateUI(ForgotPassword.this, Login.class);
                finish();
            }
        });

    }

     private void forgotPass(String email) {
         FirebaseAuth mAuth = FirebaseAuth.getInstance();
         mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
             @Override
             public void onComplete(@NonNull Task<Void> task) {
                 Utility.toastLS(ForgotPassword.this, "Kami telah mengirimkan email kepada anda untuk melakukan reset password anda");
             }
         });
     }
 }