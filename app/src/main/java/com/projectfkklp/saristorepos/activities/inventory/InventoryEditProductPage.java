package com.projectfkklp.saristorepos.activities.inventory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.projectfkklp.saristorepos.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class InventoryEditProductPage extends AppCompatActivity {

    ImageView updateImage;
    Button updateButton;
    EditText updatePrice, updateProduct, updateStock;
    String product, price, stock;
    String imageUrl;
    String key, oldImageURL;
    Uri uri;
    DocumentReference documentReference;
    StorageReference storageReference;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_edit_product_page);

        updateButton = findViewById(R.id.updateButton);
        updatePrice = findViewById(R.id.updatePrice);
        updateImage = findViewById(R.id.updateImage);
        updateStock = findViewById(R.id.updateStock);
        updateProduct = findViewById(R.id.updateProduct);

        mAuth = FirebaseAuth.getInstance();

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        uri = result.getData().getData();
                        updateImage.setImageURI(uri);
                    } else {
                        Toast.makeText(InventoryEditProductPage.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Glide.with(this).load(bundle.getString("Image")).into(updateImage);
            updateProduct.setText(bundle.getString("_Product"));
            updatePrice.setText(bundle.getString("Price"));
            updateStock.setText(bundle.getString("Stock"));
            key = bundle.getString("Key");
            oldImageURL = bundle.getString("Image");
        }

        // Get the current user from FirebaseAuth
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userUid = currentUser.getUid();

        documentReference = FirebaseFirestore.getInstance()
                .collection("users")
                .document(userUid)  // Assuming userUid is the user's UID
                .collection("products")
                .document(key);


        updateImage.setOnClickListener(view -> {
            Intent photoPicker = new Intent(Intent.ACTION_PICK);
            photoPicker.setType("image/*");
            activityResultLauncher.launch(photoPicker);
        });

        updateButton.setOnClickListener(view -> saveData());
    }

    private void saveData() {
        if (uri != null) {
            storageReference = FirebaseStorage.getInstance().getReference().child("Android Images").child(Objects.requireNonNull(uri.getLastPathSegment()));

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setView(R.layout.progress_layout);
            AlertDialog dialog = builder.create();
            dialog.show();

            storageReference.putFile(uri).addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(urlImage -> {
                imageUrl = urlImage.toString();
                updateData();
                dialog.dismiss();
            }).addOnFailureListener(e -> dialog.dismiss())).addOnFailureListener(e -> dialog.dismiss());
        } else {
            // Handle the case where no image is selected
            //Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            // If no new image is selected, use the existing image URL
            imageUrl = oldImageURL;
            updateData();
        }
    }

    private void updateData() {
        product = updateProduct.getText().toString().trim();
        double updatedPrice = Double.parseDouble(updatePrice.getText().toString().trim());
        int updatedStock = Integer.parseInt(updateStock.getText().toString().trim());

        // Create a Map to update only the necessary fields
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("product", product);
        updateMap.put("price", updatedPrice);
        updateMap.put("stock", updatedStock);
        updateMap.put("imageURL", imageUrl);

        // Update only the specified fields in the document
        documentReference.update(updateMap)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Update successful, handle UI updates
                        Toast.makeText(InventoryEditProductPage.this, "Updated", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        // Handle errors
                        Toast.makeText(InventoryEditProductPage.this, "Update failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
