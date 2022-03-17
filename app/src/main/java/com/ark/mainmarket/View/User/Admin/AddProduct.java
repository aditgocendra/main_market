package com.ark.mainmarket.View.User.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;
import com.ark.mainmarket.Model.ModelCategory;
import com.ark.mainmarket.Model.ModelProduct;
import com.ark.mainmarket.R;
import com.ark.mainmarket.Utility;
import com.ark.mainmarket.databinding.ActivityAddProductBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import petrov.kristiyan.colorpicker.ColorPicker;

public class AddProduct extends AppCompatActivity {

    private ActivityAddProductBinding binding;

    // pick image gallery
    private final int PICK_IMAGE_GALLERY = 8888;
    private int onPickMedia = 0;
    private Uri fileUri;

    // bottom dialog
    private BottomSheetDialog bottomSheetDialog;
    private TextInputEditText priceWholesaleTiAdd, minBuyWholesale;
    private SwitchMaterial switchFreeSending, switchWholeSaleProduct;
    private AutoCompleteTextView autoCompleteDisc;
    private String categorySelectKey = null;

    // database
    private final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Utility.checkWindowSetFlag(this);
        listenerClick();

    }

    private void listenerClick() {
        binding.backBtn.setOnClickListener(view -> finish());

        binding.imageProductAdd.setOnClickListener(view -> pickImageOnGalery());

        binding.saveProduct.setOnClickListener(view -> {
            String nameItem = binding.nameItemTiAdd.getText().toString();
            String priceNormalItem = binding.priceItemNormalTiAdd.getText().toString();
            String condition = binding.autoCompleteCondition.getText().toString();
            String stock = binding.stockProductAdd.getText().toString();
            String desc = binding.descriptionItemTiAdd.getText().toString();

            if (nameItem.isEmpty()){
                binding.nameItemTiAdd.setError("Tidak boleh kosong");
            }else if (priceNormalItem.isEmpty()){
                binding.priceItemNormalTiAdd.setError("Tidak boleh kosong");
            }else if (condition.isEmpty()){
                binding.autoCompleteCondition.setError("Tidak boleh kosong");
            }else if (stock.isEmpty()){
                binding.stockProductAdd.setError("Tidak boleh kosong");
            }else if (desc.isEmpty()){
                binding.descriptionItemTiAdd.setError("Tidak boleh kosong");
            }else if (categorySelectKey == null){
                Utility.toastLS(AddProduct.this, "Anda belum memilih kategori");
            }else {
                if (onPickMedia > 0){
                    binding.saveProduct.setEnabled(false);
                    binding.progressCircular.setVisibility(View.VISIBLE);
                    saveImageProduct();
                }else {
                    Toast.makeText(AddProduct.this, "Anda belum mengupload photo", Toast.LENGTH_SHORT).show();
                }
            }
        });

        setItemAutoComplete();

        bottomSheetDialog = new BottomSheetDialog(AddProduct.this);
        setBottomDialog();
        binding.promoSaleBtn.setOnClickListener(view -> bottomSheetDialog.show());

//      color picker
        binding.colorPicker.setOnClickListener(view -> {
            ColorPicker colorPicker = new ColorPicker(AddProduct.this);
            colorPicker.show();
            colorPicker.setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                @Override
                public void onChooseColor(int position,int color) {
                    binding.cardThumbsImage.setCardBackgroundColor(color);
                }

                @Override
                public void onCancel(){
                    colorPicker.dismissDialog();

                }
            });
        });
    }

    private void setBottomDialog(){
        View viewBottomDialog = getLayoutInflater().inflate(R.layout.layout_dialog_promo, null, false);
        switchFreeSending = viewBottomDialog.findViewById(R.id.switch_free_sending);
        switchWholeSaleProduct = viewBottomDialog.findViewById(R.id.switch_wholesale_product);
        priceWholesaleTiAdd = viewBottomDialog.findViewById(R.id.price_wholesale_ti_add);
        minBuyWholesale = viewBottomDialog.findViewById(R.id.min_buy_wholesale);
        autoCompleteDisc = viewBottomDialog.findViewById(R.id.auto_complete_disc);

        TextInputLayout layoutPriceWholesaleTiAdd = viewBottomDialog.findViewById(R.id.layout_price_wholesale_ti_add);
        TextInputLayout layoutMinBuyWholesale = viewBottomDialog.findViewById(R.id.layout_min_buy_wholesale);
        Button finishPromoBtn = viewBottomDialog.findViewById(R.id.finish_promo_btn);

        String[] disc = {"-", "5", "10", "20", "50", "70", "90"};
        ArrayAdapter<String> discAdapter;
        discAdapter = new ArrayAdapter<>(this, R.layout.layout_option_item, disc);
        autoCompleteDisc.setAdapter(discAdapter);

        switchWholeSaleProduct.setOnCheckedChangeListener((compoundButton, checked) -> {
            if (checked){
                layoutPriceWholesaleTiAdd.setVisibility(View.VISIBLE);
                layoutMinBuyWholesale.setVisibility(View.VISIBLE);
            }else {
                layoutPriceWholesaleTiAdd.setVisibility(View.GONE);
                layoutMinBuyWholesale.setVisibility(View.GONE);
            }
        });

        finishPromoBtn.setOnClickListener(view -> bottomSheetDialog.dismiss());

        bottomSheetDialog.setContentView(viewBottomDialog);
    }

    private void setItemAutoComplete(){
        String[] condition = {"Baru", "Bekas"};
        ArrayAdapter<String> conditionAdapter;
        conditionAdapter = new ArrayAdapter<>(this, R.layout.layout_option_item, condition);
        binding.autoCompleteCondition.setAdapter(conditionAdapter);



        setCategoryProduct();

    }

    private void setCategoryProduct() {
        ArrayAdapter<ModelCategory> categoryAdapter = new ArrayAdapter<>(this, R.layout.layout_option_item);
        reference.child("category").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    ModelCategory modelCategory = ds.getValue(ModelCategory.class);
                    modelCategory.setKey(ds.getKey());
                    categoryAdapter.add(modelCategory);
                }
                binding.autoCompleteCategory.setAdapter(categoryAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Utility.toastLS(AddProduct.this, "Category Database"+error.getMessage());
            }
        });

        binding.autoCompleteCategory.setOnItemClickListener((adapterView, view, i, l) -> {
            ModelCategory modelCategory = (ModelCategory) adapterView.getItemAtPosition(i);
            categorySelectKey = modelCategory.getKey();
        });
    }

    private void pickImageOnGalery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_GALLERY && resultCode == RESULT_OK){
            assert data != null;
            if (data.getData() != null){
                fileUri = data.getData();
                try {
                    InputStream is = getContentResolver().openInputStream(fileUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    binding.imageProductAdd.setImageBitmap(bitmap);
                    onPickMedia += 1;

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    private void saveImageProduct(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault());
        Date now = new Date();
        String fileName = dateFormat.format(now);

        StorageReference storageRef = FirebaseStorage.getInstance().getReference("product_image/"+fileName);
        UploadTask uploadTask = storageRef.putFile(fileUri);

        uploadTask.addOnFailureListener(e -> {
            Toast.makeText(AddProduct.this, "Photo gagal diupload", Toast.LENGTH_SHORT).show();
            binding.progressCircular.setVisibility(View.GONE);
            binding.saveProduct.setEnabled(true);
        }).addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri ->
                saveProduct(String.valueOf(uri))).addOnFailureListener(e -> {
            Toast.makeText(AddProduct.this, "Url photo gagal didownload", Toast.LENGTH_SHORT).show();
            binding.progressCircular.setVisibility(View.GONE);
            binding.saveProduct.setEnabled(true);
        }));
    }

    private void saveProduct(String url){
        String nameItem = binding.nameItemTiAdd.getText().toString();
        String priceNormalItem = binding.priceItemNormalTiAdd.getText().toString();
        String condition = binding.autoCompleteCondition.getText().toString();
        String stock = binding.stockProductAdd.getText().toString();
        String desc = binding.descriptionItemTiAdd.getText().toString();
        boolean freeSending = false, wholeSale = false;
        String priceWholeSale = "-", minWholeSale= "-";
        String disc = autoCompleteDisc.getText().toString();
        String backgroundColorProduct = String.valueOf(binding.cardThumbsImage.getCardBackgroundColor().getDefaultColor());

        if (switchFreeSending.isChecked()){
            freeSending = true;
        }

        if (switchWholeSaleProduct.isChecked()){
            wholeSale = true;
            priceWholeSale = priceWholesaleTiAdd.getText().toString();
            minWholeSale = minBuyWholesale.getText().toString();
        }

        ModelProduct modelProduct = new ModelProduct(
          nameItem,
          priceNormalItem,
          categorySelectKey,
          condition,
          stock, desc, url, backgroundColorProduct, freeSending, wholeSale, priceWholeSale, minWholeSale, disc

        );

        reference.child("product").push().setValue(modelProduct).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Toast.makeText(AddProduct.this, "Berhasil menambahkan data", Toast.LENGTH_SHORT).show();
                finish();
            }else {
                Toast.makeText(AddProduct.this, "Gagal menambahkan data", Toast.LENGTH_SHORT).show();
            }
            binding.progressCircular.setVisibility(View.GONE);
            binding.saveProduct.setEnabled(true);
        });
    }
}