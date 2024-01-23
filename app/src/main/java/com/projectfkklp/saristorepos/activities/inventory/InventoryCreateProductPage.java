package com.projectfkklp.saristorepos.activities.inventory;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.projectfkklp.saristorepos.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class InventoryCreateProductPage extends AppCompatActivity {

    ImageView uploadImage;
    Button saveButton, barcodeInsert;
    EditText uploadProduct, uploadPrice, uploadStock;
    String imageURL;
    Uri uri;
    String tempBarcode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_create_product_page);

        uploadImage = findViewById(R.id.uploadImage);
        uploadProduct = findViewById(R.id.uploadProduct);
        uploadPrice = findViewById(R.id.uploadPrice);
        uploadStock = findViewById(R.id.uploadStock);
        saveButton = findViewById(R.id.saveButton);
        barcodeInsert = findViewById(R.id.barcodeInsert);

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        uri = Objects.requireNonNull(data).getData();
                        uploadImage.setImageURI(uri);
                    } else {
                        Toast.makeText(InventoryCreateProductPage.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        barcodeInsert.setOnClickListener(view -> {
            // Launch ZXing barcode scanner
            new IntentIntegrator(InventoryCreateProductPage.this).initiateScan();
        });

        uploadImage.setOnClickListener(view -> {
            Intent photoPicker = new Intent(Intent.ACTION_PICK);
            photoPicker.setType("image/*");
            activityResultLauncher.launch(photoPicker);
        });

        saveButton.setOnClickListener(view -> saveData());
    }

    // Add the following method to handle the result of the barcode scanner
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                tempBarcode = result.getContents();
                // Update the button text to show the scanned barcode temporarily
                barcodeInsert.setText("Scanned Barcode: " + tempBarcode);
                Toast.makeText(this, "Scanned Barcode: " + tempBarcode, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void saveData() {
        if (uri != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Android Images")
                    .child(Objects.requireNonNull(uri.getLastPathSegment()));
            AlertDialog.Builder builder = new AlertDialog.Builder(InventoryCreateProductPage.this);
            builder.setCancelable(false);
            builder.setView(R.layout.progress_layout);
            AlertDialog dialog = builder.create();
            dialog.show();

            storageReference.putFile(uri).addOnSuccessListener(taskSnapshot -> {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                uriTask.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri urlImage = task.getResult();
                        imageURL = urlImage.toString();

                        // Check for duplicate barcode before uploading data to Firestore
                        checkDuplicateBarcode();
                        dialog.dismiss();
                    }
                });
            }).addOnFailureListener(e -> {
                dialog.dismiss();
                Toast.makeText(InventoryCreateProductPage.this, "_InventoryCreateProductPage failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        } else {
            Toast.makeText(InventoryCreateProductPage.this, "Please select an image", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkDuplicateBarcode() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String barcode = tempBarcode;

        // Check if the barcode is empty or null
        if (barcode == null || barcode.isEmpty()) {
            // Barcode is empty or null, proceed to upload data to Firestore
            uploadDataToFirestore();
            return;
        }

        db.collection("users").document().collection("products")
                .whereEqualTo("barcode", barcode)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null && !task.getResult().isEmpty()) {
                            // Barcode already exists, show a warning
                            showBarcodeDuplicateWarning();
                        } else {
                            // Barcode is unique, proceed to upload data to Firestore
                            uploadDataToFirestore();
                        }
                    } else {
                        // Handle error if needed
                        Toast.makeText(InventoryCreateProductPage.this, "Error checking duplicate barcode: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }

                    // Reset tempBarcode after checking for duplicates
                    tempBarcode = null;
                });
    }


    private void showBarcodeDuplicateWarning() {
        new AlertDialog.Builder(this)
                .setTitle("Duplicate Barcode")
                .setMessage("A product with the same barcode already exists.")
                .setPositiveButton("OK", null)
                .show();
    }


    public void uploadDataToFirestore() {
        String product = uploadProduct.getText().toString();
        double price = Double.parseDouble(uploadPrice.getText().toString());
        int stock = Integer.parseInt(uploadStock.getText().toString());

        // Create a Map to store the data, including the temporarily scanned barcode
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("product", product);
        dataMap.put("price", price);
        dataMap.put("stock", stock);
        dataMap.put("imageURL", imageURL);
        dataMap.put("barcode", tempBarcode); // Add the temporarily scanned barcode

        // Access FirebaseAuth to get the current user
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // Access Firestore instance and add the data to the user's collection
            String userUid = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users")
                    .document(userUid)
                    .collection("products") // Use the specific subcollection for products
                    .add(dataMap)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(InventoryCreateProductPage.this, "Data Uploaded to Firestore", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(InventoryCreateProductPage.this, "Firestore _InventoryCreateProductPage failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            // Handle the case where the user is not signed in
            Toast.makeText(InventoryCreateProductPage.this, "_User not signed in", Toast.LENGTH_SHORT).show();
        }
    }



}
