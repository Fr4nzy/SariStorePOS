package com.projectfkklp.saristorepos.activities.pos;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.activities.pos.checkout.CheckoutPage;
import com.projectfkklp.saristorepos.models.Product;
import com.projectfkklp.saristorepos.models.Store;
import com.projectfkklp.saristorepos.models.TransactionItem;
import com.projectfkklp.saristorepos.repositories.SessionRepository;
import com.projectfkklp.saristorepos.repositories.StoreRepository;
import com.projectfkklp.saristorepos.utils.CacheUtils;
import com.projectfkklp.saristorepos.utils.ProgressUtils;
import com.projectfkklp.saristorepos.utils.StringUtils;
import com.projectfkklp.saristorepos.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PosPage extends AppCompatActivity {
    private String searchStr = "";

    TextView selectedText;
    SearchView searchText;
    MaterialButton checkboutBtn;
    RecyclerView posRecycler;
    PosAdapter posAdapter;

    private List<Product> products;
    private List<TransactionItem> transactionItems;
    private List<Product> searchedProducts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pos_page);

        initializeData();
        initializeViews();
        initializedRecyclerView();
        initializeScanner();
        initializeOnBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();

        loadProducts();
    }

    private void initializeData(){
        products = new ArrayList<>();
        searchedProducts = new ArrayList<>();
        transactionItems = new ArrayList<>();
    }

    public void reloadViews(){
        int totalAmount = transactionItems.size();
        selectedText.setText(String.valueOf(totalAmount));

        checkboutBtn.setEnabled(transactionItems.size()>0);
    }

    private void initializeViews(){
        selectedText = findViewById(R.id.pos_selected_counts);
        searchText = findViewById(R.id.pos_search);
        checkboutBtn = findViewById(R.id.pos_checkout_btn);
        posRecycler = findViewById(R.id.product_picker_recycler_view);

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
        reloadViews();
    }

    private void initializedRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        posRecycler.setLayoutManager(layoutManager);

        posAdapter = new PosAdapter(this, searchedProducts, transactionItems);
        posRecycler.setAdapter(posAdapter);
    }

    private void initializeScanner(){
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setOrientationLocked(false); // Allow both portrait and landscape scanning
        integrator.setPrompt("Scan a barcode");
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadProducts(){
        ProgressUtils.showDialog(this, "Loading products...");
        StoreRepository
            .getStoreById(SessionRepository.getCurrentStore(this).getId())
            .addOnSuccessListener(successTask->{
                Store store = successTask.toObject(Store.class);
                assert store != null;
                products.clear();
                products.addAll(store.getProducts());

                loadTransactionItemsFromCache();
                search(searchStr);
            })
            .addOnFailureListener(failedTask-> ToastUtils.show(this, failedTask.getMessage()))
            .addOnCompleteListener(task-> ProgressUtils.dismissDialog())
        ;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadTransactionItemsFromCache(){
        transactionItems.clear();
        transactionItems.addAll(CacheUtils.getObjectList(this, "transaction_items", TransactionItem.class));
        posAdapter.notifyDataSetChanged();

        reloadViews();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void search(String searchText){
        searchStr = searchText;
        searchedProducts.clear();
        searchedProducts.addAll(
            products
                .stream()
                .filter(product->
                    product.getName().toLowerCase().contains(searchText.toLowerCase())
                    || (
                        searchText.startsWith("::")
                        && !StringUtils.isNullOrEmpty(product.getBarcode())
                        && product.getBarcode().contains(searchText.substring(2))
                    )
                )
                .collect(Collectors.toList()));
        searchedProducts.sort((product1, product2)->product1.getName().compareToIgnoreCase(product2.getName()));
        posAdapter.notifyDataSetChanged();
    }

    public void scan(View view){
        new IntentIntegrator(PosPage.this).initiateScan();
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

    private void searchProductByBarcode(String barcode){
        String searchStr = "::"+barcode;
        searchText.setQuery(searchStr, true);
        search(searchStr);
    }

    public void checkout(View view){
        CacheUtils.saveObjectList(this, "transaction_items", transactionItems);
        startActivity(new Intent(this, CheckoutPage.class));
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

    @SuppressLint("NotifyDataSetChanged")
    public void notifyDataSetChanged(){
        posAdapter.notifyDataSetChanged();
    }

    public List<TransactionItem> getTransactionItems() {
        return transactionItems;
    }

    private void initializeOnBackPressed(){
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navigateBack(null);
            }
        });
    }
}