package com.ark.mainmarket.View.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.ark.mainmarket.Utility;
import com.ark.mainmarket.View.User.Profile;
import com.ark.mainmarket.databinding.ActivityAdministratorMenuBinding;

public class AdministratorMenu extends AppCompatActivity {

    private ActivityAdministratorMenuBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdministratorMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        listenerClick();

    }

    private void listenerClick() {
        binding.backBtn.setOnClickListener(view -> {
            Utility.updateUI(AdministratorMenu.this, Profile.class);
            finish();
        });

        binding.cardCategoryManage.setOnClickListener(view -> {
            Utility.updateUI(AdministratorMenu.this, AddCategory.class);
        });
    }
}