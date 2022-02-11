package com.ark.mainmarket;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;

import com.ark.mainmarket.View.Admin.AddCategory;
import com.ark.mainmarket.View.Admin.AddProduct;
import com.ark.mainmarket.View.Auth.Login;
import com.ark.mainmarket.View.Fragment.Home;
import com.ark.mainmarket.View.User.HomeApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
                if (mUser != null){
                    Utility.updateUI(MainActivity.this, HomeApp.class);
                    Utility.uidCurrentUser = mUser.getUid();
                }else {
                    Utility.updateUI(MainActivity.this, Login.class);
                }

                finish();
            }
        }, 1000);


    }
}