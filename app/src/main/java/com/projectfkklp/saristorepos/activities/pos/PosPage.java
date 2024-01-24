package com.projectfkklp.saristorepos.activities.pos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.models.Product;
import com.projectfkklp.saristorepos.models.Store;
import com.projectfkklp.saristorepos.models.TransactionItem;
import com.projectfkklp.saristorepos.repositories.SessionRepository;
import com.projectfkklp.saristorepos.repositories.StoreRepository;
import com.projectfkklp.saristorepos.utils.ProgressUtils;
import com.projectfkklp.saristorepos.utils.StringUtils;
import com.projectfkklp.saristorepos.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class PosPage extends AppCompatActivity {
    TextView totalAmountText;
    MaterialButton submitBtn;
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
        totalAmountText = findViewById(R.id.pos_total_amount);
        posRecycler = findViewById(R.id.pos_recycler_view);
        emptyCard = findViewById(R.id.pos_empty_card);
        submitBtn = findViewById(R.id.pos_submit);
    }

    private void initializedRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        posRecycler.setLayoutManager(layoutManager);

        // Set up adapter
        posAdapter = new PosAdapter(this, transactionItems, products);
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
        for (int i=1; i<=10;i++){
            Product product = products.get(i);
            transactionItems.add(new TransactionItem(
                product.getId(),
                i,
                product.getUnitPrice()
            ));
        }
        posAdapter.notifyDataSetChanged();

        reloadViews();
    }

    public void reloadViews(){
        float totalAmount = (float) transactionItems.stream().mapToDouble(TransactionItem::getAmount).sum();
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