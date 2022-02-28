package com.ark.mainmarket.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.ark.mainmarket.Model.ModelCategory;
import com.ark.mainmarket.R;
import com.ark.mainmarket.Utility;
import com.ark.mainmarket.View.Admin.EditCategory;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AdapterManageCategory extends RecyclerView.Adapter<AdapterManageCategory.MyViewHolder> {

    private final Context mContext;
    private final List<ModelCategory> modelCategoryList = new ArrayList<>();

    public AdapterManageCategory(Context mContext){
        this.mContext = mContext;

    }

    public void setItem(List<ModelCategory> listCategory){
        modelCategoryList.addAll(listCategory);
    }

    @NonNull
    @Override
    public AdapterManageCategory.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.layout_manage_category, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterManageCategory.MyViewHolder holder, int position) {
        ModelCategory modelCategory = modelCategoryList.get(position);

        Picasso.get().load(modelCategory.getIcon_link()).into(holder.iconCategory);
        holder.nameCategory.setText(modelCategory.getName_category());

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
                deleteCategory(modelCategory.getKey(), modelCategory.getIcon_link(), position);
                dialog.dismiss();
            });

            Cancel.setOnClickListener(v -> dialog.dismiss());
        });

        holder.cardEdit.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, EditCategory.class);
            intent.putExtra("key_category", modelCategory.getKey());
            intent.putExtra("name_category", modelCategory.getName_category());
            intent.putExtra("icon_url", modelCategory.getIcon_link());
            mContext.startActivity(intent);
        });
    }
    


    @Override
    public int getItemCount() {
        return modelCategoryList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iconCategory;
        TextView nameCategory;
        CardView cardEdit, cardDelete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            iconCategory = itemView.findViewById(R.id.icon_category_manage);
            iconCategory.setBackgroundColor(Color.TRANSPARENT);
            nameCategory = itemView.findViewById(R.id.name_category_manage);
            cardEdit = itemView.findViewById(R.id.card_edit_category);
            cardDelete = itemView.findViewById(R.id.card_delete_category);

        }
    }

    private void deleteCategory(String key, String urlPhoto, int pos){
        DatabaseReference referenceDatabase = FirebaseDatabase.getInstance().getReference();

        FirebaseStorage referenceStorage = FirebaseStorage.getInstance();
        String name_photo = referenceStorage.getReferenceFromUrl(urlPhoto).getName();
        StorageReference deleteRef = referenceStorage.getReference("icon_categories/"+name_photo);

        deleteRef.delete().addOnCompleteListener(taskStorage -> {
            if (taskStorage.isSuccessful()){
                referenceDatabase.child("category").child(key).removeValue().addOnCompleteListener(taskDatabase -> {
                    if (taskDatabase.isSuccessful()){
                        Utility.toastLS(mContext, "Berhasil menghapus kategori");
                        modelCategoryList.remove(pos);
                        this.notifyDataSetChanged();
                    }else {
                        Utility.toastLS(mContext, Objects.requireNonNull(taskDatabase.getException()).getMessage());
                    }
                });
            }else {
                Utility.toastLS(mContext, Objects.requireNonNull(taskStorage.getException()).getMessage());
            }
        });
    }
}
