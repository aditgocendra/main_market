package com.ark.mainmarket.View.User.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Bundle;

import com.ark.mainmarket.Adapter.AdapterManageProduct;
import com.ark.mainmarket.Model.ModelProduct;
import com.ark.mainmarket.Utility;
import com.ark.mainmarket.databinding.ActivityManageProductBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ManageProduct extends AppCompatActivity {

    private ActivityManageProductBinding binding;
    private AdapterManageProduct adapterManageProduct;
    private List<ModelProduct> listProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Utility.checkWindowSetFlag(this);

        listenerClick();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        binding.recycleManageProduct.setLayoutManager(gridLayoutManager);
        binding.recycleManageProduct.setHasFixedSize(true);

        setDataProduct();

    }

    private void listenerClick() {
        binding.backBtn.setOnClickListener(view -> finish());

        binding.floatAddProduct.setOnClickListener(view -> Utility.updateUI(ManageProduct.this, AddProduct.class));


    }

    private void setDataProduct() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        reference.child("product").addValueEventListener(new ValueEventListener() {
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

                adapterManageProduct = new AdapterManageProduct(ManageProduct.this, listProduct);
                binding.recycleManageProduct.setAdapter(adapterManageProduct);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Utility.toastLS(ManageProduct.this, "Product"+error.getMessage());
            }
        });
    }
}