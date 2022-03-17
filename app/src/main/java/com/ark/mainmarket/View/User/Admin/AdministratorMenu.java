package com.ark.mainmarket.View.User.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.ark.mainmarket.Utility;
import com.ark.mainmarket.databinding.ActivityAdministratorMenuBinding;

public class AdministratorMenu extends AppCompatActivity {

    private ActivityAdministratorMenuBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdministratorMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Utility.checkWindowSetFlag(this);
        listenerClick();

    }

    private void listenerClick() {
        binding.backBtn.setOnClickListener(view -> {
            finish();
        });

        binding.cardCategoryManage.setOnClickListener(view -> {
            Utility.updateUI(AdministratorMenu.this, ManageCategory.class);
        });

        binding.cardProductManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.updateUI(AdministratorMenu.this, ManageProduct.class);
            }
        });
    }
}