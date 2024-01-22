package com.projectfkklp.saristorepos.activities.transaction.transaction_daily_summary;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.projectfkklp.saristorepos.R;
import com.projectfkklp.saristorepos.activities.transaction.transaction_invoice.TransactionInvoicePage;
import com.projectfkklp.saristorepos.models.DailyTransactions;

import java.time.format.DateTimeFormatter;
import java.util.List;


public class TransactionDailySummaryAdapter extends RecyclerView.Adapter<TransactionDailySummaryViewHolder>{
    private final Context context;
    private final List<DailyTransactions> dailyTransactionsList;
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MMMM d, yyyy");
    public TransactionDailySummaryAdapter(Context context, List<DailyTransactions> dailyTransactions) {
        this.context = context;
        this.dailyTransactionsList = dailyTransactions;
    }

    @NonNull
    @Override
    public TransactionDailySummaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_daily_summary_recycler_view, parent, false);
        return new TransactionDailySummaryViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull TransactionDailySummaryViewHolder holder, int position) {
        DailyTransactions dailyTransactions = dailyTransactionsList.get(position);

        holder.summaryDateText.setText(dailyTransactions.getDate().format(dateFormat));
        holder.totalSoldItemsText.setText(String.format(
            "Total Sold Items: %,d",
            dailyTransactions.getTotalSoldItems()
        ));
        holder.totalSalesText.setText(String.format(
                "Total Sales: ₱%,.2f",
                dailyTransactions.getTotalSales()
        ));

        holder.container.setOnClickListener(view -> {
            Intent intent = new Intent(context, TransactionInvoicePage.class);
            intent.putExtra("src", "dailyTransactions");
            intent.putExtra("dailyTransactions", dailyTransactions);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return dailyTransactionsList.size();
    }
}
