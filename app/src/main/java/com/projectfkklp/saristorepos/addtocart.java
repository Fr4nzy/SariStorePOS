package com.projectfkklp.saristorepos;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;


public class addtocart extends AppCompatActivity {

    private ArrayList<DataClass> cartItemList;
    private CartAdapter cartAdapter;
    private CollectionReference usersCollection;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtocart);

        FirebaseApp.initializeApp(this);

        mAuth = FirebaseAuth.getInstance();

        // Initialize Firestore
        usersCollection = FirebaseFirestore.getInstance().collection("users");

        // Retrieve the cartItemList from the Intent
        if (cartItemList == null) {
            cartItemList = getIntent().getParcelableArrayListExtra("cartItemList");
            assert cartItemList != null;
            for (DataClass cartItem : cartItemList) {
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
        cartAdapter = new CartAdapter(this, cartItemList);
        cartRecyclerView.setAdapter(cartAdapter);

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
            deductStockFromDatabase(cartItemList);

            // Display the receipt
            showReceipt(totalAmount);

            // Clear the cart after purchase
            cartItemList.clear();
            cartAdapter.notifyDataSetChanged();
        } else {
            showExceedStockWarning("Please check stock availability and update them.");
        }
    }


    // Helper method to check if the quantity of any product exceeds the available stock
    private boolean isQuantityValid(ArrayList<DataClass> cartItemList) {
        for (DataClass cartItem : cartItemList) {
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

    private void showStockUpdateWarning(String warningMessage) {
        new AlertDialog.Builder(this)
                .setTitle("Warning")
                .setMessage(warningMessage)
                .setPositiveButton("OK", (dialogInterface, i) -> {

                    // Intent intent = new Intent(addtocart.this, viewInventory.class);
                    // startActivity(intent);
                    // finish();
                })
                .show();
    }

    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date currentDate = new Date();
        return dateFormat.format(currentDate);
    }

    // Modify the showReceipt method in addtocart activity
    private void showReceipt(double totalAmount) {
        // Generate a unique transaction ID
        String transactionId = generateTransactionId();

        // Get the current date
        String currentDate = getCurrentDate();

        // Create a StringBuilder to build the receipt message
        StringBuilder receiptMessage = new StringBuilder();
        receiptMessage.append("Date/Time - ").append(currentDate).append("\n");
        receiptMessage.append("Transaction ID: ").append(transactionId).append("\n\n");

        // Add details for each item in the cart
        for (DataClass item : cartItemList) {
            receiptMessage.append(item.getProduct())
                    .append("\n")
                    .append("Quantity: ").append(item.getQuantity()).append("\n")
                    .append("Price: ₱").append(item.getPrice()).append("\n")
                    .append("Total: ₱").append(item.getTotalPrice()).append("\n\n");
        }

        // Add the total amount to the receipt
        receiptMessage.append("Total Amount: ₱").append(totalAmount);

        // Store the transaction data in Firestore
        storeTransactionData(transactionId, currentDate, totalAmount, cartItemList);

        // Create and show an AlertDialog with the receipt details
        new AlertDialog.Builder(this)
                .setTitle("Receipt")
                .setMessage(receiptMessage.toString())
                .setPositiveButton("Confirm", (dialogInterface, i) -> {
                    // Handle confirmation (transaction completed)
                    Toast.makeText(this, "Transaction Complete", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(addtocart.this, viewPOS.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent); // Start viewPOS and clear its previous instances
                    finish(); // Finish the current activity (addtocart)
                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> {
                    // Handle cancellation (transaction canceled)
                    Toast.makeText(this, "Transaction Canceled", Toast.LENGTH_SHORT).show();
                    // You can add any additional actions or leave it empty
                })
                .setCancelable(false) // Prevent canceling by tapping outside the dialog
                .show();
    }

    // Generate a unique transaction ID
    private String generateTransactionId() {
        // Implement your logic to generate a unique transaction ID (e.g., using timestamp, random numbers, etc.)
        return UUID.randomUUID().toString();
    }

    // Store the transaction data in Firestore
    private void storeTransactionData(String transactionId, String currentDate, double totalAmount, ArrayList<DataClass> cartItemList) {
        // Get a reference to the Firestore collection for transactions
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userUid = currentUser.getUid();
        CollectionReference transactionsCollection = usersCollection.document(userUid).collection("transactions");

        // Create a new document with the generated transaction ID
        DocumentReference transactionDocument = transactionsCollection.document(transactionId);

        // Create a data object to store in Firestore
        TransactionData transactionData = new TransactionData(transactionId, currentDate, totalAmount, cartItemList);

        // Set the data in the document
        transactionDocument.set(transactionData)
                .addOnSuccessListener(aVoid -> {
                    // Transaction data stored successfully
                })
                .addOnFailureListener(e -> {
                    // Handle failure if needed
                    showErrorDialog("Failed to store transaction data: " + e.getMessage());
                });
    }


    // Add a new class for storing transaction data
    public class TransactionData {
        private String transactionId;
        private String date;
        private double totalAmount;

        // Constructors, getters, setters (you can generate them automatically in Android Studio)

        public String getTransactionId() {
            return transactionId;
        }

        public void setTransactionId(String transactionId) {
            this.transactionId = transactionId;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public double getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(double totalAmount) {
            this.totalAmount = totalAmount;
        }
        public TransactionData() {
            // Default constructor required for Firestore

        }
        public TransactionData(String transactionId, String date, double totalAmount, ArrayList<DataClass> cartItemList) {
            this.transactionId = transactionId;
            this.date = date;
            this.totalAmount = totalAmount;
        }
    }


    // Helper method to check if any product has a stock less than or equal to 0
    private boolean isStockAvailable(ArrayList<DataClass> cartItemList) {
        for (DataClass cartItem : cartItemList) {
            int stock = cartItem.getStock();
            if (stock <= 0) {
                return false; // Product is out of stock
            }
        }
        return true; // All products have stock greater than 0
    }

    // Helper method to deduct the purchased quantity from the stock in Firestore
    private void deductStockFromDatabase(ArrayList<DataClass> cartItemList) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        for (DataClass cartItem : cartItemList) {
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
        for (DataClass item : cartItemList) {
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