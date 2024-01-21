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
import com.projectfkklp.saristorepos.activities.transaction.transaction_daily_summary.TransactionDailySummaryAdapter;
import com.projectfkklp.saristorepos.activities.transaction.transaction_history.TransactionHistoryAdapter;
import com.projectfkklp.saristorepos.models.DailyTransactions;
import com.projectfkklp.saristorepos.models.Product;
import com.projectfkklp.saristorepos.models.Transaction;
import com.projectfkklp.saristorepos.models.TransactionItem;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TransactionPage extends AppCompatActivity {
    Spinner modeDropdown;
    RecyclerView transactionRecycler;

    TransactionDailySummaryAdapter dailySummaryAdapter;
    TransactionHistoryAdapter historyAdapter;

    private List<Product> products;
    private List<DailyTransactions> dailyTransactions;
    private List<Transaction> transactions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_page);

        // Load & Sort Data
        {
            loadDailyTransactions();
            sortDailyTransactions();
            loadTransactions();
            sortTransactions();
        }

        initializeViews();
        initializeRecyclerView();
        initializeDropdown();
    }

    private void loadDailyTransactions(){
        products = new ArrayList<>(Arrays.asList(
            new Product("productabc001", "Product A", 5, 1, "", ""),
            new Product("productabc002", "Product B", 10, 5, "", ""),
            new Product("productabc003", "Product C", 15, 10, "", ""),
            new Product("productabc004", "Product D", 20, 20, "", ""),
            new Product("productabc005", "Product E", 25, 50, "", "")
        ));
        dailyTransactions = new ArrayList<>();

        LocalDate date = LocalDate.of(2024, 1, 1);
        for (int i=0; i < 30;i++){
            dailyTransactions.add(new DailyTransactions(
                date,
                new ArrayList<>(Arrays.asList(
                    new Transaction(
                        LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 10, 0, 0),
                        new ArrayList<>(Arrays.asList(
                            new TransactionItem("productabc001",1,1),
                            new TransactionItem("productabc002",1,5),
                            new TransactionItem("productabc003",1,10),
                            new TransactionItem("productabc004",1,20),
                            new TransactionItem("productabc005",1,50)
                        ))
                    ),
                    new Transaction(
                        LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 11, 0, 0),
                        new ArrayList<>(Arrays.asList(
                            new TransactionItem("productabc001",2,1),
                            new TransactionItem("productabc002",2,5),
                            new TransactionItem("productabc003",2,10),
                            new TransactionItem("productabc004",2,20),
                            new TransactionItem("productabc005",2,50)
                        ))
                    ),
                    new Transaction(
                        LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 12, 0, 0),
                        new ArrayList<>(Arrays.asList(
                            new TransactionItem("productabc001",3,1),
                            new TransactionItem("productabc002",3,5),
                            new TransactionItem("productabc003",3,10),
                            new TransactionItem("productabc004",3,20),
                            new TransactionItem("productabc005",3,50)
                        ))
                    )
                ))
            ));

            date = date.plusDays(1);
        }
    }

    private void sortDailyTransactions() {
        dailyTransactions.sort((dt1, dt2) -> dt2.getDate().compareTo(dt1.getDate()));
    }

    public void loadTransactions(){
        transactions = new ArrayList<>();

        for (DailyTransactions dailyTransactions : this.dailyTransactions){
            transactions.addAll(dailyTransactions.getTransactions());
        }
    }

    private void sortTransactions() {
        transactions.sort((t1, t2) -> t2.getDateTime().compareTo(t1.getDateTime()));
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
        historyAdapter = new TransactionHistoryAdapter(this, transactions);

        transactionRecycler.setAdapter(dailySummaryAdapter);
    }

    private void initializeDropdown(){
        List<String> items = Arrays.asList("Daily Summarized", "Not Summarized");
        List<RecyclerView.Adapter> transactionAdapters = Arrays.asList(dailySummaryAdapter, historyAdapter);
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