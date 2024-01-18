package com.projectfkklp.saristorepos.activities.transaction.transaction_history;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.activities.transaction.transaction_daily_summary.TransactionDailySummaryPage;
import com.projectfkklp.saristorepos.models._Transaction;
import com.projectfkklp.saristorepos.repositories.TransactionRepository;
import com.projectfkklp.saristorepos.utils.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TransactionHistoryPage extends AppCompatActivity {

    private TransactionHistoryAdapter adapter;
    private List<_Transaction> transactionList;
    private Date lowerDate;
    private Date upperDate;
    private Date firstTransactionDate;
    private int page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_history_page);

        RecyclerView recyclerView = findViewById(R.id.transactionHistoryRV);
        transactionList = new ArrayList<>();
        adapter = new TransactionHistoryAdapter(transactionList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Start Pagination
        TransactionRepository.getFirstTransaction(transaction -> {
            firstTransactionDate = DateUtils.parse(transaction.getDate());
            setPage(0);
        });
    }

    public void onPrevClick(View view) {
        setPage(page+1);
    }

    public void onNextClick(View view) {
        setPage(page-1);
    }

    public void onSwitchClick(View view){
        Intent i = new Intent(this, TransactionDailySummaryPage.class);
        startActivity(i);
        finish();
    }

    void setPage(int page){
        Button nextButton = (Button)findViewById(R.id.history_next_button);
        Button prevButton = (Button)findViewById(R.id.history_prev_button);
        this.page = page;
        if (this.page==0){
            nextButton.setEnabled(false);
            nextButton.setBackgroundResource(R.drawable.navigate_right_disabled_icon);
        }
        else {
            nextButton.setEnabled(true);
            nextButton.setBackgroundResource(R.drawable.navigate_right_enabled_icon);
        }

        lowerDate = DateUtils.addDays(new Date(), 1-30*(page+1));
        upperDate = DateUtils.addDays(new Date(), -(30*page));
        if (firstTransactionDate.before(lowerDate)) {
            prevButton.setEnabled(true);
            prevButton.setBackgroundResource(R.drawable.navigate_left_enabled_icon);
        }
        else {
            prevButton.setEnabled(false);
            prevButton.setBackgroundResource(R.drawable.navigate_left_disabled_icon);
        }

        try {
            TransactionRepository.retrieveAllTransactions(
                    (transactions) -> {
                        transactionList.clear();
                        transactionList.addAll(transactions);
                        adapter.notifyDataSetChanged();

                        TextView overallSummaryTextView = findViewById(R.id.history_summary_overall);

                        // Set the text of the TextView
                        overallSummaryTextView.setText(String.format(
                                "%s to %s",
                                DateUtils.formatDate(lowerDate),
                                DateUtils.formatDate(upperDate)
                        ));
                    },
                    this,
                    this.page
            );
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

}
