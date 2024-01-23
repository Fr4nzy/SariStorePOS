package com.projectfkklp.saristorepos.activities.inventory;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.appcompat.widget.SearchView;


import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.models.Product;
import com.projectfkklp.saristorepos.models.Store;
import com.projectfkklp.saristorepos.repositories.SessionRepository;
import com.projectfkklp.saristorepos.repositories.StoreRepository;
import com.projectfkklp.saristorepos.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
;
public class InventoryProductListPage extends AppCompatActivity {
    SearchView searchText;
    RecyclerView productListRecycler;
    ProgressBar progressBar;
    FrameLayout emptyFrame;
    InventoryProductListAdapter inventoryProductListAdapter;

    private List<Product> products;
    private List<Product> searchedProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_product_list_page);

        initializeData();
        initializeViews();
        initializedRecyclerView();
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
        StoreRepository
            .getStoreById(SessionRepository.getCurrentStore(this).getId())
            .addOnSuccessListener(successTask->{
                Store store = successTask.toObject(Store.class);

                assert store != null;
                products.addAll(store.getProducts());
                emptyFrame.setVisibility(products.isEmpty()? View.VISIBLE: View.GONE);
                search("");
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
        searchedProducts.clear();
        searchedProducts.addAll(products.stream().filter(product->product.getName().toLowerCase().contains(searchText.toLowerCase())).collect(Collectors.toList()));
        searchedProducts.sort((product1, product2)->product1.getName().compareToIgnoreCase(product2.getName()));
        inventoryProductListAdapter.notifyDataSetChanged();
    }

}