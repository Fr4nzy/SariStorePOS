package com.projectfkklp.saristorepos.activities.pos;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.projectfkklp.saristorepos.activities.user_login.UserLoginPage;
import com.projectfkklp.saristorepos.models._Product;
import com.projectfkklp.saristorepos.R;

public class PosPage extends AppCompatActivity {

    // Add the following member variable
    private String userUid;
    FirebaseAuth mAuth;
    RecyclerView posRecyclerView;
    SearchView searchView;
    CollectionReference productsCollection;
    private TextView cartCountTextView;
    private PointOfSalePageAdapter adapter;
    private List<_Product> cartItemList = new ArrayList<>();
    final List<_Product> productList = new ArrayList<>();
    private final List<_Product> filteredProductList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pos_page);

        mAuth = FirebaseAuth.getInstance();

        // Initialize views, lists, and set up SearchView
        searchView = findViewById(R.id.searchViewPos);
        posRecyclerView = findViewById(R.id.pos_recycler_view);
        cartCountTextView = findViewById(R.id.cartTextView);
        cartItemList = new ArrayList<>();

        Button barcodeSearch = findViewById(R.id.barcodeSearch);

        // Get the current user's UID
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userUid = currentUser.getUid();
        } else {
            // Handle the case when the user is not authenticated
            // You may want to redirect them to the UserLoginPage activity
            Toast.makeText(this, "Failed to Authenticate _User", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, UserLoginPage.class);
            startActivity(intent);
        }

        // Set up RecyclerView
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        posRecyclerView.setLayoutManager(gridLayoutManager);

        // Set up adapter
        adapter = new PointOfSalePageAdapter(this, filteredProductList);
        posRecyclerView.setAdapter(adapter);

        // Firestore initialization
        productsCollection = FirebaseFirestore.getInstance().collection("users")
                .document(userUid)  // Use user's UID as the document ID
                .collection("products");

        // Firestore event listener to fetch product data and listen for updates
        productsCollection.addSnapshotListener((value, error) -> {
            if (error != null) {
                // Handle the error
                Log.e("FirestoreListener", "Error fetching data", error);
                return;
            }

            productList.clear();
            for (DocumentSnapshot document : Objects.requireNonNull(value).getDocuments()) {
                _Product product = document.toObject(_Product.class);
                if (product != null) {
                    product.setKey(document.getId());
                    productList.add(product);
                    Log.d("FirestoreListener", "Fetched data: " + product.getProduct() + ", " + product.getPrice() + ", " + product.getStock());
                }
            }
            adapter.notifyDataSetChanged();
        });

        // Set up Firestore event listener to fetch initial data
        productsCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                productList.clear();
                for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    _Product product = document.toObject(_Product.class);
                    if (product != null) {
                        product.setKey(document.getId());
                        productList.add(product);
                        Log.d("FirestoreListener", "Fetched data: " + product.getProduct() + ", " + product.getPrice() + ", " + product.getStock());
                    }
                }
                // Sort the productList alphabetically based on the product names
                Collections.sort(productList, new Comparator<_Product>() {
                    @Override
                    public int compare(_Product product1, _Product product2) {
                        return product1.getProduct().compareToIgnoreCase(product2.getProduct());
                    }
                });

                filter("");
                adapter.notifyDataSetChanged();

                // Set up real-time Firestore listener for subsequent updates
                setUpFirestoreListener();
            } else {
                // Handle the error
                Log.e("FirestoreListener", "Error fetching initial data", task.getException());
            }
        });

        // Register a BroadcastReceiver to receive quantity updates from PosCheckoutPage activity
        BroadcastReceiver quantityUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if ("update_quantity_action".equals(intent.getAction())) {
                    String cartItemKey = intent.getStringExtra("cart_item_key");
                    int newQuantity = intent.getIntExtra("cart_item_quantity", 0);

                    // Update the quantity in the PosPage activity
                    updateQuantityInViewPOS(cartItemKey, newQuantity);
                }
            }
        };

        // Register the receiver with the intent filter
        IntentFilter intentFilter = new IntentFilter("update_quantity_action");
        registerReceiver(quantityUpdateReceiver, intentFilter);

        // Set click listener for the adapter
        adapter.setOnItemClickListener(this::addToCart);

        barcodeSearch.setOnClickListener(view -> {
            // Launch ZXing barcode scanner
            new IntentIntegrator(PosPage.this).initiateScan();
        });

        // Set up SearchView listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });

        // Add the following code to handle close button click
        searchView.setOnCloseListener(() -> {
            // Clear the search and show the original data
            clearSearch();
            return false;
        });
    }

    private void filter(String searchText) {
        // Save the original productList before applying the filter
        List<_Product> originalList = new ArrayList<>(productList);

        List<_Product> filteredList = new ArrayList<>();

        for (_Product product : originalList) {
            if (product.getProduct().toLowerCase().contains(searchText.toLowerCase())) {
                filteredList.add(product);
            }
        }

        // Save the filtered list
        filteredProductList.clear();
        filteredProductList.addAll(filteredList);

        // Update the RecyclerView with the filtered list
        adapter.updateProductList(filteredList);
    }

    private void clearSearch() {
        // Clear the search and show the original data
        filter("");
    }


    private void setUpFirestoreListener() {
        // Real-time Firestore event listener to listen for updates
        productsCollection.addSnapshotListener((value, error) -> {
            if (error != null) {
                // Handle the error
                Log.e("FirestoreListener", "Error fetching real-time updates", error);
                return;
            }

            for (DocumentChange dc : Objects.requireNonNull(value).getDocumentChanges()) {
                switch (dc.getType()) {
                    case ADDED:
                        // Handle added document
                        break;
                    case MODIFIED:
                        // Handle modified document
                        handleModifiedDocument(dc.getDocument());
                        break;
                    case REMOVED:
                        // Handle removed document
                        break;
                }
            }
        });
    }

    private void handleModifiedDocument(DocumentSnapshot document) {
        // Update the corresponding item in productList when a document is modified
        String modifiedKey = document.getId();
        for (_Product product : productList) {
            if (product.getKey().equals(modifiedKey)) {
                _Product updatedProduct = document.toObject(_Product.class);
                if (updatedProduct != null) {
                    updatedProduct.setKey(modifiedKey);
                    int index = productList.indexOf(product);
                    productList.set(index, updatedProduct);
                    adapter.notifyItemChanged(index);
                    Log.d("FirestoreListener", "Updated data: " + updatedProduct.getProduct() + ", " + updatedProduct.getPrice() + ", " + updatedProduct.getStock());
                }
                break;
            }
        }
    }

    private void addToCart(_Product product) {
        // Toggle the selection state of the item in cartItemList
        if (cartItemList.contains(product)) {
            cartItemList.remove(product); // Remove the item if it's already in the cart
            product.setSelected(false); // Update the selection state in the original productList
            Toast.makeText(this, product.getProduct() + " removed to cart ", Toast.LENGTH_SHORT).show();
        } else {
            cartItemList.add(product); // Add the item to the cartItemList if not already added
            product.setSelected(true);// Update the selection state in the original productList
            Toast.makeText(this, product.getProduct() + " added to cart ", Toast.LENGTH_SHORT).show();
        }

        // Update the RecyclerView to reflect the changes in the selection state
        adapter.notifyDataSetChanged();

        updateCartCount();
    }


    // Add the following method to handle the result of the barcode scanner
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                String scannedBarcode = result.getContents();
                // Use the scannedBarcode to search for the product in Firestore
                searchProductByBarcode(scannedBarcode);
            }
        }
    }

    // Method to search for a product in Firestore based on the barcode
    private void searchProductByBarcode(String barcode) {
        List<_Product> filteredList = new ArrayList<>();

        for (_Product product : productList) {
            if (product.getBarcode() != null && product.getBarcode().equals(barcode)) {
                filteredList.add(product);
            }
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(this, "Product not found for Barcode: " + barcode, Toast.LENGTH_SHORT).show();
        } else {
            // Update the RecyclerView with the filtered list
            adapter.updateProductList(filteredList);
        }
    }

    private void updateCartCount() {
        // Update the cart count TextView with the number of items in the cart
        int cartCount = cartItemList.size();
        cartCountTextView.setText("Cart: " + cartCount);
    }

    public void newAddToCartClick(View view) {
        // Ensure cartItemList is not null before passing it to the intent
        if (cartItemList != null && !cartItemList.isEmpty()) {
            Intent intent = new Intent(PosPage.this, PosCheckoutPage.class);

            // Convert the List<_Product> to ArrayList<_Product>
            ArrayList<_Product> cartArrayList = new ArrayList<>(cartItemList);

            intent.putParcelableArrayListExtra("cartItemList", cartArrayList);
            startActivity(intent);
        } else {
            // Handle the case when cartItemList is null
            showEmptyCartAlert();
        }
    }

    private void showEmptyCartAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Empty Cart");
        builder.setMessage("No added items in the cart");
        builder.setPositiveButton("OK", null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void updateQuantityInViewPOS(String cartItemKey, int newQuantity) {
        // Find the item in your productList and update its quantity
        for (_Product product : productList) {
            if (product.getKey().equals(cartItemKey)) {
                product.setQuantity(newQuantity);
                adapter.notifyDataSetChanged(); // Notify the RecyclerView adapter about the change
                break; // Exit the loop once the item is found and updated
            }
        }
    }
}