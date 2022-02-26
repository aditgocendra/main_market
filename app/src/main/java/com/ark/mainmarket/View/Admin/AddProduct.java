package com.ark.mainmarket.View.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.ark.mainmarket.Model.ModelProduct;
import com.ark.mainmarket.R;
import com.ark.mainmarket.Utility;
import com.ark.mainmarket.databinding.ActivityProductAddBinding;
import com.ark.mainmarket.databinding.LayoutDialogPromoBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import petrov.kristiyan.colorpicker.ColorPicker;

public class AddProduct extends AppCompatActivity {

    private ActivityProductAddBinding binding;

    private final int PICK_IMAGE_GALLERY = 8888;
    private int onPickMedia = 0;
    private Uri fileUri;

    private BottomSheetDialog bottomSheetDialog;

    private SwitchMaterial switchFreeSending;
    private SwitchMaterial switchWholeSaleProduct;
    private TextInputEditText priceWholesaleTiAdd;
    private TextInputEditText discProduct;
    private Button finishPromoBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Utility.checkWindowSetFlag(this);
//        binding.cardThumbsImage.setBackgroundResource(R.color.blue_product);

        binding.imageProductAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImageOnGalery();
            }
        });


        binding.saveProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.nameItemTiAdd.getText().toString().isEmpty()){
                    binding.nameItemTiAdd.setError("Tidak boleh kosong");
                }else if (binding.priceItemNormalTiAdd.getText().toString().isEmpty()){
                    binding.priceItemNormalTiAdd.setError("Tidak boleh kosong");
                }else {
                    if (onPickMedia > 0){
                        binding.saveProduct.setEnabled(false);
                        saveImageProduct();
                    }else {
                        Toast.makeText(AddProduct.this, "Anda belum mengupload photo", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        setItemAutoComplete();


        bottomSheetDialog = new BottomSheetDialog(AddProduct.this);
        setBottomDialog();
        binding.promoSaleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.show();

            }
        });

//      color picker
        binding.colorPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });

    }

    private void setBottomDialog(){
        View viewBottomDialog = getLayoutInflater().inflate(R.layout.layout_dialog_promo, null, false);
        SwitchMaterial switchFreeSending = viewBottomDialog.findViewById(R.id.switch_free_sending);
        SwitchMaterial switchWholeSaleProduct = viewBottomDialog.findViewById(R.id.switch_wholesale_product);
        TextInputEditText priceWholesaleTiAdd = viewBottomDialog.findViewById(R.id.price_wholesale_ti_add);
        TextInputEditText minBuyWholesale = viewBottomDialog.findViewById(R.id.min_buy_wholesale);
        AutoCompleteTextView autoCompleteDisc = viewBottomDialog.findViewById(R.id.auto_complete_disc);
        Button finishPromoBtn = viewBottomDialog.findViewById(R.id.finish_promo_btn);

        String[] disc = {"10", "20", "50", "70", "90"};
        ArrayAdapter<String> discAdapter;
        discAdapter = new ArrayAdapter<>(this, R.layout.layout_option_item, disc);
        autoCompleteDisc.setAdapter(discAdapter);
        autoCompleteDisc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
            }
        });

        switchWholeSaleProduct.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked){
                    priceWholesaleTiAdd.setEnabled(true);
                }else {
                    priceWholesaleTiAdd.setEnabled(false);
                }
            }
        });

        finishPromoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.setContentView(viewBottomDialog);
    }

    private void setItemAutoComplete(){

        String[] condition = {"Baru", "Bekas"};
        ArrayAdapter<String> conditionAdapter;
        conditionAdapter = new ArrayAdapter<>(this, R.layout.layout_option_item, condition);
        binding.autoCompleteCondition.setAdapter(conditionAdapter);
        binding.autoCompleteCondition.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
            }
        });

        String[] stock = {"> 10", "> 100", "> 1000"};
        ArrayAdapter<String> stockAdapter;
        stockAdapter = new ArrayAdapter<>(this, R.layout.layout_option_item, stock);
        binding.autoCompleteStock.setAdapter(stockAdapter);
        binding.autoCompleteStock.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
            }
        });

    }



    private void pickImageOnGalery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 8888);
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

        Bitmap bitmap = ((BitmapDrawable) binding.imageProductAdd.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = storageRef.putBytes(data);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddProduct.this, "Photo gagal diupload", Toast.LENGTH_SHORT).show();
                binding.saveProduct.setEnabled(true);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
//                        saveProduct(String.valueOf(uri));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddProduct.this, "Url photo gagal didownload", Toast.LENGTH_SHORT).show();
                        binding.saveProduct.setEnabled(true);
                    }
                });
            }
        });
    }

//    private void saveProduct(String url){
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
//
//        ModelProduct modelProduct = new ModelProduct(
//          binding.nameItemTiAdd.getText().toString(),
//          binding.priceItemNormalTiAdd.getText().toString(),
//          binding.descriptionItemTiAdd.getText().toString(),
//          url
//        );
//
//        reference.child("product").push().setValue(modelProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()){
//                    Toast.makeText(AddProduct.this, "Berhasil menambahkan data", Toast.LENGTH_SHORT).show();
//                    binding.saveProduct.setEnabled(true);
//                }else {
//                    Toast.makeText(AddProduct.this, "Gagal menambahkan data", Toast.LENGTH_SHORT).show();
//                    binding.saveProduct.setEnabled(true);
//                }
//            }
//        });
//    }
}