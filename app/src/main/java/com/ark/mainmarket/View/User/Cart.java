package com.ark.mainmarket.View.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ark.mainmarket.Adapter.AdapterCartShop;
import com.ark.mainmarket.Model.ModelShopCart;
import com.ark.mainmarket.Utility;
import com.ark.mainmarket.databinding.ActivityCartBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Cart extends AppCompatActivity {

    private ActivityCartBinding binding;

    private final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    private AdapterCartShop adapterCartShop;
    private List<ModelShopCart> listShopCart;

    private long countData;
    private String key = null;
    private boolean isLoadData = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Utility.checkWindowSetFlag(this);

        RecyclerView.LayoutManager layoutManagerCategory = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.recycleCartShop.setHasFixedSize(true);
        binding.recycleCartShop.setLayoutManager(layoutManagerCategory);

        adapterCartShop = new AdapterCartShop(this);
        binding.recycleCartShop.setAdapter(adapterCartShop);

        reference.child("cart").child(Utility.uidCurrentUser).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                countData = task.getResult().getChildrenCount();
                isLoadData = true;
                setCartShop();
            }else {
                Toast.makeText(this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        listenerComponent();
    }

    private void listenerComponent(){

        binding.cardBackBtn.setOnClickListener(view -> finish());
        // listen user scroll recyclerview
        binding.recycleCartShop.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                // get total item in list category
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) binding.recycleCartShop.getLayoutManager();
                assert linearLayoutManager != null;
                int totalItem = linearLayoutManager.getItemCount();
                Log.d("Total Item", String.valueOf(totalItem));
                // check scroll on bottom
                if (!binding.recycleCartShop.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE){
                    // check data item if total item < total data in database == load more data
                    if (totalItem < countData){
                        // load more data
                        if (!isLoadData){
                            isLoadData = true;
                            setCartShop();
                        }
                    }
                }
            }
        });

    }

    private void setCartShop(){
        if (!isLoadData){
            return;
        }

        Query query;
        if (key == null){
            query = reference.child("cart").child(Utility.uidCurrentUser).orderByKey().limitToFirst(10);
        }else {
            query = reference.child("cart").child(Utility.uidCurrentUser).orderByKey().startAfter(key).limitToFirst(10);
        }

        isLoadData = true;

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listShopCart = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ModelShopCart modelShopCart = dataSnapshot.getValue(ModelShopCart.class);
                    assert modelShopCart != null;
                    modelShopCart.setKey(dataSnapshot.getKey());
                    key = dataSnapshot.getKey();
                    listShopCart.add(modelShopCart);
                }

                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    adapterCartShop.setItem(listShopCart);
                    adapterCartShop.notifyDataSetChanged();
                    isLoadData = false;
                }, 500);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}