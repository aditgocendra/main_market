package com.ark.mainmarket;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;

import com.ark.mainmarket.View.Admin.AddCategory;
import com.ark.mainmarket.View.Admin.AddProduct;
import com.ark.mainmarket.View.Auth.Login;
import com.ark.mainmarket.View.User.HomeApp;

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
                Utility.updateUI(MainActivity.this, Login.class);
                finish();
            }
        }, 1000);


    }
}