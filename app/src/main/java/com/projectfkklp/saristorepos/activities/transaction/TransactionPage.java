package com.projectfkklp.saristorepos.activities.transaction;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.activities.transaction.transaction_daily_summary.TransactionDailySummaryAdapter;
import com.projectfkklp.saristorepos.activities.transaction.transaction_history.TransactionHistoryAdapter;
import com.projectfkklp.saristorepos.models.DailyTransactions;
import com.projectfkklp.saristorepos.models.Transaction;
import com.projectfkklp.saristorepos.repositories.DailyTransactionsRepository;
import com.projectfkklp.saristorepos.utils.ToastUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class TransactionPage extends AppCompatActivity {
    Spinner modeDropdown;
    RecyclerView transactionRecycler;
    FrameLayout emptyFrame;
    ProgressBar progressBar;
    MaterialButton prevButton, nextButton;
    TextView dateRangeText;

    // for pagination
    private final DateTimeFormatter withYearDateFormatter = DateTimeFormatter.ofPattern("yyyy MMM, d");
    private final DateTimeFormatter withOutYearDateFormatter = DateTimeFormatter.ofPattern("MMM, d");
    private LocalDate firstTransactionDate;
    private int page = 0;

    TransactionDailySummaryAdapter dailySummaryAdapter;
    TransactionHistoryAdapter historyAdapter;

    private List<DailyTransactions> dailyTransactions;
    private List<Transaction> transactions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_page);

        dailyTransactions = new ArrayList<>();
        transactions = new ArrayList<>();
        initializeViews();
        initializeRecyclerView();
        initializeDropdown();
    }

    @Override
    protected void onStart() {
        super.onStart();

        progressBar.setVisibility(View.VISIBLE);
        DailyTransactionsRepository
            .getFirstDailyTransactions(this)
            .addOnSuccessListener(task->{
                List<DailyTransactions> dailyTransactions = task.toObjects(DailyTransactions.class);
                Optional<DailyTransactions> optionalDailyTransaction = dailyTransactions.stream().findFirst();

                if (optionalDailyTransaction.isPresent()){
                    firstTransactionDate = LocalDate.parse(optionalDailyTransaction.get().getDate());
                    setPage(page);
                    return;
                }

                progressBar.setVisibility(View.GONE);
                emptyFrame.setVisibility(View.VISIBLE);
            })
            .addOnFailureListener(failedTask-> ToastUtils.show(this, failedTask.getMessage()) )
        ;
    }

    private void initializeViews(){
        modeDropdown = findViewById(R.id.transaction_dropdown);
        transactionRecycler = findViewById(R.id.transaction_recycler);
        progressBar = findViewById(R.id.transaction_progress);
        emptyFrame = findViewById(R.id.transaction_empty_frame);
        dateRangeText = findViewById(R.id.transaction_date_range);
        prevButton = findViewById(R.id.transaction_prev_btn);
        nextButton = findViewById(R.id.transaction_next_btn);
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

    @SuppressLint("NotifyDataSetChanged")
    private void refreshData(){
        sortDailyTransactions();
        loadTransactions();
        sortTransactions();

        historyAdapter.notifyDataSetChanged();
        dailySummaryAdapter.notifyDataSetChanged();
    }

    private void setPage(int page){
        progressBar.setVisibility(View.VISIBLE);
        dailyTransactions.clear();

        this.page = page;
        if (this.page==0){
            nextButton.setEnabled(false);
            nextButton.setBackgroundResource(R.drawable.navigate_right_disabled_icon);
        }
        else {
            nextButton.setEnabled(true);
            nextButton.setBackgroundResource(R.drawable.navigate_right_enabled_icon);
        }

        LocalDate currentDate = LocalDate.now();
        long paginationLimit = DailyTransactionsRepository.PAGINATION_LIMIT;
        LocalDate lowerDate = currentDate.plusDays(1 - paginationLimit * (page+1) );
        LocalDate upperDate = currentDate.plusDays(-(paginationLimit *page) );

        if (firstTransactionDate.isBefore(lowerDate)) {
            prevButton.setEnabled(true);
            prevButton.setBackgroundResource(R.drawable.navigate_left_enabled_icon);
        }
        else {
            prevButton.setEnabled(false);
            prevButton.setBackgroundResource(R.drawable.navigate_left_disabled_icon);
        }

        dateRangeText.setText(String.format(
            "%s — %s",
            lowerDate.format(withYearDateFormatter),
            upperDate.format(
                lowerDate.getYear()==upperDate.getYear()
                    ? withOutYearDateFormatter
                    : withYearDateFormatter
            )
        ));

        DailyTransactionsRepository
            .getDailyTransactions(this, page)
            .addOnSuccessListener(task->{
                dailyTransactions.addAll(task.toObjects(DailyTransactions.class));

                emptyFrame.setVisibility(dailyTransactions.isEmpty() ? View.VISIBLE: View.GONE);
                refreshData();
            })
            .addOnFailureListener(failedTask-> ToastUtils.show(this, failedTask.getMessage()) )
            .addOnCompleteListener(completeTask-> progressBar.setVisibility(View.GONE))
        ;
    }

    public void prev(View view){
        setPage(page+1);
    }

    public void next(View view){
        setPage(page-1);
    }

    private void sortDailyTransactions() {
        dailyTransactions.sort((dt1, dt2) -> dt2.getDate().compareTo(dt1.getDate()));
    }

    public void loadTransactions(){
        transactions.clear();
        for (DailyTransactions dailyTransactions : this.dailyTransactions){
            transactions.addAll(dailyTransactions.getTransactions());
        }
    }

    private void sortTransactions() {
        transactions.sort((t1, t2) -> t2.getDateTime().compareTo(t1.getDateTime()));
    }

    public void navigateBack(View view){
        finish();
    }

}