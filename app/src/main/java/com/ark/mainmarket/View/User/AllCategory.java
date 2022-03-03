package com.ark.mainmarket.View.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.ark.mainmarket.Adapter.AdapterAllCategory;
import com.ark.mainmarket.Model.ModelCategory;
import com.ark.mainmarket.Utility;
import com.ark.mainmarket.databinding.ActivityAllCategoryBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AllCategory extends AppCompatActivity {

    private ActivityAllCategoryBinding binding;
    private final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    private List<ModelCategory> listCategory;
    private AdapterAllCategory adapterAllCategory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Utility.checkWindowSetFlag(this);

        listenerClick();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.recycleAllCategory.setLayoutManager(layoutManager);
        binding.recycleAllCategory.setItemAnimator(new DefaultItemAnimator());

        setCategory();

    }

    private void listenerClick() {
        binding.backBtn.setOnClickListener(view -> finish());
    }

    private void setCategory() {
        reference.child("category").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listCategory = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()){
                    ModelCategory modelCategory = ds.getValue(ModelCategory.class);
                    if (modelCategory != null){
                        modelCategory.setKey(ds.getKey());
                        if (!modelCategory.getName_category().equals("Semua Kategori")){
                            listCategory.add(modelCategory);
                        }
                    }
                }
                adapterAllCategory = new AdapterAllCategory(AllCategory.this, listCategory);
                binding.recycleAllCategory.setAdapter(adapterAllCategory);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Utility.toastLS(AllCategory.this, "Database"+error.getMessage());
            }
        });
    }


}