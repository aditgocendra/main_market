package com.ark.mainmarket.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ark.mainmarket.Model.ModelProduct;
import com.ark.mainmarket.R;
import com.ark.mainmarket.Utility;
import com.ark.mainmarket.View.User.ProductDetail;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterHomeNewProduct extends RecyclerView.Adapter<AdapterHomeNewProduct.MyViewHolder> {

    private final Context mContext;
    private final List<ModelProduct> listProductNew;

    public AdapterHomeNewProduct(Context mContext, List<ModelProduct> listProductNew) {
        this.mContext = mContext;
        this.listProductNew = listProductNew;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.layout_item_product_1, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ModelProduct modelProduct = listProductNew.get(position);

        Picasso.get().load(modelProduct.getUrl_image()).into(holder.imageProduct);

        holder.nameProduct.setText(modelProduct.getItem_name());

        if (!modelProduct.getDisc().equals("-")){
            holder.priceNormal.setVisibility(View.VISIBLE);
            holder.priceNormal.setText(Utility.currencyRp(modelProduct.getPrice_normal()));
            holder.priceNormal.setPaintFlags(holder.priceNormal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            float totalDisc = Integer.parseInt(modelProduct.getPrice_normal()) * (Float.parseFloat(modelProduct.getDisc()) / 100);
            float priceSale = Integer.parseInt(modelProduct.getPrice_normal()) - totalDisc;
            holder.priceSale.setText(Utility.currencyRp(String.valueOf(priceSale)));
        }else {
            holder.priceSale.setText(Utility.currencyRp(modelProduct.getPrice_normal()));
        }

        holder.cardProduct.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, ProductDetail.class);
            intent.putExtra("key_product", modelProduct.getKey());
            intent.putExtra("key_category", modelProduct.getCategory());
            mContext.startActivity(intent);
        });


    }

    @Override
    public int getItemCount() {
        return listProductNew.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nameProduct, priceSale, priceNormal;
        ImageView imageProduct;
        CardView cardProduct;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nameProduct = itemView.findViewById(R.id.name_product_tv);
            priceSale = itemView.findViewById(R.id.price_sale_tv);
            priceNormal = itemView.findViewById(R.id.price_disc);
            imageProduct = itemView.findViewById(R.id.image_product_thumbs);
            cardProduct = itemView.findViewById(R.id.card_product);

        }
    }
}
