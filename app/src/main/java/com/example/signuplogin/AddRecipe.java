package com.example.signuplogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;

import java.io.IOException;

public class AddRecipe extends AppCompatActivity {

    private static final String TAG = "AddRestActivity";
    private EditText etName, etDesc, etAddress, etPhone;
    private Spinner spCat;
    private ImageView ivPhoto;
    private FirebaseServices fbs;
    private static final String TAG = "AddRestActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

    }
    public class Recipes {
        private String name;
        private String description;
        private String address;
        private String phone;
        private  RecipeType category;
        private String photo;

        @Override
        public String toString() {
            return "Recipes{" +
                    "name='" + name + '\'' +
                    ", description='" + description + '\'' +
                    ", address='" + address + '\'' +
                    ", phone='" + phone + '\'' +
                    ", category=" + category +
                    ", photo='" + photo + '\'' +
                    '}';
        }
        private void connectComponents() {
            etName = findViewById(R.id.TvRecipeNameAddRecipe);
            etDesc = findViewById(R.id.MACTVinfoAddRecipe);
            etAddress = findViewById(R.id.PTaddressAddRecipe);
            etPhone = findViewById(R.id.ETphoneAddRecipe);
            spCat = findViewById(R.id.sprChooseAddRecipe);
            ivPhoto = findViewById(R.id.IvPhotoAddRecipe);
            fbs = FirebaseServices.getInstance();
            spCat.setAdapter(new ArrayAdapter<RecipeType>(this, android.R.layout.simple_list_item_1, RecipeType.values()));
        }
        public void add(View view) {
            // check if any field is empty
            String name, description, address, phone, category, photo;
            name = etName.getText().toString();
            description = etDesc.getText().toString();
            address = etAddress.getText().toString();
            phone = etPhone.getText().toString();
            category = spCat.getSelectedItem().toString();
            if (ivPhoto.getDrawable() == null)
                photo = "no_image";
            else photo = ivPhoto.getDrawable().toString();

            if (name.trim().isEmpty() || description.trim().isEmpty() || address.trim().isEmpty() ||
                    phone.trim().isEmpty() || category.trim().isEmpty() || photo.trim().isEmpty())
            {
                Toast.makeText(this, R.string.err_fields_empty, Toast.LENGTH_SHORT).show();
                return;
            }

            Restaurant rest = new Restaurant(name, description, address, RestCat.valueOf(category), photo, phone);
            fbs.getFire().collection("restaurants")
                    .add(rest)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                        }
                    });
        }

        public void selectPhoto(View view) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"),40);
        }

        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == 40) {
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (resultCode == Activity.RESULT_CANCELED)  {
                    Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


