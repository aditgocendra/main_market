package com.ark.mainmarket.View.User;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.os.Bundle;
import com.ark.mainmarket.Model.ModelUser;
import com.ark.mainmarket.R;
import com.ark.mainmarket.Utility;
import com.ark.mainmarket.View.User.Fragment.Account;
import com.ark.mainmarket.View.User.Fragment.Favorite;
import com.ark.mainmarket.View.User.Fragment.Feed;
import com.ark.mainmarket.View.User.Fragment.Home;
import com.ark.mainmarket.databinding.ActivityHomeAppBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class HomeApp extends AppCompatActivity {

    private ActivityHomeAppBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeAppBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Utility.checkWindowSetFlag(this);
        setGlobalRole();


        changeFragment(new Home());
        binding.bottomNavbar.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.home:
                    changeFragment(new Home());
                    break;
                case R.id.feed:
                    changeFragment(new Feed());
                    break;
                case R.id.account:
                    changeFragment(new Account());
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

    private void setGlobalRole(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        reference.child("users").child(Utility.uidCurrentUser).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                ModelUser modelUser = task.getResult().getValue(ModelUser.class);
                assert modelUser != null;
                Utility.roleCurrentUser = modelUser.getRole();
            }else {
                Utility.toastLS(HomeApp.this, Objects.requireNonNull(task.getException()).getMessage());
            }
        });
    }
}