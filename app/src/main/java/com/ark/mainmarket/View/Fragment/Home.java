package com.ark.mainmarket.View.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.ark.mainmarket.Adapter.AdapterHomeCategory;
import com.ark.mainmarket.Adapter.AdapterHomeNewProduct;
import com.ark.mainmarket.Model.ModelCategory;
import com.ark.mainmarket.Model.ModelProduct;
import com.ark.mainmarket.Utility;
import com.ark.mainmarket.View.User.Profile;
import com.ark.mainmarket.databinding.FragmentHomeBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Home extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Home() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Home.
     */
    // TODO: Rename and change types and number of parameters
    public static Home newInstance(String param1, String param2) {
        Home fragment = new Home();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCategoryList();
        setNewProduct();
    }

    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    private List<ModelCategory> listCategory;
    private List<ModelProduct> listProductNew;

    private AdapterHomeCategory adapterHomeCategory;
    private AdapterHomeNewProduct adapterHomeNewProduct;

    private FragmentHomeBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        binding.accountImg.setOnClickListener(view -> {
            Utility.updateUI(getActivity(), Profile.class);
        });

        // category recycle
        RecyclerView.LayoutManager layoutManagerCategory = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.recycleCategoryHorizontal.setHasFixedSize(true);
        binding.recycleCategoryHorizontal.setLayoutManager(layoutManagerCategory);

        // new product recycle
        RecyclerView.LayoutManager layoutManagerProduct = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.recycleProductNewHorizontal.setHasFixedSize(true);
        binding.recycleProductNewHorizontal.setLayoutManager(layoutManagerProduct);

        return binding.getRoot();

    }

    private void setCategoryList(){
        reference.child("category").limitToFirst(10).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listCategory = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()){
                    ModelCategory modelCategory = ds.getValue(ModelCategory.class);
                    if (modelCategory != null){
                        modelCategory.setKey(ds.getKey());
                        listCategory.add(modelCategory);
                    }
                }

                adapterHomeCategory = new AdapterHomeCategory(getContext(), listCategory);
                binding.recycleCategoryHorizontal.setAdapter(adapterHomeCategory);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Utility.toastLS(getContext(), "Category :"+error.getMessage());
            }
        });
    }

    private void setNewProduct(){
        reference.child("product").limitToLast(5).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listProductNew = new ArrayList<>();

                for (DataSnapshot ds : snapshot.getChildren()){
                    ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                    if (modelProduct != null){
                        modelProduct.setKey(ds.getKey());
                        listProductNew.add(modelProduct);
                    }
                }
                adapterHomeNewProduct = new AdapterHomeNewProduct(getContext(), listProductNew);
                binding.recycleProductNewHorizontal.setAdapter(adapterHomeNewProduct);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}