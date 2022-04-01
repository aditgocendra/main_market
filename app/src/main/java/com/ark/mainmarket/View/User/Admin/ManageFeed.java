package com.ark.mainmarket.View.User.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.ark.mainmarket.Utility;
import com.ark.mainmarket.databinding.ActivityManageFeedBinding;

public class ManageFeed extends AppCompatActivity {

    private ActivityManageFeedBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageFeedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Utility.checkWindowSetFlag(this);


    }
}