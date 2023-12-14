package com.example.saristorepos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class addtocart extends AppCompatActivity {

    private RecyclerView cartRecyclerView;
    private ArrayList<DataClass> cartItemList;
    private CartAdapter cartAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtocart);

        // Retrieve the cartItemList from the Intent
        cartItemList = getIntent().getParcelableArrayListExtra("cartItemList");

        // Initialize views and set up RecyclerView
        cartRecyclerView = findViewById(R.id.cartrecyclerview);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        cartRecyclerView.setLayoutManager(layoutManager);

        // Set up adapter
        cartAdapter = new CartAdapter(this, cartItemList);
        cartRecyclerView.setAdapter(cartAdapter);

        // Add click listener to the "Purchase" button
        Button purchaseButton = findViewById(R.id.purchaseButton);
        purchaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                purchaseClick(view);
            }
        });
    }

    // Add this method to update the overall total amount
    public void updateOverallTotalAmount(double totalAmount) {
        // Implement the logic to update the total amount in your activity
        // For example, find the TextView and update its text.
        TextView totalItemPriceTextView = findViewById(R.id.totalItemPrice);
        String formattedTotalAmount = String.format("Total Price: %.2f", totalAmount);
        totalItemPriceTextView.setText(formattedTotalAmount);
    }

    // Method to handle the "Purchase" button click
    public void purchaseClick(View view) {
        // Check if any product has a stock less than or equal to 0
        if (isStockAvailable(cartItemList)) {
            // Calculate the total amount
            double totalAmount = calculateTotalAmount(cartItemList);

            // Deduct the purchased quantity from the stock
            deductStockFromDatabase(cartItemList);

            // Display the receipt
            showReceipt(totalAmount);
        } else {
            // Show an error message if any product is out of stock
            showErrorDialog("Some products are out of stock. Please remove or update them from your cart.");
        }
    }

    private void showReceipt(double totalAmount) {
        // Create a StringBuilder to build the receipt message
        StringBuilder receiptMessage = new StringBuilder();
        receiptMessage.append("Receipt:\n\n");

        // Add details for each item in the cart
        for (DataClass item : cartItemList) {
            receiptMessage.append(item.getDataProduct())
                    .append("\n")
                    .append("Quantity: ").append(item.getQuantity()).append("\n")
                    .append("Price: ₱").append(item.getDataPrice()).append("\n")
                    .append("Total: ₱").append(item.getTotalPrice()).append("\n\n");
        }

        // Add the total amount to the receipt
        receiptMessage.append("Total Amount: ₱").append(totalAmount);

        // Create and show an AlertDialog with the receipt details
        new AlertDialog.Builder(this)
                .setTitle("Receipt")
                .setMessage(receiptMessage.toString())
                .setPositiveButton("OK", null)
                .show();
    }

    // Helper method to check if any product has a stock less than or equal to 0
    private boolean isStockAvailable(ArrayList<DataClass> cartItemList) {
        for (DataClass cartItem : cartItemList) {
            int stock = cartItem.getDataStock();
            if (stock <= 0) {
                return false; // Product is out of stock
            }
        }
        return true; // All products have stock greater than 0
    }

    // Helper method to deduct the purchased quantity from the stock in the database
    private void deductStockFromDatabase(ArrayList<DataClass> cartItemList) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Products");

        for (DataClass cartItem : cartItemList) {
            // Get the product key from the cart item
            String productKey = cartItem.getKey();

            // Get the purchased quantity from the cart item
            int purchasedQuantity = cartItem.getQuantity();

            // Update the stock in the database by deducting the purchased quantity
            databaseReference.child(productKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Get the current stock value
                        int currentStock = snapshot.child("dataStock").getValue(Integer.class);

                        // Calculate the new stock value after deducting the purchased quantity
                        int newStock = currentStock - purchasedQuantity;

                        // Update the stock value in the database
                        databaseReference.child(productKey).child("dataStock").setValue(newStock);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error if needed
                }
            });
        }
    }

    // Helper method to calculate the total amount
    private double calculateTotalAmount(ArrayList<DataClass> cartItemList) {
        double totalAmount = 0.0;
        for (DataClass item : cartItemList) {
            totalAmount += item.getTotalPrice();
        }
        return totalAmount;
    }

    // Helper method to show an error dialog
    private void showErrorDialog(String errorMessage) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(errorMessage)
                .setPositiveButton("OK", null)
                .show();
    }
}