package com.projectfkklp.saristorepos.activities.pos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.activities.pos.checkout.CheckoutPage;
import com.projectfkklp.saristorepos.models.Product;
import com.projectfkklp.saristorepos.models.Store;
import com.projectfkklp.saristorepos.repositories.SessionRepository;
import com.projectfkklp.saristorepos.repositories.StoreRepository;
import com.projectfkklp.saristorepos.utils.ProgressUtils;
import com.projectfkklp.saristorepos.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class PosPage extends AppCompatActivity {

    RecyclerView productPickerRecycler;
    PosAdapter productPickerAdapter;

    private List<Product> products;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pos_page);

        initializeData();
        initializeViews();
        initializedRecyclerView();

        loadProducts();
    }
    private void initializeData(){
        products = new ArrayList<>();
    }

    private void initializeViews(){
        productPickerRecycler = findViewById(R.id.product_picker_recycler_view);
    }

    private void initializedRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        productPickerRecycler.setLayoutManager(layoutManager);

        productPickerAdapter = new PosAdapter(this, products);
        productPickerRecycler.setAdapter(productPickerAdapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadProducts(){
        ProgressUtils.showDialog(this, "Loading products...");
        StoreRepository
            .getStoreById(SessionRepository.getCurrentStore(this).getId())
            .addOnSuccessListener(successTask->{
                Store store = successTask.toObject(Store.class);
                assert store != null;
                products.addAll(store.getProducts());

                productPickerAdapter.notifyDataSetChanged();
            })
            .addOnFailureListener(failedTask-> ToastUtils.show(this, failedTask.getMessage()))
            .addOnCompleteListener(task-> ProgressUtils.dismissDialog())
        ;
    }

    public void checkout(View view){
        startActivity(new Intent(this, CheckoutPage.class));
    }

    public void navigateBack(View view){
        finish();
    }
}