package com.projectfkklp.saristorepos.activities.transaction_invoice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.models.DailyTransactions;
import com.projectfkklp.saristorepos.models.Transaction;
import com.projectfkklp.saristorepos.models.TransactionItem;
import com.projectfkklp.saristorepos.utils.Serializer;
import com.projectfkklp.saristorepos.utils.StringUtils;
import com.projectfkklp.saristorepos.utils.ToastUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class TransactionInvoicePage extends AppCompatActivity {
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy hh:mm a");

    RecyclerView invoiceRecycler;
    TextView titleText;
    TextView totalAmountText;
    TransactionInvoiceAdapter invoiceAdapter;

    private String invoiceFilename;
    private String invoiceTitle;
    private String invoiceDate;
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

    private void loadTitleAndTransactions(){
        String src = getIntent().getStringExtra("src");
        assert src != null;
        if (src.equals("dailyTransactions")) {
            DailyTransactions dailyTransactions = Serializer.deserialize(getIntent().getStringExtra("dailyTransactions"), DailyTransactions.class);
            assert dailyTransactions != null;
            invoiceTitle = "Orders Summary of the Day";
            invoiceDate = LocalDate.parse(dailyTransactions.getDate()).format(dateFormatter);
            invoiceFilename = String.format("daily-summary-%s.pdf", dailyTransactions.getDate());
            title = String.format(
                "Summary Invoice — %s",
                invoiceDate
            );
            transactions = dailyTransactions.getTransactions();
        }
        else {
            Transaction transaction = Serializer.deserialize(getIntent().getStringExtra("transaction"), Transaction.class);
            assert transaction != null;
            invoiceTitle = "Orders Receipt";
            invoiceDate = LocalDateTime.parse(transaction.getDateTime()).format(dateTimeFormatter);
            invoiceFilename = String.format(
                "order-receipt-%s.pdf",
                LocalDateTime.parse(transaction.getDateTime()).format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
            );
            title = String.format(
                "Invoice — %s",
                invoiceDate
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
        try{
            new TransactionInvoicePdfWriter(
        this,
                invoiceFilename,
                invoiceTitle,
                invoiceDate,
                transactedProductIds,
                transactions
            ).start();
        }
        catch (Exception ignored){
            ToastUtils.show(this, "Error writing pdf");
        }
    }

    public void navigateBack(View view){
        finish();
    }
}