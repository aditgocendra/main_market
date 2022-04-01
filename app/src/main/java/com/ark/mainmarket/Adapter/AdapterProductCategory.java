package com.ark.mainmarket.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ark.mainmarket.Model.ModelProduct;
import com.ark.mainmarket.R;

import java.util.ArrayList;
import java.util.List;

public class AdapterProductCategory extends RecyclerView.Adapter<AdapterProductCategory.ProductCategoryViewHolder> {

    private List<ModelProduct> listProduct;
    private Context mContext;

    public AdapterProductCategory(Context mContext, List<ModelProduct> listProduct) {
        this.mContext = mContext;
        this.listProduct = listProduct;
    }


    @NonNull
    @Override
    public ProductCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.layout_grid_small_product, parent, false);
        return new ProductCategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductCategoryViewHolder holder, int position) {
        ModelProduct modelProduct = listProduct.get(position);

    }

    @Override
    public int getItemCount() {
        return listProduct.size();
    }

    public static class ProductCategoryViewHolder extends RecyclerView.ViewHolder {
        public ProductCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
