package com.example.saristorepos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DetailActivity extends AppCompatActivity {

    private TextView detailPrice, detailProduct, detailStock;
    private ImageView detailImage;
    private FloatingActionButton deleteButton, editButton;
    private String key = "";
    private String imageUrl = "";
    private int stock = 0; // Change to int

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        detailPrice = findViewById(R.id.detailPrice);
        detailImage = findViewById(R.id.detailImage);
        detailProduct = findViewById(R.id.detailProduct);
        deleteButton = findViewById(R.id.deleteButton);
        editButton = findViewById(R.id.editButton);
        detailStock = findViewById(R.id.detailStock);

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

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteProduct();
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProduct();
            }
        });
    }

    private void deleteProduct() {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Products");
        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageReference = storage.getReferenceFromUrl(imageUrl);
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                reference.child(key).removeValue();
                Toast.makeText(DetailActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), viewInventory.class));
                finish();
            }
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
