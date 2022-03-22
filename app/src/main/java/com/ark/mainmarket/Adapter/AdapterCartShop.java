package com.ark.mainmarket.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AdapterCartShop extends RecyclerView.Adapter<AdapterCartShop.CartShopViewHolder> {

    private final Context mContext;
    private final TextView textPriceCart;

    private final List<ModelShopCart> listShopCart = new ArrayList<>();
    private final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    // price set
    private int totalPriceCart = 0;
    boolean checkPriceChange = false;


    public AdapterCartShop(Context mContext, TextView textPriceCart) {
        this.mContext = mContext;
        this.textPriceCart = textPriceCart;
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
        setProduct(holder, modelShopCart);

        holder.checkBoxProduct.setOnCheckedChangeListener((compoundButton, b) -> {
            String textPriceSale = holder.textPriceSale.getText().toString().replaceAll("[^0-9]","");
            String textPriceAllItem = textPriceCart.getText().toString().replaceAll("[^0-9]","");

            if (!textPriceSale.equals("-") && !textPriceAllItem.equals("-")){
                int priceSale = Integer.parseInt(textPriceSale);
                int priceAllItem = Integer.parseInt(textPriceAllItem);

                if (b){
                    priceAllItem += priceSale;
                }else {
                    priceAllItem -= priceSale;
                }

                textPriceCart.setText(Utility.currencyRp(String.valueOf(priceAllItem)));
            }
        });

    }

    @Override
    public int getItemCount() {
        return listShopCart.size();
    }

    public static class CartShopViewHolder extends RecyclerView.ViewHolder {
        CardView cardProductCart;
        ImageView imageProduct, minValueProduct, addValueProduct;
        TextView textNameProduct,textPriceSale, textValueProduct, textPriceNormal, freeSendingCart, wholeSale;
        CheckBox checkBoxProduct;

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
            checkBoxProduct = itemView.findViewById(R.id.checkbox_product);
        }
    }


    private void setProduct(CartShopViewHolder holder, ModelShopCart modelShopCart) {
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
                    setPriceValueProduct(holder, modelProduct, value, "default");


                    // if the change value product min
                    holder.minValueProduct.setOnClickListener(view -> {
                        int valueChange = Integer.parseInt(holder.textValueProduct.getText().toString());
                        if (valueChange <= 1){
                            confirmDeleteProductCart(modelShopCart.getKey());
                        }else {
                            valueChange -= 1;

                            if (!modelProduct.getMin_buy_wholesale().equals("-")){
                                checkPriceChange = (valueChange + 1) == Integer.parseInt(modelProduct.getMin_buy_wholesale());
                            }

                            setPriceValueProduct(holder, modelProduct, valueChange, "min");
                        }
                    });

                    // if the change value product plus
                    holder.addValueProduct.setOnClickListener(view -> {
                        int valueChange = Integer.parseInt(holder.textValueProduct.getText().toString());
                        if (valueChange >= Integer.parseInt(modelProduct.getStock())){
                            Toast.makeText(mContext, "Sudah mencapai batas stock product", Toast.LENGTH_SHORT).show();
                        }else {
                            valueChange += 1;
                            if (!modelProduct.getMin_buy_wholesale().equals("-")){
                                checkPriceChange = valueChange == Integer.parseInt(modelProduct.getMin_buy_wholesale());
                            }
                            Log.d("check_change", modelProduct.getMin_buy_wholesale()+" "+valueChange);
                            setPriceValueProduct(holder, modelProduct, valueChange, "add");
                        }
                    });

                }else {
                    Toast.makeText(mContext, "Product tidak ditemukan", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(mContext, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setPriceValueProduct(CartShopViewHolder holder, ModelProduct modelProduct, int value, String operation) {
        float priceProduct;
        float priceSale;
        float priceStrike = 0;

        if (!modelProduct.getDisc().equals("-")){
            // disc product sale set
            float totalDisc = Integer.parseInt(modelProduct.getPrice_normal()) * (Float.parseFloat(modelProduct.getDisc()) / 100);

            if (checkPriceChange && operation.equals("min")){
                if (modelProduct.isWholesale()){
                    priceProduct = Integer.parseInt(modelProduct.getPrice_wholesale());
                    totalPriceCart -= priceProduct * (value + 1);
                    totalPriceCart += (Integer.parseInt(modelProduct.getPrice_normal()) - totalDisc) * (value + 1);
                }
                checkPriceChange = false;
            }

            priceProduct = (Integer.parseInt(modelProduct.getPrice_normal()) - totalDisc);
            priceSale = priceProduct * value;
            priceStrike = Integer.parseInt(modelProduct.getPrice_normal()) * value;

            holder.textPriceNormal.setVisibility(View.VISIBLE);
        }else {
            // normal price and not disc
            if (checkPriceChange && operation.equals("min")){
                if (modelProduct.isWholesale()){
                    priceProduct = Integer.parseInt(modelProduct.getPrice_wholesale());
                    totalPriceCart -= priceProduct * (value + 1);
                    totalPriceCart += Integer.parseInt(modelProduct.getPrice_normal()) * (value + 1);
                }
                checkPriceChange = false;
            }

            priceProduct = Integer.parseInt(modelProduct.getPrice_normal());
            priceSale = priceProduct * value;
        }


        if (modelProduct.isWholesale()){
            if (value >= Integer.parseInt(modelProduct.getMin_buy_wholesale())){
                holder.wholeSale.setVisibility(View.VISIBLE);
                holder.textPriceNormal.setVisibility(View.VISIBLE);

                // check if the price change to wholesale
                if (checkPriceChange){
                    if (!modelProduct.getDisc().equals("-")){
                        float totalDisc = Integer.parseInt(modelProduct.getPrice_normal()) * (Float.parseFloat(modelProduct.getDisc()) / 100);
                        priceProduct = (Integer.parseInt(modelProduct.getPrice_normal()) - totalDisc);
                    }else {
                        priceProduct = Integer.parseInt(modelProduct.getPrice_normal());
                    }
                    totalPriceCart -= priceProduct * (value - 1);
                    totalPriceCart += Integer.parseInt(modelProduct.getPrice_wholesale()) * (value - 1);
                    checkPriceChange = false;
                }

                priceProduct = Integer.parseInt(modelProduct.getPrice_wholesale());
                priceStrike = Integer.parseInt(modelProduct.getPrice_normal()) * value;
                priceSale = priceProduct * value;
            }else {
                holder.wholeSale.setVisibility(View.GONE);
            }
        }

        holder.textPriceNormal.setText(Utility.currencyRp(String.valueOf(priceStrike)));
        holder.textPriceSale.setText(Utility.currencyRp(String.valueOf(priceSale)));
        holder.textValueProduct.setText(String.valueOf(value));


        if (operation.equals("default")){
            totalPriceCart += priceSale;
        }

        if (operation.equals("add")){
            totalPriceCart += priceProduct;
        }

        if (operation.equals("min")){
            totalPriceCart -= priceProduct;
        }

        if (holder.checkBoxProduct.isChecked()){
            setCartOrderPrice();
        }

    }

    private void confirmDeleteProductCart(String keyProduct){
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
            deleteProductCart(keyProduct);
            dialog.dismiss();
        });

        Cancel.setOnClickListener(v -> dialog.dismiss());
    }

    private void deleteProductCart(String key){
        reference.child("cart").child(Utility.uidCurrentUser).child(key).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                ((Activity) mContext).finish();
                mContext.startActivity(((Activity) mContext).getIntent());
                ((Activity) mContext).overridePendingTransition(R.anim.nav_default_enter_anim, R.anim.nav_default_pop_exit_anim);
            }else {
                Toast.makeText(mContext, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setCartOrderPrice(){
        textPriceCart.setText(Utility.currencyRp(String.valueOf(totalPriceCart)));
    }

}
