package com.ark.mainmarket;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Utility {

    public static void updateUI(Context from, Class to){
        Intent intent = new Intent(from, to);
        from.startActivity(intent);
    }

    public static void toastLS(Context context,  String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void login(){
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.signInWithEmailAndPassword("gocendra123@gmail.com", "aditgocendra123")
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Log.d("login", "berhasil");
                        }
                    }
                });
    }
}
