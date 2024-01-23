package com.projectfkklp.saristorepos.activities.pos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;

import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.activities.store_selector.StoreSelectorPage;
import com.projectfkklp.saristorepos.managers.UserStoreRelationManager;
import com.projectfkklp.saristorepos.models.Product;
import com.projectfkklp.saristorepos.models.Store;
import com.projectfkklp.saristorepos.models.TransactionItem;
import com.projectfkklp.saristorepos.repositories.SessionRepository;
import com.projectfkklp.saristorepos.repositories.StoreRepository;
import com.projectfkklp.saristorepos.utils.ActivityUtils;
import com.projectfkklp.saristorepos.utils.ProgressUtils;
import com.projectfkklp.saristorepos.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class PosPage extends AppCompatActivity {
    RecyclerView posRecycler;
    CardView emptyCard;
    PosAdapter posAdapter;

    private List<Product> products;
    private List<TransactionItem> transactionItems;

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
        transactionItems = new ArrayList<>();
    }

    private void initializeViews(){
        posRecycler = findViewById(R.id.pos_recycler_view);
        emptyCard = findViewById(R.id.pos_empty_card);
    }

    private void initializedRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        posRecycler.setLayoutManager(layoutManager);

        // Set up adapter
        posAdapter = new PosAdapter(this, transactionItems);
        posRecycler.setAdapter(posAdapter);
    }

    private void loadProducts(){
        ProgressUtils.showDialog(this, "Loading products...");
        StoreRepository
            .getStoreById(SessionRepository.getCurrentStore(this).getId())
            .addOnSuccessListener(successTask->{
                Store store = successTask.toObject(Store.class);
                assert store != null;
                products.addAll(store.getProducts());

                loadTransactionItemsFromCache();
            })
            .addOnFailureListener(failedTask-> ToastUtils.show(this, failedTask.getMessage()))
            .addOnCompleteListener(task-> ProgressUtils.dismissDialog())
        ;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadTransactionItemsFromCache(){
        for (int i=0; i<10;i++){
            transactionItems.add(new TransactionItem(

            ));
        }
        posAdapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void reset(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
            .setTitle("Reset Fields?")
            .setMessage("Are you sure you want to clear all items?")
            .setPositiveButton("Yes", (dialog, which) -> {
                transactionItems.clear();
                posAdapter.notifyDataSetChanged();
                emptyCard.setVisibility(View.VISIBLE);
            })
            .setNegativeButton("No", (dialog, which) -> {
                dialog.dismiss();
            })
            .show();
    }

    public void navigateBack(View view){
        if (transactionItems.size()==0){
            finish();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
            .setTitle("Cancel Transaction?")
            .setMessage("Are you sure you want to cancel this transaction?")
            .setPositiveButton("Yes", (dialog, which) -> {
                finish();
            })
            .setNegativeButton("No", (dialog, which) -> {
                dialog.dismiss();
            })
            .show();

    }
}