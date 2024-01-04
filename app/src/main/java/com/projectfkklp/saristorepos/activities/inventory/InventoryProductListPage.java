package com.projectfkklp.saristorepos.activities.inventory;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.clans.fab.FloatingActionButton; // Use the correct FloatingActionButton class
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.projectfkklp.saristorepos.activities.user_login.UserLoginPage;
import com.projectfkklp.saristorepos.models.Product;
import com.projectfkklp.saristorepos.adapters.InventoryPageAdapter;
import com.projectfkklp.saristorepos.R;

import java.util.ArrayList;
import java.util.List;


public class InventoryProductListPage extends AppCompatActivity{

    // Add the following member variable
    private String userUid;
    FirebaseAuth mAuth;
    RecyclerView recyclerView;
    List<Product> dataList;
    List<Product> filteredDataList;
    CollectionReference productsCollection;
    ListenerRegistration eventListener;
    AlertDialog dialog;
    InventoryPageAdapter adapter;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_product_list_page);

        SearchView searchView = findViewById(R.id.search);

        FloatingActionButton fab = findViewById(R.id.fab); // Use the correct FloatingActionButton class
        recyclerView = findViewById(R.id.recyclerView);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        dataList = new ArrayList<>();
        filteredDataList = new ArrayList<>();

        adapter = new InventoryPageAdapter(this, filteredDataList);
        recyclerView.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();

        // Access "users" collection directly
        CollectionReference usersCollection = FirebaseFirestore.getInstance().collection("users");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        dialog = builder.create();

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

        // Use "users" collection in the eventListener
        eventListener = usersCollection
                .document(userUid)
                .collection("products")
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        dataList.clear(); // Clear existing data before updating
                        for (DocumentSnapshot document : value.getDocuments()) {
                            Product product = document.toObject(Product.class);
                            if (product != null) {
                                product.setKey(document.getId());
                                dataList.add(product);
                            } else {
                                // Handle the case when product is null
                                Log.e("FirestoreListener", "Product is null");
                            }
                        }

                        // Sort data alphabetically
                        dataList.sort((o1, o2) -> o1.getProduct().compareToIgnoreCase(o2.getProduct()));

                        // Apply in the view via filter method
                        filter("");
                    } else {
                        // Handle the case when value is null
                        Log.e("FirestoreListener", "Snapshot is null");
                    }
                });

        fab.setOnClickListener(v -> {
            Intent intent = new Intent(InventoryProductListPage.this, InventoryCreateProductPage.class);
            startActivity(intent);
        });

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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (eventListener != null) {
            eventListener.remove();
        }
    }

    protected  void filter(String searchText){
        filteredDataList.clear();
        for (Product item : dataList) {
            if (item.getProduct().toLowerCase().contains(searchText.toLowerCase())) {
                filteredDataList.add(item);
            }
        }
        adapter.notifyDataSetChanged();
    }
}
