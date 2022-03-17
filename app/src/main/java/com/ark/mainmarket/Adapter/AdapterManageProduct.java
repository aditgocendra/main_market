package com.ark.mainmarket.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ark.mainmarket.Model.ModelProduct;
import com.ark.mainmarket.R;
import com.ark.mainmarket.Utility;
import com.ark.mainmarket.View.User.Admin.EditProduct;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterManageProduct extends RecyclerView.Adapter<AdapterManageProduct.MyViewHolder> {

    private Context mContext;
    private List<ModelProduct> listProduct;

    public AdapterManageProduct(Context mContext, List<ModelProduct> listProduct) {
        this.mContext = mContext;
        this.listProduct = listProduct;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.layout_grid_manage_product, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ModelProduct modelProduct = listProduct.get(position);
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

        holder.cardDelete.setOnClickListener(view -> {
            //Create the Dialog here
            Dialog dialog = new Dialog(mContext);
            dialog.setContentView(R.layout.custom_delete_dialog);
            dialog.getWindow().setBackgroundDrawable(mContext.getDrawable(R.drawable.custom_background_dialog));

            dialog.getWindow().setLayout(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            dialog.setCancelable(false); //Optional
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //Setting the animations to dialog

            Button Okay = dialog.findViewById(R.id.btn_okay);
            Button Cancel = dialog.findViewById(R.id.btn_cancel);

            dialog.show();
            Okay.setOnClickListener(v -> {
                deleteProduct(modelProduct.getKey(), modelProduct.getUrl_image());
                dialog.dismiss();
            });

            Cancel.setOnClickListener(v -> dialog.dismiss());
        });

        holder.cardEdit.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, EditProduct.class);
            intent.putExtra("key_product", modelProduct.getKey());
            intent.putExtra("url_image", modelProduct.getUrl_image());
            mContext.startActivity(intent);
        });

    }


    @Override
    public int getItemCount() {
        return listProduct.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nameProduct, priceNormal, priceSale;
        ImageView imageProduct;
        CardView cardEdit, cardDelete;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            nameProduct = itemView.findViewById(R.id.name_product_manage_tv);
            priceNormal = itemView.findViewById(R.id.price_normal_manage_tv);
            priceSale = itemView.findViewById(R.id.price_sale_manage_tv);
            imageProduct = itemView.findViewById(R.id.image_product_manage_iv);
            cardDelete = itemView.findViewById(R.id.card_delete_product);
            cardEdit = itemView.findViewById(R.id.card_edit_product);

        }
    }

    private void deleteProduct(String key, String url_image) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        FirebaseStorage referenceStorage = FirebaseStorage.getInstance();
        String name_photo = referenceStorage.getReferenceFromUrl(url_image).getName();
        StorageReference deleteRef = referenceStorage.getReference("product_image/"+name_photo);

        deleteRef.delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                reference.child("product").child(key).removeValue().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()){
                        Utility.toastLS(mContext, "Berhasil menghapus produk");
                    }else {
                        Utility.toastLS(mContext, "Product : "+ task1.getException().getMessage());
                    }
                });
            }else {
                Utility.toastLS(mContext, "Storage : "+task.getException().getMessage());
            }
        });


    }
}
