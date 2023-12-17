package com.projectfkklp.saristorepos;

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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class UpdateActivity extends AppCompatActivity {

    ImageView updateImage;
    Button updateButton;
    EditText updatePrice, updateProduct, updateStock;
    String product, price, stock;
    String imageUrl;
    String key, oldImageURL;
    Uri uri;
    DocumentReference documentReference;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        updateButton = findViewById(R.id.updateButton);
        updatePrice = findViewById(R.id.updatePrice);
        updateImage = findViewById(R.id.updateImage);
        updateStock = findViewById(R.id.updateStock);
        updateProduct = findViewById(R.id.updateProduct);

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        uri = result.getData().getData();
                        updateImage.setImageURI(uri);
                    } else {
                        Toast.makeText(UpdateActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Glide.with(this).load(bundle.getString("Image")).into(updateImage);
            updateProduct.setText(bundle.getString("Product"));
            updatePrice.setText(bundle.getString("Price"));
            updateStock.setText(bundle.getString("Stock"));
            key = bundle.getString("Key");
            oldImageURL = bundle.getString("Image");
        }

        documentReference = FirebaseFirestore.getInstance().collection("products").document(key);

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
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(UpdateActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        // Handle errors
                        Toast.makeText(UpdateActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
