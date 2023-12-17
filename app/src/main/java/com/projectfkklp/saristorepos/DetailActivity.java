package com.projectfkklp.saristorepos;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DetailActivity extends AppCompatActivity {

    private TextView detailPrice;
    private TextView detailProduct;
    private String key = "";
    private String imageUrl = "";
    private int stock = 0; // Change to int

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

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

        // Reference to the Firestore document
        StorageReference imageRef = storage.getReferenceFromUrl(imageUrl);
        imageRef.delete().addOnSuccessListener(aVoid -> {
            // Image deletion successful
            db.collection("products").document(key)
                    .delete()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Delete successful, handle UI updates
                            Toast.makeText(DetailActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            // Handle errors
                            Toast.makeText(DetailActivity.this, "Error deleting product", Toast.LENGTH_SHORT).show();
                        }
                    });
        }).addOnFailureListener(e -> {
            // Handle failure to delete image
            Toast.makeText(DetailActivity.this, "Error deleting image", Toast.LENGTH_SHORT).show();
        });
    }


    private void editProduct() {
        Intent intent = new Intent(DetailActivity.this, UpdateActivity.class);
        intent.putExtra("Product", detailProduct.getText().toString());
        intent.putExtra("Price", detailPrice.getText().toString());
        intent.putExtra("Stock", String.valueOf(stock)); // Change to String.valueOf(stock) for int
        intent.putExtra("Image", imageUrl);
        intent.putExtra("Key", key);
        startActivity(intent);
    }
}
