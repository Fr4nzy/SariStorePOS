package com.projectfkklp.saristorepos.activities.pos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.projectfkklp.saristorepos.managers.TransactionManager;
import com.projectfkklp.saristorepos.models._Product;
import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.models._Transaction;
import com.projectfkklp.saristorepos.utils.ModelUtils;

import java.util.ArrayList;
import java.util.Date;


public class _PosCheckoutPage extends AppCompatActivity {

    private ArrayList<_Product> cartItemList;
    private _PosCheckoutPageAdapter posCheckoutPageAdapter;
    private CollectionReference usersCollection;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pos_checkout_page);

        FirebaseApp.initializeApp(this);

        mAuth = FirebaseAuth.getInstance();

        // Initialize Firestore
        usersCollection = FirebaseFirestore.getInstance().collection("users");

        // Retrieve the cartItemList from the Intent
        if (cartItemList == null) {
            cartItemList = getIntent().getParcelableArrayListExtra("cartItemList");
            assert cartItemList != null;
            for (_Product cartItem : cartItemList) {
                cartItem.setQuantity(1);
            }
        }
        updateOverallTotalAmount();

        // Add click listener to the "Purchase" button
        Button purchaseButton = findViewById(R.id.purchaseButton);
        purchaseButton.setOnClickListener(this::purchaseClick);

        // Initialize views and set up RecyclerView
        RecyclerView cartRecyclerView = findViewById(R.id.cartrecyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        cartRecyclerView.setLayoutManager(layoutManager);

        // Set up adapter
        posCheckoutPageAdapter = new _PosCheckoutPageAdapter(this, cartItemList);
        cartRecyclerView.setAdapter(posCheckoutPageAdapter);

    }

    // Add this method to update the overall total amount
    public void updateOverallTotalAmount() {
        // Implement the logic to update the total amount in your activity
        // For example, find the TextView and update its text.
        double totalAmount = calculateTotalAmount();
        TextView totalItemPriceTextView = findViewById(R.id.totalItemPrice);
        String formattedTotalAmount = String.format("Total Price: %.2f", totalAmount);
        totalItemPriceTextView.setText(formattedTotalAmount);
    }

    // Method to handle the "Purchase" button click
    public void purchaseClick(View view) {
        if (isStockAvailable(cartItemList) && isQuantityValid(cartItemList)) {
            double totalAmount = calculateTotalAmount();

            // Display the receipt
            showReceipt(totalAmount);
        } else {
            showExceedStockWarning("Please check stock availability and update them.");
        }
    }


    // Helper method to check if the quantity of any product exceeds the available stock
    private boolean isQuantityValid(ArrayList<_Product> cartItemList) {
        for (_Product cartItem : cartItemList) {
            int stock = cartItem.getStock();
            int quantity = cartItem.getQuantity();
            if (quantity > stock) {
                return false; // Quantity exceeds available stock
            }
        }
        return true; // All products have a valid quantity
    }

    // Helper method to show a warning message about exceeding the available stock
    private void showExceedStockWarning(String warningMessage) {
        new AlertDialog.Builder(this)
                .setTitle("Warning")
                .setMessage(warningMessage)
                .setPositiveButton("OK", null)
                .show();
    }

    // Modify the showReceipt method in _PosCheckoutPage activity
    private void showReceipt(double totalAmount) {
        _Transaction transaction = new _Transaction(
                ModelUtils.createUUID(),
                new Date(),
                totalAmount,
                cartItemList
        );

        // Create a StringBuilder to build the receipt message
        StringBuilder receiptMessage = new StringBuilder();
        receiptMessage.append("Date/Time - ").append(transaction.getDate()).append("\n");
        receiptMessage.append("_Transaction ID: ").append(transaction.getId()).append("\n\n");

        // Add details for each item in the cart
        for (_Product item : cartItemList) {
            receiptMessage.append(item.getProduct())
                    .append("\n")
                    .append("Quantity: ").append(item.getQuantity()).append("\n")
                    .append("Price: ₱").append(item.getPrice()).append("\n")
                    .append("Total: ₱").append(item.getTotalPrice()).append("\n\n");
        }

        // Add the total amount to the receipt
        receiptMessage.append("Total Amount: ₱").append(totalAmount);

        // Create and show an AlertDialog with the receipt details
        new AlertDialog.Builder(this)
                .setTitle("Receipt")
                .setMessage(receiptMessage.toString())
                .setPositiveButton("Confirm", (dialogInterface, i) -> {
                    // Store the transaction data in Firestore
                    TransactionManager.addTransaction(
                        transaction,
                        task -> cartItemList.clear()
                    );
                    deductStockFromDatabase(cartItemList);
                    posCheckoutPageAdapter.notifyDataSetChanged();

                    // Handle confirmation (transaction completed)
                    Toast.makeText(this, "_Transaction Complete", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(_PosCheckoutPage.this, _PosPage.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent); // Start _PosPage and clear its previous instances
                    finish(); // Finish the current activity (_PosCheckoutPage)
                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> {
                    // Handle cancellation (transaction canceled)
                    Toast.makeText(this, "_Transaction Canceled", Toast.LENGTH_SHORT).show();
                    // You can add any additional actions or leave it empty
                })
                .setCancelable(false) // Prevent canceling by tapping outside the dialog
                .show();
    }

    // Helper method to check if any product has a stock less than or equal to 0
    private boolean isStockAvailable(ArrayList<_Product> cartItemList) {
        for (_Product cartItem : cartItemList) {
            int stock = cartItem.getStock();
            if (stock <= 0) {
                return false; // Product is out of stock
            }
        }
        return true; // All products have stock greater than 0
    }

    // Helper method to deduct the purchased quantity from the stock in Firestore
    private void deductStockFromDatabase(ArrayList<_Product> cartItemList) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        for (_Product cartItem : cartItemList) {
            // Get the user's UID
            FirebaseUser currentUser = mAuth.getCurrentUser();
            String userUid = currentUser.getUid();

            // Get the product key from the cart item
            String productKey = cartItem.getKey();

            // Get the purchased quantity from the cart item
            int purchasedQuantity = cartItem.getQuantity();

            // Create a reference to the product document in Firestore
            DocumentReference productRef = db.collection("users").document(userUid)
                    .collection("products").document(productKey);

            db.runTransaction(transaction -> {
                DocumentSnapshot snapshot = transaction.get(productRef);

                // Check if the document exists
                if (snapshot.exists()) {
                    // Get the current stock value
                    Long currentStock = snapshot.getLong("stock");

                    // Check for null and handle it gracefully
                    if (currentStock != null) {
                        // Calculate the new stock value after deducting the purchased quantity
                        int newStock = Math.max(currentStock.intValue() - purchasedQuantity, 0);

                        // Update the stock value in Firestore
                        transaction.update(productRef, "stock", newStock);
                    }
                }

                return null;
            }).addOnSuccessListener(result -> {
                // Stock updated successfully
            }).addOnFailureListener(e -> {
                // Handle failure if needed
                showErrorDialog("Failed to update stock: " + e.getMessage());
            });
        }
    }

    // Helper method to calculate the total amount
    private double calculateTotalAmount() {
        double totalAmount = 0.0;
        for (_Product item : cartItemList) {
            totalAmount += item.getTotalPrice();
        }
        return totalAmount;
    }

    private void showErrorDialog(String errorMessage) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(errorMessage)
                .setPositiveButton("OK", null)
                .show();
    }

}