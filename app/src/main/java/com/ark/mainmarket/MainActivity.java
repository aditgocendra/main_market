package com.ark.mainmarket;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import com.ark.mainmarket.View.Auth.Login;
import com.ark.mainmarket.View.User.HomeApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Utility.checkWindowSetFlag(this);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
            if (mUser != null){
                // set uid current user
                Utility.uidCurrentUser = mUser.getUid();
                Utility.updateUI(MainActivity.this, HomeApp.class);
            }else {
                Utility.updateUI(MainActivity.this, Login.class);
            }

            finish();
        }, 1000);


    }
}