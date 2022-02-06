package com.ark.mainmarket.View.User;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

import com.ark.mainmarket.R;
import com.ark.mainmarket.View.Fragment.Favorite;
import com.ark.mainmarket.View.Fragment.Feed;
import com.ark.mainmarket.View.Fragment.Home;
import com.ark.mainmarket.databinding.ActivityHomeAppBinding;

public class HomeApp extends AppCompatActivity {

    private ActivityHomeAppBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeAppBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        changeFragment(new Home());
        binding.bottomNavbar.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.home:
                    changeFragment(new Home());
                    break;
                case R.id.feed:
                    changeFragment(new Feed());
                    break;
                case R.id.fav:
                    changeFragment(new Favorite());
                    break;
            }

            return true;
        });

    }

    private void changeFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.nav_default_pop_enter_anim, R.anim.nav_default_pop_exit_anim);
        fragmentTransaction.replace(R.id.frame_layout_fragment, fragment);
        fragmentTransaction.commit();
    }
}