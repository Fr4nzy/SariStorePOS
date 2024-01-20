package com.projectfkklp.saristorepos.activities.inventory;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;


import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.models.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
;
public class InventoryProductListPage extends AppCompatActivity {
    SearchView searchText;
    RecyclerView productListRecycler;
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
    }

    private void initializeData(){
        products = new ArrayList<>();
        searchedProducts = new ArrayList<>();

        for (int i=0;i<100;i++){
            products.add(new Product(
                "",
                (i < 50? "Test Product ": "Sample Product ")+i,
                i%20,
                (i+1)*10,
                "",
                ""
            ));
        }
    }

    private void initializeViews(){
        searchText = findViewById(R.id.inventory_product_list_search);
        productListRecycler = findViewById(R.id.inventory_product_list_recycler);

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

        search("");
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