package com.projectfkklp.saristorepos.activities.pos.checkout;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.activities.transaction_invoice.TransactionInvoicePage;
import com.projectfkklp.saristorepos.managers.DailyTransactionsManager;
import com.projectfkklp.saristorepos.models.Product;
import com.projectfkklp.saristorepos.models.Store;
import com.projectfkklp.saristorepos.models.Transaction;
import com.projectfkklp.saristorepos.models.TransactionItem;
import com.projectfkklp.saristorepos.repositories.SessionRepository;
import com.projectfkklp.saristorepos.repositories.StoreRepository;
import com.projectfkklp.saristorepos.utils.CacheUtils;
import com.projectfkklp.saristorepos.utils.ProgressUtils;
import com.projectfkklp.saristorepos.utils.StringUtils;
import com.projectfkklp.saristorepos.utils.ToastUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CheckoutPage extends AppCompatActivity {
    TextView totalAmountText;
    MaterialButton submitBtn;
    RecyclerView posRecycler;
    CardView emptyCard;
    CheckoutAdapter posAdapter;

    private List<Product> products;
    private List<TransactionItem> transactionItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout_page);

        initializeData();
        initializeViews();
        initializedRecyclerView();

        loadProducts();

        initializeOnBackPressed();
    }

    private void initializeData(){
        products = new ArrayList<>();
        transactionItems = new ArrayList<>();
    }

    private void initializeViews(){
        totalAmountText = findViewById(R.id.pos_total_amount);
        posRecycler = findViewById(R.id.pos_recycler_view);
        emptyCard = findViewById(R.id.pos_empty_card);
        submitBtn = findViewById(R.id.pos_submit);
    }

    private void initializedRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        posRecycler.setLayoutManager(layoutManager);

        // Set up adapter
        posAdapter = new CheckoutAdapter(this, transactionItems, products);
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
        transactionItems.addAll(CacheUtils.getObjectList(this, "transaction_items", TransactionItem.class));
        posAdapter.notifyDataSetChanged();

        reloadViews();
    }

    public void reloadViews(){
        float totalAmount = (float) transactionItems.stream().mapToDouble(TransactionItem::calculateAmount).sum();
        totalAmountText.setText(StringUtils.formatToPeso(totalAmount));

        submitBtn.setEnabled(transactionItems.size()>0);
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

                reloadViews();
            })
            .setNegativeButton("No", (dialog, which) -> {
                dialog.dismiss();
            })
            .show();
    }

    public void addMoreItems(View view){
        CacheUtils.saveObjectList(this, "transaction_items", transactionItems);
        finish();
    }

    public void submit(View view){
        String dateTime = LocalDateTime.now().toString();
        Transaction transaction = new Transaction(
            dateTime,
            transactionItems
        );

        ProgressUtils.showDialog(this, "Submitting...");
        DailyTransactionsManager
            .addTransaction(this, transaction)
            .addOnSuccessListener(task->{
                // Clear cache
                CacheUtils.saveObjectList(this, "transaction_items", new ArrayList<>());

                // Goto Invoice Page
                Intent intent = new Intent(this, TransactionInvoicePage.class);
                intent.putExtra("src", "transaction");
                intent.putExtra("transaction", transaction);

                startActivity(intent);
                finish();
            })
            .addOnFailureListener(failedTask-> ToastUtils.show(this, failedTask.getMessage()))
            .addOnCompleteListener(task-> ProgressUtils.dismissDialog())
        ;
    }

    private void initializeOnBackPressed(){
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                addMoreItems(null);
            }
        });
    }
}