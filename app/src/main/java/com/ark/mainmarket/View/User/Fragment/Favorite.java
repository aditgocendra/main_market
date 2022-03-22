package com.ark.mainmarket.View.User.Fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.ark.mainmarket.Adapter.AdapterFavorite;
import com.ark.mainmarket.Utility;
import com.ark.mainmarket.databinding.FragmentFavoriteBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Favorite extends Fragment {

    private List<String> listFavorite;
    private AdapterFavorite adapterFavorite;
    private final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    private long countData;
    private String key = null;
    private boolean isLoadData = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reference.child("product_favorite").child(Utility.uidCurrentUser).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                countData = task.getResult().getChildrenCount();
                isLoadData = true;
                setDataFavUser();
            }else {
                Toast.makeText(requireContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private FragmentFavoriteBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFavoriteBinding.inflate(inflater, container, false);

        RecyclerView.LayoutManager layoutManagerCategory = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        binding.recycleFavorite.setHasFixedSize(true);
        binding.recycleFavorite.setLayoutManager(layoutManagerCategory);

        adapterFavorite = new AdapterFavorite(requireContext());
        binding.recycleFavorite.setAdapter(adapterFavorite);

        binding.recycleFavorite.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                // get total item in list category
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) binding.recycleFavorite.getLayoutManager();
                assert linearLayoutManager != null;
                int totalItem = linearLayoutManager.getItemCount();
                Log.d("Total Item", String.valueOf(totalItem));
                // check scroll on bottom
                if (!binding.recycleFavorite.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE){
                    // check data item if total item < total data in database == load more data
                    if (totalItem < countData){
                        // load more data
                        if (!isLoadData){
                            isLoadData = true;
                            setDataFavUser();
                        }
                    }
                }
            }
        });

        return binding.getRoot();
    }

    private void setDataFavUser(){
        if (!isLoadData){
            return;
        }

        Query query;
        if (key == null){
            query = reference.child("product_favorite").child(Utility.uidCurrentUser).orderByKey().limitToFirst(10);
        }else {
            query = reference.child("product_favorite").child(Utility.uidCurrentUser).orderByKey().startAfter(key).limitToFirst(10);
        }

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listFavorite = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()){
                    String productKeyFav = ds.getKey();
                    if (productKeyFav != null){
                        key = ds.getKey();
                        listFavorite.add(productKeyFav);
                    }
                }
                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    if (listFavorite.size() != 0){
                        adapterFavorite.setItem(listFavorite);
                        adapterFavorite.notifyDataSetChanged();
                    }
                    isLoadData = false;

                },100);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}