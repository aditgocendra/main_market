package com.ark.mainmarket;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.ark.mainmarket.View.User.HomeApp;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Utility.updateUI(MainActivity.this, HomeApp.class);
    }
}