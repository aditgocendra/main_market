package com.ark.mainmarket.View.User;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

import com.ark.mainmarket.R;
import com.ark.mainmarket.databinding.ActivityHomeAppBinding;

public class HomeApp extends AppCompatActivity {

    private ActivityHomeAppBinding binding;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeAppBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        Window window = this.getWindow();
          getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        window.setStatusBarColor(this.getResources().getColor(R.color.blue_cyan));



        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);






    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        super.onCreateOptionsMenu(menu);
//
//        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
//        return true;
//    }
}