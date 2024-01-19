package com.projectfkklp.saristorepos.activities.inventory;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;

import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.activities.store_selector.StoreSelectorAdapter;
import com.projectfkklp.saristorepos.models.Product;

import java.util.ArrayList;
import java.util.Arrays;

public class InventoryProductListPage extends AppCompatActivity {

    EditText searchEditText;
    RecyclerView productListRecycler;
    InventoryProductListAdapter inventoryProductListAdapter;

    private ArrayList<Product> products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_product_list_page);

        initializeData();
        initializeViews();
        initializedRecyclerView();
    }

    private void initializeData(){
        products = new ArrayList<>(Arrays.asList(
            new Product(
                "",
                "Product 1",
                100,
                12,
                "",
                ""
            ),
            new Product(
                "",
                "Product 2",
                50,
                100,
                "",
                ""
            ),
            new Product(
                "",
                "Product 3",
                20,
                30,
                "",
                ""
            ),
            new Product(
                "",
                "Product 4",
                70,
                123,
                "",
                ""
            ),
            new Product(
                "",
                "Product 5",
                80,
                40,
                "",
                ""
            ),
            new Product(
                "",
                "Product 6",
                12,
                10,
                "",
                ""
            ),
            new Product(
                "",
                "Product 7",
                90,
                99,
                "",
                ""
            ),
            new Product(
                "",
                "Product 8",
                45,
                54,
                "",
                ""
            )
        ));
    }

    private void initializeViews(){
        productListRecycler = findViewById(R.id.inventory_product_list_recycler);
    }

    private void initializedRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        productListRecycler.setLayoutManager(layoutManager);

        // Set up adapter
        inventoryProductListAdapter = new InventoryProductListAdapter(this, products);
        productListRecycler.setAdapter(inventoryProductListAdapter);
    }

    public void navigateBack(android.view.View view){
        finish();
    }

}