package com.ark.mainmarket.View.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.ark.mainmarket.Model.ModelCategory;
import com.ark.mainmarket.Model.ModelProduct;
import com.ark.mainmarket.Utility;
import com.ark.mainmarket.databinding.ActivityAddCategoryBinding;
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

public class AddCategory extends AppCompatActivity {

    private ActivityAddCategoryBinding binding;

    private final int PICK_IMAGE_GALLERY = 8888;
    private int onPickMedia = 0;
    private Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Utility.login();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        listenerClick();

    }

    private void listenerClick(){
        binding.selectIconBtn.setOnClickListener(view -> {
            if (ActivityCompat.checkSelfPermission(AddCategory.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                pickImageOnGalery();

            }else{
                ActivityCompat.requestPermissions(AddCategory.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, PICK_IMAGE_GALLERY);
            }
        });

        binding.previewIconBtn.setOnClickListener(view -> {
            if (binding.nameCategoryAdd.getText().toString().isEmpty()){
                binding.nameCategoryAdd.setError("Nama kategori kosong");
            }else {
                binding.previewNameCategory.setText(binding.nameCategoryAdd.getText().toString());
            }
        });

        binding.saveBtnCategory.setOnClickListener(view -> {
            if (binding.nameCategoryAdd.getText().toString().isEmpty()){
                binding.nameCategoryAdd.setError("Nama kategori kosong");
            }else {
                if (onPickMedia > 0){
                    binding.saveBtnCategory.setEnabled(false);
                    saveIconCategory();
                }else {
                    Utility.toastLS(AddCategory.this, "Anda belum mengupload icon");
                }
            }
        });
    }

    private void saveIconCategory() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault());
        Date now = new Date();
        String fileName = dateFormat.format(now);

        StorageReference storageRef = FirebaseStorage.getInstance().getReference("icon_categories/"+fileName);

        Bitmap bitmap = ((BitmapDrawable) binding.imageIcon.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = storageRef.putBytes(data);

        uploadTask.addOnFailureListener(e -> {
            Utility.toastLS(AddCategory.this, "Icon gagal diupload");
            binding.saveBtnCategory.setEnabled(true);
        }).addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> saveCategory(String.valueOf(uri))).addOnFailureListener(e -> {
            Utility.toastLS(AddCategory.this, "Url photo gagal di unduh");
            binding.saveBtnCategory.setEnabled(true);
        }));
    }

    private void saveCategory(String url) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        ModelCategory modelCategory = new ModelCategory(
                binding.nameCategoryAdd.getText().toString(),
                url
        );

        reference.child("category").push().setValue(modelCategory).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Utility.toastLS(AddCategory.this, "Berhasil menambahkan kateg" +
                        "ori");
                binding.saveBtnCategory.setEnabled(true);
            }else {
                Utility.toastLS(AddCategory.this, "Gagal menambahkan kategori");
                binding.saveBtnCategory.setEnabled(true);
            }
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
                    binding.imageIcon.setImageBitmap(bitmap);
                    onPickMedia += 1;

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}