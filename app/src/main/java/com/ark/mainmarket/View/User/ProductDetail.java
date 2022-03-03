package com.ark.mainmarket.View.User;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.ViewCompat;
import androidx.core.widget.ImageViewCompat;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ark.mainmarket.Model.ModelCategory;
import com.ark.mainmarket.Model.ModelProduct;
import com.ark.mainmarket.R;
import com.ark.mainmarket.Utility;
import com.ark.mainmarket.databinding.ActivityProductDetailBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import java.util.Locale;
import java.util.Objects;

public class ProductDetail extends AppCompatActivity {

    private ActivityProductDetailBinding binding;
    private BottomSheetDialog bottomSheetDialog;

    private String keyProduct;
    private String keyCategoryProduct;

    private final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    // bottom dialog component
    private TextView nameProductCart, priceSaleCart, priceWholeSale, valueProductCart;
    private ImageView imageProductCart, imageAddValue, imageMinValue;
    private CardView cardProduct;
    private Button addProductCartBtn;

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
    }

    private void listenerClick() {
        binding.cardBackBtn.setOnClickListener(view -> finish());

        bottomSheetDialog = new BottomSheetDialog(this);
        setBottomDialog();
        binding.cardAddCart.setOnClickListener(view -> bottomSheetDialog.show());

    }

    private void setBottomDialog(){
        // instance view bottom dialog
        View viewBottomDialog = getLayoutInflater().inflate(R.layout.layout_dialog_add_cart, null, false);

        // card view
        cardProduct = viewBottomDialog.findViewById(R.id.card_product_cart);

        // image
        imageProductCart = viewBottomDialog.findViewById(R.id.image_product_cart);
        imageAddValue = viewBottomDialog.findViewById(R.id.add_value_product);
        imageMinValue = viewBottomDialog.findViewById(R.id.min_value_product);

        // textview
        nameProductCart = viewBottomDialog.findViewById(R.id.name_product_cart);
        priceSaleCart = viewBottomDialog.findViewById(R.id.price_sale_cart);
        valueProductCart = viewBottomDialog.findViewById(R.id.value_product_tv);

        // button
        addProductCartBtn = viewBottomDialog.findViewById(R.id.add_product_cart_btn);

        addProductCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
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
                    binding.nameProductDetail.setText(modelProduct.getItem_name());

                    // set data product
                    if (!modelProduct.getDisc().equals("-")){
                        binding.priceNormalDetail.setVisibility(View.VISIBLE);
                        binding.priceNormalDetail.setText(Utility.currencyRp(modelProduct.getPrice_normal()));
                        binding.priceNormalDetail.setPaintFlags(binding.priceNormalDetail.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        float totalDisc = Integer.parseInt(modelProduct.getPrice_normal()) * (Float.parseFloat(modelProduct.getDisc()) / 100);
                        float priceSale = Integer.parseInt(modelProduct.getPrice_normal()) - totalDisc;
                        binding.priceSaleDetail.setText(Utility.currencyRp(String.valueOf(priceSale)));
                    }else {
                        binding.priceSaleDetail.setText(Utility.currencyRp(modelProduct.getPrice_normal()));
                    }

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
                    Picasso.get().load(modelProduct.getUrl_image()).into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            binding.imageProductDetail.setImageBitmap(bitmap);
                            imageProductCart.setImageBitmap(bitmap);
                        }
                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                            Utility.toastLS(ProductDetail.this, "Gambar produk gagal dimuat");

                        }
                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    });

                    // bottom dialog set data
                    nameProductCart.setText(modelProduct.getItem_name());
                    priceSaleCart.setText(binding.priceSaleDetail.getText().toString());

                    ViewCompat.setBackgroundTintList(cardProduct, ColorStateList.valueOf(Integer.parseInt(modelProduct.getColor_background())));
                    ViewCompat.setBackgroundTintList(addProductCartBtn, ColorStateList.valueOf(Integer.parseInt(modelProduct.getColor_background())));

                }else {
                    Utility.toastLS(ProductDetail.this, "Product tidak ditemukan");
                }
            }else {
                Utility.toastLS(ProductDetail.this, Objects.requireNonNull(task.getException()).getMessage());
            }
        });
    }


}