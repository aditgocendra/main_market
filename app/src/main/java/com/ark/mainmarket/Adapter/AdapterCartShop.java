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
import com.ark.mainmarket.Model.ModelShopCart;
import com.ark.mainmarket.R;
import com.ark.mainmarket.Utility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class AdapterCartShop extends RecyclerView.Adapter<AdapterCartShop.CartShopViewHolder> {

    private final Context mContext;
    private final List<ModelShopCart> listShopCart = new ArrayList<>();
    private final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    public AdapterCartShop(Context mContext) {
        this.mContext = mContext;
    }

    public void setItem(List<ModelShopCart> list){
        listShopCart.addAll(list);
    }

    @NonNull
    @Override
    public CartShopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.layout_my_cart_shop, parent, false);
        return new CartShopViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartShopViewHolder holder, int position) {
        ModelShopCart modelShopCart = listShopCart.get(position);
        holder.textPriceNormal.setPaintFlags(holder.textPriceNormal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        setProduct(holder, modelShopCart, position);
    }


    @Override
    public int getItemCount() {
        return listShopCart.size();
    }

    public static class CartShopViewHolder extends RecyclerView.ViewHolder {
        CardView cardProductCart;
        ImageView imageProduct, minValueProduct, addValueProduct;
        TextView textNameProduct,textPriceSale, textValueProduct, textPriceNormal, freeSendingCart, wholeSale;

        public CartShopViewHolder(@NonNull View itemView) {
            super(itemView);

            cardProductCart = itemView.findViewById(R.id.card_product_cart);
            imageProduct = itemView.findViewById(R.id.image_product_cart);
            minValueProduct = itemView.findViewById(R.id.min_value_product);
            addValueProduct = itemView.findViewById(R.id.add_value_product);
            textNameProduct = itemView.findViewById(R.id.name_product_cart);
            textPriceSale = itemView.findViewById(R.id.price_sale_cart);
            textValueProduct = itemView.findViewById(R.id.value_product_tv);
            textPriceNormal = itemView.findViewById(R.id.price_normal_cart);
            wholeSale = itemView.findViewById(R.id.wholesale_cart);
            freeSendingCart = itemView.findViewById(R.id.free_sending_cart);
        }
    }


    private void setProduct(CartShopViewHolder holder, ModelShopCart modelShopCart, int pos) {
        reference.child("product").child(modelShopCart.getKey()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                ModelProduct modelProduct = task.getResult().getValue(ModelProduct.class);
                if (modelProduct != null){
                    Picasso.get().load(modelProduct.getUrl_image()).into(holder.imageProduct);
                    holder.textNameProduct.setText(modelProduct.getItem_name());
                    holder.textValueProduct.setText(modelShopCart.getTotal_product());

                    ViewCompat.setBackgroundTintList(holder.cardProductCart, ColorStateList.valueOf(Integer.parseInt(modelProduct.getColor_background())));

                    if (modelProduct.isFree_sending()){
                        holder.freeSendingCart.setVisibility(View.VISIBLE);
                    }

                    int value = Integer.parseInt(modelShopCart.getTotal_product());
                    setPriceValueProduct(holder, modelProduct, value);


                    // if the change value product
                    holder.minValueProduct.setOnClickListener(view -> {
                        int valueChange = Integer.parseInt(holder.textValueProduct.getText().toString());
                        if (valueChange <= 1){
                            confirmProductCart(modelShopCart.getKey(), pos);
                        }else {
                            valueChange -= 1;
                            setPriceValueProduct(holder, modelProduct, valueChange);
                        }
                    });

                    holder.addValueProduct.setOnClickListener(view -> {
                        int valueChange = Integer.parseInt(holder.textValueProduct.getText().toString());
                        if (valueChange >= Integer.parseInt(modelProduct.getStock())){
                            confirmProductCart(modelShopCart.getKey(), pos);
                        }else {
                            valueChange += 1;
                            setPriceValueProduct(holder, modelProduct, valueChange);
                        }
                    });

                }else {
                    Toast.makeText(mContext, "Product tidak ditemukan", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(mContext, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setPriceValueProduct(CartShopViewHolder holder, ModelProduct modelProduct, int value) {
        float priceSale;
        float priceStrike;
        if (!modelProduct.getDisc().equals("-")){
            float totalDisc = Integer.parseInt(modelProduct.getPrice_normal()) * (Float.parseFloat(modelProduct.getDisc()) / 100);
            priceSale = (Integer.parseInt(modelProduct.getPrice_normal()) - totalDisc) * value;
            priceStrike = Integer.parseInt(modelProduct.getPrice_normal()) * value;

            holder.textPriceSale.setText(Utility.currencyRp(String.valueOf(priceSale)));

            holder.textPriceNormal.setText(Utility.currencyRp(String.valueOf(priceStrike)));
            holder.textPriceNormal.setVisibility(View.VISIBLE);
        }else {
            priceSale = Integer.parseInt(modelProduct.getPrice_normal()) * value;
            holder.textPriceSale.setText(Utility.currencyRp(String.valueOf(priceSale)));
        }

        if (modelProduct.isWholesale()){
            if (value >= Integer.parseInt(modelProduct.getMin_buy_wholesale())){
                holder.wholeSale.setVisibility(View.VISIBLE);
                holder.textPriceNormal.setVisibility(View.VISIBLE);

                priceStrike = Integer.parseInt(modelProduct.getPrice_normal()) * value;
                priceSale = Integer.parseInt(modelProduct.getPrice_wholesale()) * value;
                holder.textPriceNormal.setText(Utility.currencyRp(String.valueOf(priceStrike)));
                holder.textPriceSale.setText(Utility.currencyRp(String.valueOf(priceSale)));
            }else {
                holder.wholeSale.setVisibility(View.GONE);
            }
        }

        holder.textValueProduct.setText(String.valueOf(value));
    }

    private void confirmProductCart(String keyProduct, int pos){
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
            deleteProductCart(keyProduct, pos);
            dialog.dismiss();
        });

        Cancel.setOnClickListener(v -> dialog.dismiss());
    }

    private void deleteProductCart(String key, int pos){

        reference.child("cart").child(Utility.uidCurrentUser).child(key).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                listShopCart.remove(pos);
                this.notifyItemRemoved(pos);
                Toast.makeText(mContext, "Berhasil menghapus produk dari keranjang", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(mContext, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
