package com.ark.mainmarket;


import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;
import com.ark.mainmarket.Model.ModelUser;
import com.ark.mainmarket.View.Auth.Login;
import com.ark.mainmarket.View.User.HomeApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
            if (mUser != null){
                // set uid current user
                Utility.uidCurrentUser = mUser.getUid();
                // set role current user
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                reference.child("users").child(mUser.getUid()).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        ModelUser modelUser = task.getResult().getValue(ModelUser.class);
                        Utility.roleCurrentUser = modelUser.getRole();

                    }else {
                        Utility.toastLS(MainActivity.this, task.getException().getMessage());
                        System.exit(0);
                    }
                });

                Utility.updateUI(MainActivity.this, HomeApp.class);

            }else {
                Utility.updateUI(MainActivity.this, Login.class);
            }

            finish();
        }, 1000);


    }
}