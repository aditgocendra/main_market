package com.ark.mainmarket.View.Auth;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.view.View;
import android.view.WindowManager;

import com.ark.mainmarket.Model.ModelUser;
import com.ark.mainmarket.Utility;
import com.ark.mainmarket.databinding.ActivityRegisterBinding;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        listenerClick();

    }

    private void listenerClick(){
        //redirect login
        binding.redirectSignIn.setOnClickListener(view -> {
            Utility.updateUI(Register.this, Login.class);
            finish();
        });

        // register
        binding.signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = binding.usernameRegisterTi.getText().toString();
                String email = binding.emailRegisterTi.getText().toString();
                String password = binding.passwordRegisterTi.getText().toString();
                String re_pass = binding.rePasswordRegisterTi.getText().toString();

                // validation form
                if (username.isEmpty()){
                    binding.usernameRegisterTi.setError("Username harus diisi");
                }else if (email.isEmpty()){
                    binding.emailRegisterTi.setError("Email harus diisi");
                }else if (password.isEmpty()){
                    binding.passwordRegisterTi.setError("Password harus diisi");
                }else if (re_pass.isEmpty()){
                    binding.rePasswordRegisterTi.setError("Konfirmasi password harus diisi");
                }else {
                    if (password.equals(re_pass)){
                        if (password.length() >= 8){
                            createUser(
                                    binding.usernameRegisterTi.getText().toString(),
                                    binding.emailRegisterTi.getText().toString(),
                                    binding.passwordRegisterTi.getText().toString()
                            );
                            binding.redirectSignIn.setEnabled(false);
                            binding.progressCircular.setVisibility(View.VISIBLE);
                        }else {
                            Utility.toastLS(Register.this, "Password minimal 8 karakter");
                        }
                    }else {
                        Utility.toastLS(Register.this, "Password dan konfirmasi password tidak sama");
                    }
                }
            }
        });
    }
    // create user in firebase auth
    private void createUser(String username, String email, String pass) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                FirebaseUser user = mAuth.getCurrentUser();
                assert user != null;
                saveDataUser(user.getUid(), username, user.getEmail());
            }else {
                Utility.toastLS(Register.this, "Gagal mendaftarkan akun");
                binding.redirectSignIn.setEnabled(true);
                binding.progressCircular.setVisibility(View.INVISIBLE);
            }
        });
    }
    // save data user in firebase realtime database
    private void saveDataUser(String uid, String username, String email){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        ModelUser modelUser = new ModelUser(
                username,
                email,
                "-",
                "-",
                "Customer",
                "-"
        );
        reference.child("users").child(uid).setValue(modelUser).addOnSuccessListener(unused -> {
            binding.redirectSignIn.setEnabled(true);
            binding.progressCircular.setVisibility(View.INVISIBLE);

            Utility.toastLS(Register.this, "Pendaftaran berhasil");
            Utility.updateUI(Register.this, Login.class);
            finish();

        }).addOnFailureListener(e -> {
            binding.redirectSignIn.setEnabled(true);
            binding.progressCircular.setVisibility(View.INVISIBLE);

            Utility.toastLS(Register.this, e.getMessage());
        });
    }
}