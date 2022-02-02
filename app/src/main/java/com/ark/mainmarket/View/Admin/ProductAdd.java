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
import android.view.View;
import android.widget.Toast;

import com.ark.mainmarket.Model.ModelProduct;
import com.ark.mainmarket.databinding.ActivityProductAddBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

public class ProductAdd extends AppCompatActivity {

    private ActivityProductAddBinding binding;

    private final int PICK_IMAGE_GALLERY = 8888;
    private int onPickMedia = 0;
    private Uri fileUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.cardPickImage.setOnClickListener(new View.OnClickListener() {
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
                }else if (binding.priceItemWholesaleTiAdd.getText().toString().isEmpty()){
                    binding.descriptionItemTiAdd.setError("Tidak boleh kosong");
                }else {
                    if (onPickMedia > 0){
                        binding.saveProduct.setEnabled(false);
                        saveImageProduct();
                    }else {
                        Toast.makeText(ProductAdd.this, "Anda belum mengupload photo", Toast.LENGTH_SHORT).show();
                    }

                }
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
                Toast.makeText(ProductAdd.this, "Photo gagal diupload", Toast.LENGTH_SHORT).show();
                binding.saveProduct.setEnabled(true);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        saveProduct(String.valueOf(uri));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProductAdd.this, "Url photo gagal didownload", Toast.LENGTH_SHORT).show();
                        binding.saveProduct.setEnabled(true);
                    }
                });
            }
        });
    }

    private void saveProduct(String url){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        ModelProduct modelProduct = new ModelProduct(
          binding.nameItemTiAdd.getText().toString(),
          binding.priceItemNormalTiAdd.getText().toString(),
          binding.priceItemWholesaleTiAdd.getText().toString(),
          binding.descriptionItemTiAdd.getText().toString(),
          url
        );

        reference.child("product").push().setValue(modelProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(ProductAdd.this, "Berhasil menambahkan data", Toast.LENGTH_SHORT).show();
                    binding.saveProduct.setEnabled(true);
                }else {
                    Toast.makeText(ProductAdd.this, "Gagal menambahkan data", Toast.LENGTH_SHORT).show();
                    binding.saveProduct.setEnabled(true);
                }
            }
        });
    }
}