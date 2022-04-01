package com.ark.mainmarket.View.User;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.ViewCompat;
import androidx.core.widget.ImageViewCompat;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.ark.mainmarket.Model.ModelCategory;
import com.ark.mainmarket.Model.ModelProduct;
import com.ark.mainmarket.Model.ModelShopCart;
import com.ark.mainmarket.R;
import com.ark.mainmarket.Utility;
import com.ark.mainmarket.databinding.ActivityProductDetailBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;


import java.util.Locale;
import java.util.Objects;

public class ProductDetail extends AppCompatActivity {

    private ActivityProductDetailBinding binding;
    private BottomSheetDialog bottomSheetDialog;

    private String keyProduct;
    private String keyCategoryProduct;

    private final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    private boolean userFav = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Utility.checkWindowSetFlag(this);

        keyProduct = getIntent().getStringExtra("key_product");
        keyCategoryProduct = getIntent().getStringExtra("key_category");

        listenerClick();

        setProductDetail();
        setCategoryProductDetail();
        setProductFavorite();
    }

    private void listenerClick() {
        binding.cardBackBtn.setOnClickListener(view -> finish());

        bottomSheetDialog = new BottomSheetDialog(this);
        binding.cardAddCart.setOnClickListener(view -> bottomSheetDialog.show());
        binding.cardMyCart.setOnClickListener(view -> Utility.updateUI(ProductDetail.this, Cart.class));

        binding.cardWhatsapp.setOnClickListener(view -> {
            String url = "https://api.whatsapp.com/send?phone=" + "+62 896-9947-1130" + "&text=" + "Hi saya tertarik dengan produk anda, apakah saya bisa memesannya sekarang?";

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);

        });

        binding.cardFav.setOnClickListener(view -> {
            if (userFav){
                removeFavorite();
            }else {
                addFavorite();
            }
        });

    }

    private void setBottomDialog(ModelProduct modelProduct, float priceSaleCount){
        // instance view bottom dialog
        View viewBottomDialog = getLayoutInflater().inflate(R.layout.layout_dialog_add_cart, null, false);

        // card view
        CardView cardProduct = viewBottomDialog.findViewById(R.id.card_product_cart);

        // image
        ImageView imageProductCart, imageAddValue, imageMinValue;
        imageProductCart = viewBottomDialog.findViewById(R.id.image_product_cart);
        imageAddValue = viewBottomDialog.findViewById(R.id.add_value_product);
        imageMinValue = viewBottomDialog.findViewById(R.id.min_value_product);

        // textview
        TextView nameProductCart, priceSaleCart, valueProductCart, priceWholeSale, freeSending;
        nameProductCart = viewBottomDialog.findViewById(R.id.name_product_cart);
        priceSaleCart = viewBottomDialog.findViewById(R.id.price_sale_cart);
        valueProductCart = viewBottomDialog.findViewById(R.id.value_product_tv);
        priceWholeSale = viewBottomDialog.findViewById(R.id.price_wholesale);
        freeSending = viewBottomDialog.findViewById(R.id.free_sending_cart);

        // button
        Button addProductCartBtn = viewBottomDialog.findViewById(R.id.add_product_cart_btn);

        // bottom dialog set data
        Picasso.get().load(modelProduct.getUrl_image()).into(imageProductCart);
        nameProductCart.setText(modelProduct.getItem_name());
        priceSaleCart.setText(Utility.currencyRp(String.valueOf(priceSaleCount)));

        ViewCompat.setBackgroundTintList(cardProduct, ColorStateList.valueOf(Integer.parseInt(modelProduct.getColor_background())));
        ViewCompat.setBackgroundTintList(addProductCartBtn, ColorStateList.valueOf(Integer.parseInt(modelProduct.getColor_background())));

        if (modelProduct.isFree_sending()){
            freeSending.setVisibility(View.VISIBLE);
        }else {
            freeSending.setVisibility(View.GONE);
        }

        imageAddValue.setOnClickListener(view -> {
            int value = Integer.parseInt(valueProductCart.getText().toString());
            int stock = Integer.parseInt(modelProduct.getStock());

            float priceResult = 0;

            if (value < stock){
                value += 1;

                if (modelProduct.isWholesale()){
                    if (value >= Integer.parseInt(modelProduct.getMin_buy_wholesale())){
                        priceWholeSale.setVisibility(View.VISIBLE);
                        priceSaleCart.setText(Utility.currencyRp(modelProduct.getPrice_wholesale()));
                        priceResult = Integer.parseInt(modelProduct.getPrice_wholesale()) * value;
                    }else {
                        priceWholeSale.setVisibility(View.GONE);
                        priceSaleCart.setText(Utility.currencyRp(String.valueOf(priceSaleCount)));
                        priceResult = priceSaleCount * value;
                    }
                }else {
                    priceResult = priceSaleCount * value;
                }

                valueProductCart.setText(String.valueOf(value));
                priceSaleCart.setText(Utility.currencyRp(String.valueOf(priceResult)));
            }else {
                Utility.toastLS(ProductDetail.this, "Jumlah penambahan sudah mencapai batas stok produk");
            }

        });

        imageMinValue.setOnClickListener(view -> {
            int value = Integer.parseInt(valueProductCart.getText().toString());
            float priceResult = 0;

            if (value > 1){
                value -= 1;

                if (modelProduct.isWholesale()){
                    if (value >= Integer.parseInt(modelProduct.getMin_buy_wholesale())){
                        priceWholeSale.setVisibility(View.VISIBLE);
                        priceSaleCart.setText(Utility.currencyRp(modelProduct.getPrice_wholesale()));
                        priceResult = Integer.parseInt(modelProduct.getPrice_wholesale()) * value;

                    }else {
                        priceWholeSale.setVisibility(View.GONE);
                        priceSaleCart.setText(Utility.currencyRp(String.valueOf(priceSaleCount)));
                        priceResult = priceSaleCount * value;
                    }
                }else {
                    priceResult = priceSaleCount * value;
                }

                valueProductCart.setText(String.valueOf(value));
                priceSaleCart.setText(Utility.currencyRp(String.valueOf(priceResult)));
            }else {
                Utility.toastLS(ProductDetail.this, "Jumlah pengurangan sudah mencapai batas");
            }
        });

        addProductCartBtn.setOnClickListener(view -> {
            addProductToCart(modelProduct.getKey(), valueProductCart.getText().toString());
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.setContentView(viewBottomDialog);

    }

    private void setCategoryProductDetail() {
        reference.child("category").child(keyCategoryProduct).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                ModelCategory modelCategory = task.getResult().getValue(ModelCategory.class);
                if (modelCategory != null){
                    binding.categoryProductDetail.setText(modelCategory.getName_category());
                    binding.textSearchDetailProduct.setText("Cari "+modelCategory.getName_category().toLowerCase(Locale.ROOT));
                    binding.productCategoryTv.setText("Produk "+modelCategory.getName_category());
                }
            }else {
                Utility.toastLS(ProductDetail.this, task.getException().getMessage());
            }
        });
    }

    private void setProductDetail() {
        reference.child("product").child(keyProduct).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                ModelProduct modelProduct = task.getResult().getValue(ModelProduct.class);
                if (modelProduct != null){
                    modelProduct.setKey(task.getResult().getKey());
                    binding.nameProductDetail.setText(modelProduct.getItem_name());

                    float priceSaleCount;
                    // set data product
                    if (!modelProduct.getDisc().equals("-")){
                        binding.priceNormalDetail.setVisibility(View.VISIBLE);
                        binding.priceNormalDetail.setText(Utility.currencyRp(modelProduct.getPrice_normal()));
                        binding.priceNormalDetail.setPaintFlags(binding.priceNormalDetail.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        float totalDisc = Integer.parseInt(modelProduct.getPrice_normal()) * (Float.parseFloat(modelProduct.getDisc()) / 100);
                        priceSaleCount = Integer.parseInt(modelProduct.getPrice_normal()) - totalDisc;
                    }else {
                        priceSaleCount = Float.parseFloat(modelProduct.getPrice_normal());
                    }

                    binding.priceSaleDetail.setText(Utility.currencyRp(String.valueOf(priceSaleCount)));

                    if (modelProduct.isFree_sending()){
                        binding.freeSendingDetail.setVisibility(View.VISIBLE);
                    }else {
                        binding.freeSendingDetail.setVisibility(View.GONE);
                    }

                    if (modelProduct.isWholesale()){
                        binding.wholesaleLayout.setVisibility(View.VISIBLE);
                        binding.wholesaleDetail.setText(
                                "Beli >= "+modelProduct.getMin_buy_wholesale()+" ("+Utility.currencyRp(modelProduct.getPrice_wholesale())+")");
                    }else {
                        binding.wholesaleLayout.setVisibility(View.GONE);
                    }

                    // detail produk
                    binding.conditionProduct.setText(modelProduct.getCondition());
                    binding.stockProductDetail.setText(modelProduct.getStock());
                    binding.descProductDetail.setText(modelProduct.getDescription());

                    // set color
                    ViewCompat.setBackgroundTintList(binding.layoutProductDetail, ColorStateList.valueOf(Integer.parseInt(modelProduct.getColor_background())));
                    ViewCompat.setBackgroundTintList(binding.orderBtn, ColorStateList.valueOf(Integer.parseInt(modelProduct.getColor_background())));

                    ImageViewCompat.setImageTintList(binding.addCartImg, ColorStateList.valueOf(Integer.parseInt(modelProduct.getColor_background())));
                    ImageViewCompat.setImageTintList(binding.whatsappImg, ColorStateList.valueOf(Integer.parseInt(modelProduct.getColor_background())));

                    // set image
                    Picasso.get().load(modelProduct.getUrl_image()).into(binding.imageProductDetail);

                    setBottomDialog(modelProduct, priceSaleCount);

                }else {
                    Utility.toastLS(ProductDetail.this, "Product tidak ditemukan");
                }
            }else {
                Utility.toastLS(ProductDetail.this, Objects.requireNonNull(task.getException()).getMessage());
            }
        });
    }

    private void addProductToCart(String keyProduct, String totalProduct){
        ModelShopCart modelShopCart = new ModelShopCart(keyProduct, totalProduct);
        reference.child("cart").child(Utility.uidCurrentUser).child(keyProduct).setValue(modelShopCart).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Utility.toastLS(ProductDetail.this, "Berhasil menambahkan produk ke dalam keranjang");
            }else {
                Utility.toastLS(ProductDetail.this, task.getException().getMessage());
            }
        });
    }

    private void setProductFavorite(){
        // user fav
        reference.child("product_favorite").child(Utility.uidCurrentUser).child(keyProduct).child("my_fav").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Boolean my_fav = task.getResult().getValue(Boolean.class);
                if (my_fav != null){
                    binding.favImage.setImageResource(R.drawable.ic_favorite);
                    ImageViewCompat.setImageTintList(binding.favImage, ColorStateList.valueOf(getResources().getColor(R.color.red_product)));
                    userFav = true;
                }
            }
        });

    }

    private void addFavorite(){
        reference.child("product_favorite").child(Utility.uidCurrentUser).child(keyProduct).child("my_fav").setValue(true).addOnSuccessListener(unused -> {
            binding.favImage.setImageResource(R.drawable.ic_favorite);
            ImageViewCompat.setImageTintList(binding.favImage, ColorStateList.valueOf(getResources().getColor(R.color.red_product)));
            userFav = true;
        }).addOnFailureListener(e -> Utility.toastLS(ProductDetail.this, "Gagal menambahkan item ke favorit"));
    }

    private void removeFavorite(){
        reference.child("product_favorite").child(Utility.uidCurrentUser).child(keyProduct).removeValue().addOnSuccessListener(unused -> {
            Utility.toastLS(ProductDetail.this, "Berhasil menghapus item dari favorit");
            binding.favImage.setImageResource(R.drawable.ic_favorite_border);
            ImageViewCompat.setImageTintList(binding.favImage, ColorStateList.valueOf(getResources().getColor(R.color.gray)));
            userFav = false;
        });
    }


}