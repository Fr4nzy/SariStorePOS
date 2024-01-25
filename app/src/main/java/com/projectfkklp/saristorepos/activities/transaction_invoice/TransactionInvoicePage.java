package com.projectfkklp.saristorepos.activities.transaction_invoice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.models.DailyTransactions;
import com.projectfkklp.saristorepos.models.Transaction;
import com.projectfkklp.saristorepos.models.TransactionItem;
import com.projectfkklp.saristorepos.utils.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class TransactionInvoicePage extends AppCompatActivity {
    public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
            "MMMM d, yyyy",
            Locale.getDefault()
    );
    public static SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat(
            "MMMM d, yyyy hh:mm a",
            Locale.getDefault()
    );

    RecyclerView invoiceRecycler;
    TextView titleText;
    TextView totalAmountText;
    TransactionInvoiceAdapter invoiceAdapter;

    private String title;
    private List<Transaction> transactions;
    private List<String> transactedProductIds;
    private float totalAmount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_invoice);

        // Load Data
        {
            loadTitleAndTransactions();
            loadTransactedProductIds();
        }

        loadInitialViews();
        initializeRecyclerView();
    }

    @SuppressLint("NewApi")
    private void loadTitleAndTransactions(){
        String src = getIntent().getStringExtra("src");
        assert src != null;
        if (src.equals("dailyTransactions")) {
            DailyTransactions dailyTransactions = getIntent().getSerializableExtra("dailyTransactions", DailyTransactions.class);
            assert dailyTransactions != null;
            title = String.format(
                "Summary Invoice — %s",
                DATE_FORMAT.format(dailyTransactions.getDate())
            );
            transactions = dailyTransactions.getTransactions();
        }
        else {
            Transaction transaction = getIntent().getSerializableExtra("transaction", Transaction.class);
            assert transaction != null;
            title = String.format(
                "Invoice — %s",
                DATETIME_FORMAT.format(transaction.getDateTime())
            );
            transactions = Collections.singletonList(transaction);
        }

        totalAmount = (float) transactions.stream()
            .mapToDouble(Transaction::calculateTotalSales)
            .sum();
    }

    private void loadTransactedProductIds(){
        // Filter transactedProductIds
        transactedProductIds = new ArrayList<>();
        for (Transaction transaction: transactions){
            for (TransactionItem item:transaction.getItems()){
                transactedProductIds.add(item.getProductId());
            }
        }

        transactedProductIds = new ArrayList<>(new HashSet<>(transactedProductIds));
    }

    private void loadInitialViews(){
        invoiceRecycler = findViewById(R.id.transaction_invoice_recycler);
        titleText = findViewById(R.id.transaction_invoice_title);
        totalAmountText = findViewById(R.id.transaction_invoice_total_amount);

        titleText.setText(title);
        totalAmountText.setText(StringUtils.formatToPeso(totalAmount));
    }

    private void initializeRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        invoiceRecycler.setLayoutManager(layoutManager);

        // Set up adapter
        invoiceAdapter = new TransactionInvoiceAdapter(this, transactedProductIds, transactions);

        invoiceRecycler.setAdapter(invoiceAdapter);
    }

    public void createPdf(View view){

    }

    public void navigateBack(View view){
        finish();
    }
}