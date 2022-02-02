package com.ark.mainmarket.View.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import com.ark.mainmarket.databinding.ActivityProductAddBinding;

public class ProductAdd extends AppCompatActivity {

    private ActivityProductAddBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}