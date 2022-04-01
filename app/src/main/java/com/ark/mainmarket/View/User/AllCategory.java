package com.ark.mainmarket.View.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;
import com.ark.mainmarket.Adapter.AdapterAllCategory;
import com.ark.mainmarket.Adapter.AdapterProductCategory;
import com.ark.mainmarket.Model.ModelCategory;
import com.ark.mainmarket.Model.ModelProduct;
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

    private List<ModelProduct> listProduct;
    private AdapterProductCategory adapterProductCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Utility.checkWindowSetFlag(this);

        listenerClick();

        // recycle all category
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.recycleAllCategory.setLayoutManager(layoutManager);
        binding.recycleAllCategory.setItemAnimator(new DefaultItemAnimator());

        setCategory();

        // product category recycle
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        binding.recycleAllCategoryProduct.setLayoutManager(gridLayoutManager);
        binding.recycleAllCategoryProduct.setItemAnimator(new DefaultItemAnimator());
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

                if (listCategory.size() != 0){
                    setProductCategory(listCategory.get(0).getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Utility.toastLS(AllCategory.this, "Database"+error.getMessage());
            }
        });
    }

    public void setProductCategory(String keyCategory){
        reference.child("product").orderByChild("category").equalTo(keyCategory).limitToLast(10).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listProduct = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()){
                    ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                    if (modelProduct != null){
                        modelProduct.setKey(ds.getKey());
                        listProduct.add(modelProduct);
                    }
                }

                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    if (listProduct.size() != 0){
                        adapterProductCategory = new AdapterProductCategory(AllCategory.this, listProduct);
                        binding.recycleAllCategoryProduct.setAdapter(adapterProductCategory);
                    }else {
                        Toast.makeText(AllCategory.this, "Kategori ini belum memilki produk", Toast.LENGTH_SHORT).show();
                    }
                }, 300);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AllCategory.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }




}