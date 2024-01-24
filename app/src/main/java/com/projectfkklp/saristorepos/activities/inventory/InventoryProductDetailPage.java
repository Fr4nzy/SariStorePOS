package com.projectfkklp.saristorepos.activities.inventory;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.models.Product;
import com.projectfkklp.saristorepos.utils.StringUtils;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class InventoryProductDetailPage extends AppCompatActivity {
    Uri uri;
    String tempBarcode, imageURL;
    private Product product;
    private TextView titleText;
    private ImageView detailImageView;
    private Button detailBarcodeButton;
    private Button detailSaveButton;
    private EditText detailStockText;
    private EditText detailProductText;
    private EditText detailPriceText;


    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_product_detail_page);

        product = getIntent().getSerializableExtra("Product", Product.class);

        initializeViews();

    }

    private void initializeViews() {
        titleText = findViewById(R.id.product_detail_title);
        detailImageView = findViewById(R.id.product_detail_image_view);
        detailBarcodeButton = findViewById(R.id.product_detail_barcode_button);
        detailSaveButton = findViewById(R.id.product_detail_save_button);
        detailPriceText = findViewById(R.id.product_detail_price_text);
        detailStockText = findViewById(R.id.product_detail_stock_text);
        detailProductText = findViewById(R.id.product_detail_product_text);

        titleText.setText(StringUtils.isNullOrEmpty(product.getId()) ? "Create Product" : "Edit Product");
        detailPriceText.setText(String.valueOf(product.getUnitPrice()));
        detailProductText.setText(String.valueOf(product.getName()));
        detailStockText.setText(String.valueOf(product.getStocks()));

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        uri = Objects.requireNonNull(data).getData();
                        detailImageView.setImageURI(uri);
                    } else {
                        Toast.makeText(InventoryProductDetailPage.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        detailImageView.setOnClickListener(v -> {
            Intent photoPicker = new Intent(Intent.ACTION_PICK);
            photoPicker.setType("image/*");
            activityResultLauncher.launch(photoPicker);
        });

        detailBarcodeButton.setOnClickListener(v -> {
            // Launch ZXing barcode scanner
            new IntentIntegrator(InventoryProductDetailPage.this).initiateScan();
        });

        detailSaveButton.setOnClickListener(v -> uploadProductToDB());

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
                detailBarcodeButton.setText("" + tempBarcode);
                Toast.makeText(this, "Scanned Barcode: " + tempBarcode, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void uploadProductToDB(){
        if (uri != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Android Images")
                    .child(Objects.requireNonNull(uri.getLastPathSegment()));
            AlertDialog.Builder builder = new AlertDialog.Builder(InventoryProductDetailPage.this);
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
                Toast.makeText(InventoryProductDetailPage.this, "InventoryProductDetailPage failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        } else {
            Toast.makeText(InventoryProductDetailPage.this, "Please select an image", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkDuplicateBarcode() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String barcode = tempBarcode;

        // Check if the barcode is empty or null
        if (barcode == null || barcode.isEmpty()) {
            // Barcode is empty or null, proceed to upload data to Firestore
            saveProduct();
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
                            saveProduct();
                        }
                    } else {
                        // Handle error if needed
                        Toast.makeText(InventoryProductDetailPage.this, "Error checking duplicate barcode: " + task.getException(), Toast.LENGTH_SHORT).show();
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

    public void saveProduct() {
        String product = detailProductText.getText().toString();
        double price = Double.parseDouble(detailPriceText.getText().toString());
        int stock = Integer.parseInt(detailStockText.getText().toString());

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
                        Toast.makeText(InventoryProductDetailPage.this, "Data Uploaded", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(InventoryProductDetailPage.this, "InventoryProductDetailPage failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            // Handle the case where the user is not signed in
            Toast.makeText(InventoryProductDetailPage.this, "User not signed in", Toast.LENGTH_SHORT).show();
        }
    }
    public void navigateBack(View view){    finish();   }
}
