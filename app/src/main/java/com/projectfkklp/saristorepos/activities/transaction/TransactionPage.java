package com.projectfkklp.saristorepos.activities.transaction;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.models.DailyTransactions;
import com.projectfkklp.saristorepos.models.Product;
import com.projectfkklp.saristorepos.models.Transaction;
import com.projectfkklp.saristorepos.models.TransactionItem;
import com.projectfkklp.saristorepos.utils.ModelUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TransactionPage extends AppCompatActivity {
    Spinner modeDropdown;
    RecyclerView transactionRecycler;

    TransactionDailySummaryAdapter dailySummaryAdapter;

    private List<Product> products;
    private List<DailyTransactions> dailyTransactions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_page);

        loadDailyTransactions();
        initializeViews();
        initializeRecyclerView();
        initializeDropdown();
    }

    private void loadDailyTransactions(){
        products = new ArrayList<>();
        dailyTransactions = new ArrayList<>();
        ArrayList<Transaction> transactions = new ArrayList<>();
        ArrayList<TransactionItem> transactionItems = new ArrayList<>();

        // Set Products
        for (int i=0; i<5;i++){
            products.add(new Product(
                ModelUtils.createUUID(),
                "Product "+i,
                12,
                25,
                "",
                ""
            ));
        }

        // Set Transaction Items
        for (int i=0;i<5;i++){
            Product product = products.get(i);
            transactionItems.add(new TransactionItem(
                product.getId(),
                i+1,
                product.getUnitPrice()
            ));
        }

        // Set Transactions
        LocalTime time = LocalTime.of(8, 0, 0);
        for (int i=0;i<5;i++){
            transactions.add(new Transaction(
                time,
                transactionItems
            ));
            time = time.plusHours(1);
        }

        // Set Daily Transactions
        LocalDate date = LocalDate.of(2024, 1, 1);
        for (int i=0;i<30;i++) {
            dailyTransactions.add(new DailyTransactions(
                date,
                    transactions
            ));

            date = date.plusDays(1);
        }
    }

    private void initializeViews(){
        modeDropdown = findViewById(R.id.transaction_dropdown);
        transactionRecycler = findViewById(R.id.transaction_recycler);
    }

    private void initializeRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        transactionRecycler.setLayoutManager(layoutManager);

        // Set up adapter
        dailySummaryAdapter = new TransactionDailySummaryAdapter(this, dailyTransactions);

        transactionRecycler.setAdapter(dailySummaryAdapter);
    }

    private void initializeDropdown(){
        List<String> items = Arrays.asList("Daily Summarized", "Not Summarized");
        List<RecyclerView.Adapter> transactionAdapters = Arrays.asList(dailySummaryAdapter, dailySummaryAdapter);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modeDropdown.setAdapter(adapter);

        modeDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Handle item selection
                String selectedItem = (String) parentView.getItemAtPosition(position);
                int selectedItemIndex = items.indexOf(selectedItem);

                transactionRecycler.setAdapter(transactionAdapters.get(selectedItemIndex));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here, or handle the case where nothing is selected
            }
        });
    }

    public void navigateBack(View view){
        finish();
    }

}