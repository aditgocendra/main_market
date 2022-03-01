package com.ark.mainmarket.View.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import com.ark.mainmarket.Adapter.AdapterManageCategory;
import com.ark.mainmarket.Model.ModelCategory;
import com.ark.mainmarket.Utility;
import com.ark.mainmarket.databinding.ActivityManageCategoryBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ManageCategory extends AppCompatActivity {

    private ActivityManageCategoryBinding binding;
    private AdapterManageCategory adapterManageCategory;
    private List<ModelCategory> modelCategoryList;

    private final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    private long countData;
    private String key = null;
    private boolean isLoadData = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Utility.checkWindowSetFlag(this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.recycleCategoryList.setLayoutManager(layoutManager);
        binding.recycleCategoryList.setItemAnimator(new DefaultItemAnimator());

        adapterManageCategory = new AdapterManageCategory(this);
        binding.recycleCategoryList.setAdapter(adapterManageCategory);

        listenerClick();
        // get totalData in firebase
        reference.child("category").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                countData = task.getResult().getChildrenCount();
                isLoadData = true;
                setDataCategory();
            }else {
                Utility.toastLS(ManageCategory.this, Objects.requireNonNull(task.getException()).getMessage());
            }
        });

    }

    private void listenerClick() {
        binding.backBtn.setOnClickListener(view -> finish());

        binding.floatAddCategory.setOnClickListener(view -> Utility.updateUI(ManageCategory.this, AddCategory.class));

        binding.recycleCategoryList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                // get total item in list category
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) binding.recycleCategoryList.getLayoutManager();
                int totalItem = linearLayoutManager.getItemCount();
                Log.d("Total Item", String.valueOf(totalItem));
                // check scroll on bottom
                if (!binding.recycleCategoryList.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE){
                    // check data item if total item < total data in database == load more data
                    if (totalItem < countData){
                        // load more data
                        if (!isLoadData){
                            isLoadData = true;
                            setDataCategory();
                        }
                    }
                }
            }
        });
    }

    private void setDataCategory() {
        if (!isLoadData){
            return;
        }

        Query query;
        if (key == null){
            query = reference.child("category").orderByKey().limitToFirst(10);
        }else {
            query = reference.child("category").orderByKey().startAfter(key).limitToFirst(10);
        }

        isLoadData = true;
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelCategoryList = new ArrayList<>();

                for (DataSnapshot ds : snapshot.getChildren()){
                    ModelCategory modelCategory = ds.getValue(ModelCategory.class);
                    if (modelCategory != null){
                        modelCategory.setKey(ds.getKey());
                        key = ds.getKey();
                        modelCategoryList.add(modelCategory);
                    }
                }

                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    if (modelCategoryList.size() == 0){
                        binding.warningCategoryTextNull.setVisibility(View.VISIBLE);
                    }else {
                        binding.warningCategoryTextNull.setVisibility(View.GONE);
                    }
                    adapterManageCategory.setItem(modelCategoryList);
                    adapterManageCategory.notifyDataSetChanged();
                    Log.d("Item set", "True");
                    isLoadData = false;
                }, 500);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Utility.toastLS(ManageCategory.this, error.getMessage());
            }
        });
    }
}