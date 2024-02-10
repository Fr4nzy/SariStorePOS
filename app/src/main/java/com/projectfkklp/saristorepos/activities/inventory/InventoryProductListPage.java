package com.projectfkklp.saristorepos.activities.inventory;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.appcompat.widget.SearchView;

import com.github.clans.fab.FloatingActionButton;
import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.models.Product;
import com.projectfkklp.saristorepos.repositories.ProductRepository;
import com.projectfkklp.saristorepos.utils.Serializer;
import com.projectfkklp.saristorepos.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InventoryProductListPage extends AppCompatActivity {
    SearchView searchText;
    FloatingActionButton addFAB;
    RecyclerView productListRecycler;
    ProgressBar progressBar;
    FrameLayout emptyFrame;
    InventoryProductListAdapter inventoryProductListAdapter;

    private List<Product> products;
    private List<Product> searchedProducts;
    private String searchStr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_product_list_page);

        initializeData();
        initializeViews();
        initializedRecyclerView();
    }

    @Override
    protected void onStart() {
        super.onStart();

        loadProducts();
    }

    private void initializeData(){
        products = new ArrayList<>();
        searchedProducts = new ArrayList<>();
    }

    private void initializeViews(){
        searchText = findViewById(R.id.inventory_product_list_search);
        productListRecycler = findViewById(R.id.inventory_product_list_recycler);
        progressBar = findViewById(R.id.inventory_product_list_progress);
        emptyFrame = findViewById(R.id.inventory_product_list_empty_frame);
        addFAB = findViewById(R.id.inventory_product_list_add_fab);

        searchText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search(newText);
                return false;
            }
        });

        addFAB.setOnClickListener(v -> {
            Intent intent = new Intent(this, InventoryProductDetailPage.class);
            intent.putExtra("Product", Serializer.serialize(new Product()));
            startActivity(intent);
        });
    }

    private void initializedRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        productListRecycler.setLayoutManager(layoutManager);

        // Set up adapter
        inventoryProductListAdapter = new InventoryProductListAdapter(this, searchedProducts);
        productListRecycler.setAdapter(inventoryProductListAdapter);
    }

    private void loadProducts(){
        progressBar.setVisibility(View.VISIBLE);
        ProductRepository
            .getActiveProducts(this)
            .addOnSuccessListener(activeProducts->{
                products.clear();
                products.addAll(activeProducts);
                emptyFrame.setVisibility(products.isEmpty()? View.VISIBLE: View.GONE);
                search(searchStr);
            })
            .addOnFailureListener(failedTask-> ToastUtils.show(this, failedTask.getMessage()))
            .addOnCompleteListener(task-> progressBar.setVisibility(View.GONE))
        ;
    }

    public void navigateBack(android.view.View view){
        finish();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void search(String searchText){
        searchStr = searchText;
        searchedProducts.clear();
        searchedProducts.addAll(products.stream().filter(product->product.getName().toLowerCase().contains(searchText.toLowerCase())).collect(Collectors.toList()));
        searchedProducts.sort((product1, product2)->product1.getName().compareToIgnoreCase(product2.getName()));
        inventoryProductListAdapter.notifyDataSetChanged();
    }

}