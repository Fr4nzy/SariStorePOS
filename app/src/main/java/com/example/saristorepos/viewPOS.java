package com.example.saristorepos;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import android.view.View;
import android.content.Intent;
import android.widget.TextView;


public class viewPOS extends AppCompatActivity {

    RecyclerView posRecyclerView;
    List<DataClass> productList;
    DatabaseReference databaseReference;
    private TextView totalTextView;
    private TextView cartCountTextView; // Added TextView for cart count
    private double totalPrice;
    private MyAdapter2 adapter;
    private List<DataClass> cartItemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pos);

        // Initialize views and lists
        posRecyclerView = findViewById(R.id.recyclerView2);
        totalTextView = findViewById(R.id.totalTextView);
        cartCountTextView = findViewById(R.id.cartTextView); // Initialize the cart count TextView
        productList = new ArrayList<>();
        cartItemList = new ArrayList<>();

        // Set up RecyclerView
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        posRecyclerView.setLayoutManager(gridLayoutManager);

        // Set up adapter
        adapter = new MyAdapter2(this, productList);
        posRecyclerView.setAdapter(adapter);

        // Firebase initialization
        databaseReference = FirebaseDatabase.getInstance().getReference("Products");

        // Firebase event listener to fetch product data
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    DataClass dataClass = itemSnapshot.getValue(DataClass.class);
                    dataClass.setKey(itemSnapshot.getKey());
                    productList.add(dataClass);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors here
            }
        });

        // Set click listener for the adapter
        adapter.setOnItemClickListener(new MyAdapter2.OnItemClickListener() {
            @Override
            public void onItemClick(DataClass data) {
                // Handle the item click event
                addToCart(data);
            }
        });
    }

    private void addToCart(DataClass data) {
        // Update your cart logic here
        // For simplicity, let's assume each item's price is added to the total
        totalPrice += data.getDataPrice();
        updateTotalPrice();

        // Add the item to the cartItemList if not already added
        if (!cartItemList.contains(data)) {
            cartItemList.add(data);
        }
        updateCartCount();

    }

    private void updateTotalPrice() {
        // Update the totalTextView with the new total price
        totalTextView.setText("Total: ₱" + String.format("%.2f", totalPrice));
    }
    private void updateCartCount() {
        // Update the cart count TextView with the number of items in the cart
        int cartCount = cartItemList.size();
        cartCountTextView.setText("Cart: " + cartCount);
    }
    public void newAddToCartClick(View view) {
        // Ensure cartItemList is not null before passing it to the intent
        if (cartItemList != null) {
            Intent intent = new Intent(viewPOS.this, addtocart.class);

            // Convert the List<DataClass> to ArrayList<DataClass>
            ArrayList<DataClass> cartArrayList = new ArrayList<>(cartItemList);

            intent.putParcelableArrayListExtra("cartItemList", cartArrayList);
            startActivity(intent);
        } else {
            // Handle the case when cartItemList is null
        }
    }

}