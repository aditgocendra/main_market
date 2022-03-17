package com.ark.mainmarket.View.User.Admin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import com.ark.mainmarket.Model.ModelCategory;
import com.ark.mainmarket.Utility;
import com.ark.mainmarket.databinding.ActivityEditCategoryBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class EditCategory extends AppCompatActivity {

    private ActivityEditCategoryBinding binding;

    private final int PICK_IMAGE_GALLERY = 8888;
    private Uri fileUri;
    private int onPickMedia = 0;

    private final FirebaseStorage referenceStorage = FirebaseStorage.getInstance();
    private final DatabaseReference referenceDatabase = FirebaseDatabase.getInstance().getReference();

    private String keyCategory, nameCategory, iconUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Utility.checkWindowSetFlag(this);
        listenerClick();

        keyCategory = getIntent().getStringExtra("key_category");
        nameCategory = getIntent().getStringExtra("name_category");
        iconUrl = getIntent().getStringExtra("icon_url");
        setDataCategory();
    }


    private void listenerClick() {
        binding.backBtn.setOnClickListener(view -> finish());

        binding.selectIconBtn.setOnClickListener(view -> pickImageOnGalery());

        binding.saveBtnCategory.setOnClickListener(view -> {
            String newNameCategory = Objects.requireNonNull(binding.nameCategoryEdit.getText()).toString();

            if (newNameCategory.isEmpty()){
                Utility.toastLS(EditCategory.this, "Anda belum mengisi nama kategori");
            }else {
                binding.saveBtnCategory.setEnabled(false);
                binding.progressCircular.setVisibility(View.VISIBLE);
                if (onPickMedia > 0){
                    updateWithDeletePhoto(iconUrl);
                }else {
                    editCategory(iconUrl);
                }
            }
        });

    }

    private void setDataCategory() {
        Picasso.get().load(iconUrl).into(binding.imageIcon);
        binding.previewNameCategory.setText(nameCategory);
        binding.nameCategoryEdit.setText(nameCategory);
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

    private void updateWithDeletePhoto(String urlPhoto){
        String name_photo = referenceStorage.getReferenceFromUrl(urlPhoto).getName();
        StorageReference deleteRef = referenceStorage.getReference("icon_categories/"+name_photo);

        deleteRef.delete().addOnCompleteListener(taskStorage -> {
            if (taskStorage.isSuccessful()){
                editIconCategory();
            }else {
                Utility.toastLS(EditCategory.this, Objects.requireNonNull(taskStorage.getException()).getMessage());
                binding.saveBtnCategory.setEnabled(true);
                binding.progressCircular.setVisibility(View.GONE);
            }
        });
    }

    private void editIconCategory() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault());
        Date now = new Date();
        String fileName = dateFormat.format(now);

        StorageReference storageRef = FirebaseStorage.getInstance().getReference("icon_categories/"+fileName);
        UploadTask uploadTask = storageRef.putFile(fileUri);

        uploadTask.addOnFailureListener(e -> {
            Utility.toastLS(EditCategory.this, "Icon gagal diupload");
            binding.saveBtnCategory.setEnabled(true);
            binding.progressCircular.setVisibility(View.GONE);
        }).addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> editCategory(String.valueOf(uri))).addOnFailureListener(e -> {
            Utility.toastLS(EditCategory.this, "Url photo gagal di unduh");
            binding.saveBtnCategory.setEnabled(true);
            binding.progressCircular.setVisibility(View.GONE);
        }));
    }

    private void editCategory(String url) {
        ModelCategory modelCategory = new ModelCategory(
                Objects.requireNonNull(binding.nameCategoryEdit.getText()).toString(),
                url
        );

        referenceDatabase.child("category").child(keyCategory).setValue(modelCategory).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Utility.toastLS(EditCategory.this, "Berhasil mengubah kategori");
                binding.saveBtnCategory.setEnabled(true);
                binding.progressCircular.setVisibility(View.GONE);
                Utility.updateUI(EditCategory.this, ManageCategory.class);
                finish();
            }else {
                Utility.toastLS(EditCategory.this, Objects.requireNonNull(task.getException()).getMessage());
                binding.saveBtnCategory.setEnabled(true);
                binding.progressCircular.setVisibility(View.GONE);
            }
        });
    }
}