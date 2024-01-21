package com.projectfkklp.saristorepos.activities.transaction.transaction_daily_summary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.projectfkklp.saristorepos.activities.transaction.transaction_history._TransactionHistoryPage;
import com.projectfkklp.saristorepos.models._Product;
import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.models._Transaction;
import com.projectfkklp.saristorepos.models.DailySalesSummaryBreakdown;
import com.projectfkklp.saristorepos.models.DailySalesSummary;
import com.projectfkklp.saristorepos.repositories.TransactionRepository;
import com.projectfkklp.saristorepos.utils.DateUtils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class _TransactionDailySummaryPage extends AppCompatActivity {
    public List<_Transaction> transactionList;
    public List<DailySalesSummary> summaryList;
    private _TransactionDailySummaryAdapter adapter;
    private Date lowerDate;
    private Date upperDate;
    private Date firstTransactionDate;
    private int page;

    TextView overallSummaryTextview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout._transaction_daily_summary_page);

        overallSummaryTextview = findViewById(R.id.summary_overall);
        RecyclerView recyclerView = findViewById(R.id.summary_content);
        summaryList = new ArrayList<>();
        adapter = new _TransactionDailySummaryAdapter(summaryList);

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
        Intent i = new Intent(this, _TransactionHistoryPage.class);
        startActivity(i);
        finish();
    }

    void setPage(int page){
        Button nextButton = (Button)findViewById(R.id.next_button);
        Button prevButton = (Button)findViewById(R.id.prev_button);
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
                    transactionList=transactions;
                    summaryList.clear();
                    showBreakDownOfSales();
                },
                this,
                this.page
            );
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void showBreakDownOfSales() throws ParseException {
        setSummaryList(transactionList, summaryList);
        Collections.reverse(summaryList);
        adapter.notifyDataSetChanged();

        // Set Overall Summary Textview
        TextView overallSummaryTextView = findViewById(R.id.summary_overall);
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        DecimalFormat intFormat = new DecimalFormat("#,##0");
        // Set the text of the TextView
        overallSummaryTextView.setText(String.format(
                Locale.getDefault(),
                DateUtils.formatDate(lowerDate)
                    +" to "
                    +DateUtils.formatDate(upperDate)
                    +"\n\nTotal Items: %s\nTotal Price: ₱%s",
                intFormat.format(getOverallQuantity()),
                decimalFormat.format(getOverallPrice())
        ));
    }

    int getOverallQuantity() {
        int overallQuantity = 0;
        for (DailySalesSummary summary:summaryList) {
            overallQuantity+=summary.getTotalQuantity();
        }

        return overallQuantity;
    }

    double getOverallPrice() {
        double overallPrice = 0;
        for (DailySalesSummary summary:summaryList) {
            overallPrice+=summary.getTotalPrice();
        }

        return overallPrice;
    }

    public static void setSummaryList(List<_Transaction> transactionList, List<DailySalesSummary> summaryList) throws ParseException {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date endDate = dateFormat.parse(transactionList.get(0).getDate());
        Date startDate = dateFormat.parse(transactionList.get(transactionList.size()-1).getDate());

        // Create a Calendar instance and set it to the start date
        Calendar calendar = Calendar.getInstance();
        assert startDate != null;
        calendar.setTime(startDate);

        summaryList.clear();
        while (!calendar.getTime().after(endDate)) {
            Date currentDate = calendar.getTime();

            List<_Transaction> transactions = transactionList
                    .stream()
                    .filter(t -> {
                        try {
                            return Objects.equals(dateFormat.parse(t.getDate()), currentDate);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.toList());


            // Get summary of sales
            Map<String, DailySalesSummaryBreakdown> breakDowns = new HashMap<>();
            for (_Transaction transaction :transactions){
                for (_Product item:transaction.getItems()) {
                    if (breakDowns.containsKey(item.getProduct())) {
                        DailySalesSummaryBreakdown breakDown = breakDowns.get(item.getProduct());
                        assert breakDown != null;
                        breakDown.setQuantity(breakDown.getQuantity()+item.getQuantity());
                    }
                    else{
                        DailySalesSummaryBreakdown breakDown = new DailySalesSummaryBreakdown();
                        breakDown.setProduct(item.getProduct());
                        breakDown.setQuantity(breakDown.getQuantity()+item.getQuantity());
                        breakDown.setUnitPrice(breakDown.getUnitPrice()+item.getPrice());
                        breakDowns.put(item.getProduct(), breakDown);
                    }
                }
            }

            summaryList.add(new DailySalesSummary(
                    currentDate,
                    new ArrayList<>(breakDowns.values())
            ));

            // Incement day for next loop
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
    }


}