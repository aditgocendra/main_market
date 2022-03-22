package com.ark.mainmarket.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ark.mainmarket.Model.ModelProduct;
import com.ark.mainmarket.R;
import com.ark.mainmarket.Utility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AdapterFavorite extends RecyclerView.Adapter<AdapterFavorite.FavoriteViewHolder> {

    private List<String> listFavorite = new ArrayList<>();
    private Context mContext;
    private final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    public AdapterFavorite( Context mContext) {
        this.mContext = mContext;
    }

    public void setItem(List<String> list){
        this.listFavorite.addAll(list);
    }


    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.layout_item_favorite, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        String keyProduct = listFavorite.get(position);
        holder.textPriceNormal.setPaintFlags(holder.textPriceNormal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        setProduct(keyProduct, holder);

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
                deleteFavProduct(keyProduct, position);
                dialog.dismiss();
            });

            Cancel.setOnClickListener(v -> dialog.dismiss());
        });
    }

    @Override
    public int getItemCount() {
        return listFavorite.size();
    }

    public static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        CardView cardProductCart, cardDelete;
        ImageView imageProduct;
        TextView textNameProduct,textPriceSale, textPriceNormal, freeSendingCart, wholeSale;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);

            cardProductCart = itemView.findViewById(R.id.card_product_cart);
            imageProduct = itemView.findViewById(R.id.image_product_cart);
            textNameProduct = itemView.findViewById(R.id.name_product_cart);
            textPriceSale = itemView.findViewById(R.id.price_sale_cart);
            textPriceNormal = itemView.findViewById(R.id.price_normal_cart);
            wholeSale = itemView.findViewById(R.id.wholesale_cart);
            freeSendingCart = itemView.findViewById(R.id.free_sending_cart);
            cardDelete = itemView.findViewById(R.id.card_delete_item_fav);

        }
    }

    private void setProduct(String key, FavoriteViewHolder holder) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("product").child(key).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                ModelProduct modelProduct = task.getResult().getValue(ModelProduct.class);
                if (modelProduct != null){
                    Picasso.get().load(modelProduct.getUrl_image()).into(holder.imageProduct);

                    ViewCompat.setBackgroundTintList(holder.cardProductCart, ColorStateList.valueOf(Integer.parseInt(modelProduct.getColor_background())));
                    holder.textNameProduct.setText(modelProduct.getItem_name());

                    if (modelProduct.isWholesale()){
                        holder.wholeSale.setVisibility(View.VISIBLE);
                    }

                    if (modelProduct.isFree_sending()){
                        holder.freeSendingCart.setVisibility(View.VISIBLE);
                    }

                    if (!modelProduct.getDisc().equals("-")){
                        float priceProduct;
                        float totalDisc = Integer.parseInt(modelProduct.getPrice_normal()) * (Float.parseFloat(modelProduct.getDisc()) / 100);
                        priceProduct = (Integer.parseInt(modelProduct.getPrice_normal()) - totalDisc);
                        holder.textPriceNormal.setText(Utility.currencyRp(modelProduct.getPrice_normal()));
                        holder.textPriceNormal.setVisibility(View.VISIBLE);
                        holder.textPriceSale.setText(Utility.currencyRp(String.valueOf(priceProduct)));
                    }

                }else {
                    Toast.makeText(mContext, "Produk tidak ditemukan", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(mContext, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteFavProduct(String key, int pos){
        reference.child("product_favorite").child(Utility.uidCurrentUser).child(key).removeValue().addOnSuccessListener(unused -> {
            listFavorite.remove(pos);
            this.notifyItemRemoved(pos);
            Toast.makeText(mContext, "Produk favorite berhasil dihapus", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
