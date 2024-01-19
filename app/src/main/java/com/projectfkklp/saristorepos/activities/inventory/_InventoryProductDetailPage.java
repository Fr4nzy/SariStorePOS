package com.projectfkklp.saristorepos.activities.inventory;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.projectfkklp.saristorepos.R;

public class _InventoryProductDetailPage extends AppCompatActivity {

    private TextView detailPrice;
    private TextView detailProduct;
    private String key = "";
    private String imageUrl = "";
    private int stock = 0; // Change to int

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_product_detail_page);

        detailPrice = findViewById(R.id.detailPrice);
        ImageView detailImage = findViewById(R.id.detailImage);
        detailProduct = findViewById(R.id.detailProduct);
        FloatingActionButton deleteButton = findViewById(R.id.deleteButton);
        FloatingActionButton editButton = findViewById(R.id.editButton);
        TextView detailStock = findViewById(R.id.detailStock);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            double price = bundle.getDouble("Price");
            stock = bundle.getInt("Stock"); // Change to getInt for int
            String product = bundle.getString("Product");
            detailPrice.setText(String.valueOf(price));
            detailStock.setText(String.valueOf(stock));
            detailProduct.setText(product);
            key = bundle.getString("Key");
            imageUrl = bundle.getString("Image");
            Glide.with(this).load(imageUrl).into(detailImage);
        }

        deleteButton.setOnClickListener(v -> deleteProduct());

        editButton.setOnClickListener(v -> editProduct());
    }

    private void deleteProduct() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final FirebaseStorage storage = FirebaseStorage.getInstance();
        // Access FirebaseAuth to get the current user
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = mAuth.getCurrentUser();

        // Reference to the Firestore document in the "users" collection and "products" subcollection
        StorageReference imageRef = storage.getReferenceFromUrl(imageUrl);
        imageRef.delete().addOnSuccessListener(aVoid -> {
            // Image deletion successful
            String userUid = currentUser.getUid();
            db.collection("users").document(userUid)  // Assuming userUid is the user's UID
                    .collection("products")
                    .document(key)
                    .delete()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Delete successful, handle UI updates
                            Toast.makeText(_InventoryProductDetailPage.this, "Deleted", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            // Handle errors
                            Toast.makeText(_InventoryProductDetailPage.this, "Error deleting product", Toast.LENGTH_SHORT).show();
                        }
                    });
        }).addOnFailureListener(e -> {
            // Handle failure to delete image
            Toast.makeText(_InventoryProductDetailPage.this, "Error deleting image", Toast.LENGTH_SHORT).show();
        });
    }

    private void editProduct() {
        Intent intent = new Intent(_InventoryProductDetailPage.this, _InventoryEditProductPage.class);
        intent.putExtra("Product", detailProduct.getText().toString());
        intent.putExtra("Price", detailPrice.getText().toString());
        intent.putExtra("Stock", String.valueOf(stock));
        intent.putExtra("Image", imageUrl);
        intent.putExtra("Key", key);
        startActivity(intent);
    }
}
